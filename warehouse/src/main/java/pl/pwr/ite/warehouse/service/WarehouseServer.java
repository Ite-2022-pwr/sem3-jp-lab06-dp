package pl.pwr.ite.warehouse.service;

import lombok.NoArgsConstructor;
import pl.pwr.ite.model.User;
import pl.pwr.ite.model.UserSocket;
import pl.pwr.ite.model.enums.UserRole;
import pl.pwr.ite.service.InterfaceServerBase;
import pl.pwr.ite.service.remote.WarehouseCommunicationInterface;

import java.util.UUID;

public class WarehouseServer extends InterfaceServerBase<WarehouseServer.CommunicationImpl> {
    public WarehouseServer() {
        super("localhost", 5555, CommunicationImpl.class);
    }

    public WarehouseServer(String host, int port) {
        super(host, port, CommunicationImpl.class);
    }

    @NoArgsConstructor
    public static class CommunicationImpl implements WarehouseCommunicationInterface {
        @Override
        public String test(String str) {
            var response = str + " - response.";
            return response;
        }

        @Override
        public UserSocket register(UserRole role, String host, Integer port) {
            var user = new User();
            user.setId(UUID.randomUUID());
            user.setRole(role);

            var userSocket = new UserSocket();
            userSocket.setHost(host);
            userSocket.setPort(port);
            userSocket.setUser(user);

            return userSocket;
        }
    }
}
