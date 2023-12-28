package pl.pwr.ite.service.remote;

import pl.pwr.ite.model.Product;
import pl.pwr.ite.model.UserSocket;
import pl.pwr.ite.model.enums.UserRole;

import java.util.List;
import java.util.UUID;

public interface WarehouseCommunicationInterface extends CommunicationInterface {
    String test(String str);

    UserSocket register(UserRole role, String host, Integer port);
}
