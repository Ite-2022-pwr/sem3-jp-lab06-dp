package pl.pwr.ite.customer.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Data;
import pl.pwr.ite.customer.service.WarehouseClient;
import pl.pwr.ite.model.enums.UserRole;
import pl.pwr.ite.model.remote.MethodArgument;
import pl.pwr.ite.model.remote.Payload;
import pl.pwr.ite.model.remote.RemoteMethod;
import pl.pwr.ite.service.ClientBase;
import pl.pwr.ite.service.DataParser;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

public class CustomerController {
    private final DataParser dataParser = DataParser.getInstance();


    @FXML
    private Label welcomeText;

    @FXML protected Button connectButton;

    private boolean clientRunning;

    private final WarehouseClient client;

    public CustomerController() {
        this.client = new WarehouseClient();
    }

    @FXML
    protected void onHelloButtonClick(ActionEvent e) {
        if(!clientRunning) {
            client.start();
            clientRunning = true;
            connectButton.setDisable(true);
            welcomeText.setText("Client connected.");
        } else {
            client.stop();
            clientRunning = false;
            connectButton.setDisable(false);
            welcomeText.setText("Client disconnected.");
        }
    }

    @FXML
    protected void sendButtonClick(ActionEvent e) {
        var clientSocket = client.register(UserRole.Customer, "localhost", 2137);
        var user = clientSocket.getUser();
        System.out.println(user.getId());
    }
}