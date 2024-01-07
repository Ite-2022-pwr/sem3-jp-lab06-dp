package pl.pwr.ite.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Product extends EntityBase {

    private String name;

    private boolean ordered = false;
}
