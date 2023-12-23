package pl.pwr.ite.warehouse.service;

import lombok.NoArgsConstructor;
import pl.pwr.ite.service.InterfaceServerBase;
import pl.pwr.ite.service.remote.WarehouseCommunicationInterface;

public class WarehouseServer extends InterfaceServerBase<WarehouseServer.Test> {
    public WarehouseServer() {
        super("localhost", 5555, Test.class);
    }

    @NoArgsConstructor
    public static class Test implements WarehouseCommunicationInterface {

    }
}
