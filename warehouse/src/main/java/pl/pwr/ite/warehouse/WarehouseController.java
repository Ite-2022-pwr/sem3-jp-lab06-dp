package pl.pwr.ite.warehouse;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pl.pwr.ite.warehouse.service.WarehouseServer;

import java.io.IOException;

public class WarehouseController {

    @FXML private Label welcomeText;
    @FXML private Button startButton;
    private boolean serverRunning = false;
    private final WarehouseServer server;

    public WarehouseController() {
        this.server = new WarehouseServer();
    }

    @FXML
    protected void onHelloButtonClick() throws IOException {
        if(!serverRunning) {
            server.start();
            serverRunning = true;
            startButton.setDisable(true);
            welcomeText.setText("Server is on.");
        } else {
            server.stop();
            serverRunning = false;
            startButton.setDisable(false);
            welcomeText.setText("Server is off.");
        }
    }
}