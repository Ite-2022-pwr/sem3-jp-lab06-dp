package pl.pwr.ite.model;

import lombok.Getter;
import lombok.Setter;
import pl.pwr.ite.model.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Order extends EntityBase {

    private User user;

    private OrderStatus status;

    private Integer productAmount;

    private List<Product> products = new ArrayList<>();
}

