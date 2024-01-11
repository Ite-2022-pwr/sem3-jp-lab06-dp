module service {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires model;
    requires java.rmi;
    exports pl.pwr.ite.service;
    exports pl.pwr.ite.service.remote;
    exports pl.pwr.ite.service.remote.client;
    exports pl.pwr.ite.service.javafx;
}
