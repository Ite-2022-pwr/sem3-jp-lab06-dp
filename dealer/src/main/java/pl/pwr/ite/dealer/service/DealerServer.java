package pl.pwr.ite.dealer.service;

import pl.pwr.ite.model.Order;
import pl.pwr.ite.model.enums.OrderStatus;
import pl.pwr.ite.service.InterfaceServerBase;
import pl.pwr.ite.service.remote.DealerCommunicationInterface;
import pl.pwr.ite.service.remote.client.WarehouseClient;

public class DealerServer extends InterfaceServerBase<DealerServer.CommunicationImpl> {

    public DealerServer() {
        super(CommunicationImpl.class);
    }

    public static class CommunicationImpl implements DealerCommunicationInterface {

        private static WarehouseClient warehouseClient;

        @Override
        public void acceptOrder(Order order) {
            order.setStatus(OrderStatus.Paid);
            warehouseClient.updateOrder(order);
        }

        public static void setWarehouseClient(WarehouseClient warehouseClient) {
            CommunicationImpl.warehouseClient = warehouseClient;
        }
    }
}
