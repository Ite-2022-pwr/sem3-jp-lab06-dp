package pl.pwr.ite.service;

import pl.pwr.ite.model.remote.Payload;
import pl.pwr.ite.service.remote.CommunicationInterface;

import java.util.ArrayList;
import java.util.UUID;

public abstract class InterfaceServerBase<I extends CommunicationInterface> extends ServerBase {

    private final Class<I> communicationInterfaceType;

    protected InterfaceServerBase(String host, int port, Class<I> communicationInterfaceType) {
        super(host, port);
        this.communicationInterfaceType = communicationInterfaceType;
    }

    @Override
    protected void process(String data) {
        var payload = dataParser.deserialize(data, Payload.class);
        var methodName = payload.getMethod().getMethodName();
        var methodArguments = payload.getMethod().getArguments();

        var argumentTypes = new ArrayList<Class<?>>();
        var arguments = new ArrayList<>();
        for(var methodArgument : methodArguments) {
            var type = methodArgument.getType();
            var value = methodArgument.getValue();
            argumentTypes.add(type);
            arguments.add(parseValue(type, value));
        }
        try {
            var _instance = Class.forName(communicationInterfaceType.getName()).getDeclaredConstructor().newInstance();
            var method = _instance.getClass().getDeclaredMethod(methodName, argumentTypes.toArray(new Class<?>[0]));
            method.invoke(_instance, arguments.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object parseValue(Class<?> type, Object value) {
        if(type.equals(UUID.class)) {
            return UUID.fromString((String) value);
        }
        return value;
    }
}