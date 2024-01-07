package pl.pwr.ite.provider.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.pwr.ite.model.Order;
import pl.pwr.ite.model.Product;
import pl.pwr.ite.model.enums.OrderStatus;
import pl.pwr.ite.model.enums.UserRole;
import pl.pwr.ite.provider.service.ProviderServer;
import pl.pwr.ite.provider.service.exception.JavaFXException;
import pl.pwr.ite.service.javafx.CommunicationController;
import pl.pwr.ite.service.remote.client.CustomerClient;

import java.net.URL;
import java.util.ResourceBundle;

public class ProviderController extends CommunicationController<ProviderServer.CommunicationImpl, ProviderServer> implements Initializable {
    @FXML private TextField hostTextField;
    @FXML private TextField portTextField;
    @FXML private Button warehouseConnectButton;
    @FXML private Label orderIdLabel;
    @FXML private Label userIdLabel;
    @FXML private TableView<Product> productsTable;
    @FXML private Button fillOrderButtonClick;

    private Order currentOrder;

    public ProviderController() {
        super(new ProviderServer());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
    }

    @FXML private void warehouseConnectButtonClick(ActionEvent event) {
        var host = hostTextField.getText();
        var port = Integer.valueOf(portTextField.getText());
        try {
            warehouseClient.connect();
            register(UserRole.Provider, host, port);
        } catch (Exception ex) {
            throw new JavaFXException(String.format(ex.getMessage()));
        }
        portTextField.setDisable(true);
        hostTextField.setDisable(true);
        warehouseConnectButton.setDisable(true);
        ProviderServer.CommunicationImpl.setWarehouseClient(warehouseClient);
    }

    @FXML private void fetchOrderButtonClick(ActionEvent event) {
        var order = warehouseClient.getOrder();
        if(order == null) {
            currentOrder = null;
            return;
        }
        this.currentOrder = order;
        orderIdLabel.setText("Order ID: " + order.getId());
        userIdLabel.setText("User ID: " + order.getUser().getId());
        productsTable.getItems().clear();
        productsTable.getItems().addAll(order.getProducts());
        fillOrderButtonClick.setDisable(false);
    }

    @FXML private void fillOrderButtonClick(ActionEvent event) {
        var user = currentOrder.getUser();
        var userSocket = warehouseClient.getInfoByUserId(user.getId());
        var customerClient = new CustomerClient(userSocket.getHost(), userSocket.getPort());
        currentOrder.setStatus(OrderStatus.Delivered);
        warehouseClient.updateOrder(currentOrder);
        customerClient.connect();
        customerClient.putOrder(currentOrder);
        customerClient.disconnect();
        this.currentOrder = null;
        orderIdLabel.setText("Order ID: ");
        userIdLabel.setText("User ID: ");
        productsTable.getItems().clear();
        fillOrderButtonClick.setDisable(true);
    }

    private void setupTable() {
        var nameColumn = new TableColumn<Product, String>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        var idColumn = new TableColumn<Product, String>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productsTable.getColumns().addAll(nameColumn, idColumn);
    }

}