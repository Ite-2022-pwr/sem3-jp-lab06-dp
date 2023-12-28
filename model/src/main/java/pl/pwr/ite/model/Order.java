package pl.pwr.ite.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Order {

    private UUID id;

    private User user;

    private List<Product> products = new ArrayList<>();
}

