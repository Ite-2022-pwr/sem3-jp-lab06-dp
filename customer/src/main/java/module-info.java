module pl.pwr.ite.customer {
    requires javafx.controls;
    requires javafx.fxml;
    requires service;
    requires lombok;
    requires model;


    opens pl.pwr.ite.customer to javafx.fxml;
    exports pl.pwr.ite.customer;
    exports pl.pwr.ite.customer.service;
    exports pl.pwr.ite.customer.view.controller;
    opens pl.pwr.ite.customer.view.controller to javafx.fxml;
}