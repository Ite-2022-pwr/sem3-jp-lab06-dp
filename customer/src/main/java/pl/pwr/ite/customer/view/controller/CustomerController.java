package pl.pwr.ite.customer.view.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.pwr.ite.customer.service.CustomerServer;
import pl.pwr.ite.customer.service.exception.JavaFXException;
import pl.pwr.ite.model.Order;
import pl.pwr.ite.model.Product;
import pl.pwr.ite.model.enums.OrderStatus;
import pl.pwr.ite.model.enums.ProductStatus;
import pl.pwr.ite.model.enums.UserRole;
import pl.pwr.ite.service.javafx.CommunicationController;
import pl.pwr.ite.service.remote.client.DealerClient;
import pl.pwr.ite.service.remote.client.ProviderClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CustomerController extends CommunicationController<CustomerServer.CommunicationImpl, CustomerServer> implements Initializable {
    @FXML private Button payButton;
    @FXML private Label selectedProductsLabel;
    @FXML private TableView<Product> productTable;
    @FXML private TextField portTextField;
    @FXML private TextField hostTextField;
    @FXML private Button warehouseConnectButton;
    @FXML private TabPane mainTabPane;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableView<Product> orderProductsTable;
    @FXML private Button returnButton;

    public CustomerController() {
        super(new CustomerServer());
        CustomerServer.CommunicationImpl.setOrderReceivedCallback((order) -> Platform.runLater(() -> ordersTabClicked(null)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();
    }

    @FXML private void warehouseConnectButtonClick(ActionEvent event) {
        var host = hostTextField.getText();
        var port = Integer.valueOf(portTextField.getText());
        try {
            register(UserRole.Customer, host, port);
        } catch (Exception ex) {
            throw new JavaFXException(String.format(ex.getMessage()), ex);
        }
        portTextField.setDisable(true);
        hostTextField.setDisable(true);
        warehouseConnectButton.setDisable(true);
        mainTabPane.setVisible(true);
        offersTabClicked(null);
    }

    @FXML private void returnButtonClick(ActionEvent event) {
        var selectedItem = ordersTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            return;
        }
        var providerSocket = warehouseClient.getInfoByUserRole(UserRole.Provider);
        if(providerSocket == null) {
            throw new JavaFXException("No provider available at this moment.");
        }
        var providerClient = new ProviderClient(providerSocket.getHost(), providerSocket.getPort());
        providerClient.connect();
        providerClient.returnOrder(selectedItem);
        providerClient.disconnect();
        returnButton.setDisable(true);
        payButton.setDisable(true);
        ordersTabClicked(null);
    }

    @FXML private void orderTableCellClicked(Event event) {
        var selectedItem = ordersTable.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            orderProductsTable.getItems().clear();
            orderProductsTable.getItems().addAll(selectedItem.getProducts());
            if(OrderStatus.Paid.equals(selectedItem.getStatus())) {
                returnButton.setDisable(true);
                payButton.setDisable(true);
            } else if(OrderStatus.Delivered.equals(selectedItem.getStatus())) {
                returnButton.setDisable(false);
                payButton.setDisable(false);
            } else if(OrderStatus.Ordered.equals(selectedItem.getStatus())) {
                returnButton.setDisable(false);
                payButton.setDisable(true);
            }
        }
    }

    @FXML private void offersTabClicked(Event event) {
//        if(warehouseClient.isConnected()) {
        try {
            productTable.getItems().clear();
            productTable.getItems().addAll(warehouseClient.getOffer());
        } catch (Exception ex) {
            throw new JavaFXException("Error fetching data.", ex);
        }
//        }
    }

    @FXML private void ordersTabClicked(Event event) {
//        if(warehouseClient.isConnected()) {
        try {
            ordersTable.getItems().clear();
            orderProductsTable.getItems().clear();
            var orders = warehouseClient.getOrders(applicationContext.getRegisteredUser().getId());
            ordersTable.getItems().addAll(orders);
        } catch (Exception ex) {
            throw new JavaFXException("Error fetching data.", ex);
        }
//        }
    }

    @FXML private void placeOrderButtonClick(ActionEvent event) {
        var productIds = productTable.getSelectionModel().getSelectedItems().stream().map(Product::getId).collect(Collectors.toList());
        warehouseClient.putOrder(applicationContext.getRegisteredUser().getId(), new ArrayList<>(productIds));
        offersTabClicked(null);
        tableCellSelected(null);
    }

    @FXML private void tableCellSelected(Event event) {
        selectedProductsLabel.setText("Selected products: " + productTable.getSelectionModel().getSelectedItems().size());
    }


    @FXML private void payButtonClick(ActionEvent event) {
        var selectedItem = ordersTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            return;
        }
        var dealerSocket = warehouseClient.getInfoByUserRole(UserRole.Dealer);
        if(dealerSocket == null) {
            throw new JavaFXException("No dealer available at this moment.");
        }
        var dealerClient = new DealerClient(dealerSocket.getHost(), dealerSocket.getPort());
        dealerClient.connect();
        dealerClient.acceptOrder(selectedItem);
        dealerClient.disconnect();
        returnButton.setDisable(true);
        payButton.setDisable(true);
        ordersTabClicked(null);
    }

    @FXML private void buyProductButtonClick(ActionEvent event) {
        var order = ordersTable.getSelectionModel().getSelectedItem();
        if(order == null) {
            return;
        }
        var product = orderProductsTable.getSelectionModel().getSelectedItem();
        if(product == null) {
            return;
        }
        var orderProduct = order.getProducts().stream().filter(p -> p.getId().equals(product.getId())).findFirst().orElse(null);
        orderProduct.setStatus(ProductStatus.Buy);
        orderTableCellClicked(null);
    }

    @FXML private void returnProductButtonClick(ActionEvent event) {
        var order = ordersTable.getSelectionModel().getSelectedItem();
        if(order == null) {
            return;
        }
        var product = orderProductsTable.getSelectionModel().getSelectedItem();
        if(product == null) {
            return;
        }
        var orderProduct = order.getProducts().stream().filter(p -> p.getId().equals(product.getId())).findFirst().orElse(null);
        orderProduct.setStatus(ProductStatus.Return);
        orderTableCellClicked(null);
    }

    private void setupTables() {
        mainTabPane.setVisible(false);

        //PRODUCTS TABLE
        var productIdColumn = new TableColumn<Product, String>("ID");
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        var productNameColumn = new TableColumn<Product, String>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productTable.getColumns().clear();
        productTable.getColumns().addAll(productNameColumn, productIdColumn);

        //ORDERS TABLE
        var orderIdColumn = new TableColumn<Order, String>("ID");
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        var orderStatusColumn = new TableColumn<Order, String>("Status");
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        var orderAmountColumn = new TableColumn<Order, Integer>("Products");
        orderAmountColumn.setCellValueFactory(new PropertyValueFactory<>("productAmount"));
        ordersTable.getColumns().clear();
        ordersTable.getColumns().addAll(orderIdColumn, orderStatusColumn, orderAmountColumn);

        //ORDER PRODUCTS TABLE
        var orderProductsIdColumn = new TableColumn<Product, String>("ID");
        orderProductsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        var orderProductsNameColumn = new TableColumn<Product, String>("Name");
        orderProductsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        var orderProductsStatusColumn = new TableColumn<Product, String>("Status");
        orderProductsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderProductsTable.getColumns().clear();
        orderProductsTable.getColumns().addAll(orderProductsNameColumn, orderProductsIdColumn, orderProductsStatusColumn);

        productTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}