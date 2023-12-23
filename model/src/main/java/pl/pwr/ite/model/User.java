package pl.pwr.ite.model;

import lombok.Data;
import pl.pwr.ite.model.enums.UserRole;

import java.util.UUID;

@Data
public class User {
    private UUID id;
    private UserRole role;
}
