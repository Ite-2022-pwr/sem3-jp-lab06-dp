package pl.pwr.ite.warehouse.service;

import pl.pwr.ite.model.Order;
import pl.pwr.ite.model.Product;
import pl.pwr.ite.service.RepositoryBase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductRepository extends RepositoryBase<Product> {
    private static ProductRepository INSTANCE = null;

    public ProductRepository() {
        var p1 = new Product();
        p1.setName("P1");
        var p2 = new Product();
        p2.setName("P2");
        var p3 = new Product();
        p3.setName("P3");
        add(p1);
        add(p2);
        add(p3);
    }

    public List<Product> getAllByProductIds(List<UUID> productIds) {
        return entities.stream()
                .filter(e -> !e.isOrdered() && productIds.contains(e.getId().toString()))
                .collect(Collectors.toList());
    }

    public static ProductRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductRepository();
        }
        return INSTANCE;
    }
}
