package pl.pwr.ite.customer.service;

import lombok.NoArgsConstructor;
import pl.pwr.ite.model.UserSocket;
import pl.pwr.ite.model.enums.UserRole;
import pl.pwr.ite.service.InterfaceClientBase;
import pl.pwr.ite.service.remote.WarehouseCommunicationInterface;

public class WarehouseClient extends InterfaceClientBase<WarehouseCommunicationInterface> implements WarehouseCommunicationInterface {

    public WarehouseClient() {
        super("localhost", 5555, WarehouseCommunicationInterface.class);
    }

    @Override
    public String test(String str) {
        return (String) send("test", str);
    }

    @Override
    public UserSocket register(UserRole role, String host, Integer port) {
        return (UserSocket) send("register", role, host, port);
    }
}