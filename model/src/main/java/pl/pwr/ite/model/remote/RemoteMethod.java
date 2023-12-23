package pl.pwr.ite.model.remote;

import lombok.Data;

import java.util.List;

@Data
public class RemoteMethod {
    private String methodName;
    private List<MethodArgument> arguments;
}
