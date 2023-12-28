package pl.pwr.ite.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserSocket {

    private UUID id;

    private User user;

    private String host;

    private Integer port;
}

