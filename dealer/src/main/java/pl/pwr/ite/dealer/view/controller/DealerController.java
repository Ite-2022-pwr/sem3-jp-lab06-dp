package pl.pwr.ite.dealer.view.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.pwr.ite.dealer.service.DealerServer;
import pl.pwr.ite.dealer.service.exception.JavaFXException;
import pl.pwr.ite.model.enums.UserRole;
import pl.pwr.ite.service.InterfaceServerBase;
import pl.pwr.ite.service.javafx.CommunicationController;

import java.net.URL;
import java.util.ResourceBundle;

public class DealerController extends CommunicationController<DealerServer.CommunicationImpl, DealerServer> {
    @FXML private TextField portTextField;
    @FXML private TextField hostTextField;
    @FXML private Button warehouseConnectButton;

    public DealerController() {
        super(new DealerServer());
    }

    @FXML private void warehouseConnectButtonClick(ActionEvent event) {
        var host = hostTextField.getText();
        var port = Integer.valueOf(portTextField.getText());
        try {
            warehouseClient.connect();
            register(UserRole.Dealer, host, port);
        } catch (Exception ex) {
            throw new JavaFXException(String.format(ex.getMessage()), ex);
        }
        portTextField.setDisable(true);
        hostTextField.setDisable(true);
        warehouseConnectButton.setDisable(true);
        DealerServer.CommunicationImpl.setWarehouseClient(warehouseClient);
    }
}