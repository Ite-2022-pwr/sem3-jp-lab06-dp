package pl.pwr.ite.service;

import pl.pwr.ite.model.Order;
import pl.pwr.ite.model.remote.Payload;
import pl.pwr.ite.service.remote.CommunicationInterface;

import java.util.ArrayList;
import java.util.UUID;

public abstract class InterfaceServerBase<I extends CommunicationInterface> extends ServerBase {

    private final Class<I> communicationInterfaceType;

    protected InterfaceServerBase(Class<I> communicationInterfaceType) {
        this.communicationInterfaceType = communicationInterfaceType;
    }

    @Override
    protected Object process(String data) {
        var payload = dataParser.deserialize(data, Payload.class);
        var methodName = payload.getMethod().getMethodName();
        var methodArguments = payload.getMethod().getArguments();

        var argumentTypes = new ArrayList<Class<?>>();
        var arguments = new ArrayList<>();
        for(var methodArgument : methodArguments) {
            var type = methodArgument.getType();
            var value = dataParser.deserialize(methodArgument.getValue(), methodArgument.getType());
            argumentTypes.add(type);
            arguments.add(value);
        }
        try {
            var _instance = Class.forName(communicationInterfaceType.getName()).getDeclaredConstructor().newInstance();
            var method = _instance.getClass().getDeclaredMethod(methodName, argumentTypes.toArray(new Class<?>[0]));
            if(!Void.TYPE.equals(method.getReturnType())) {
                var methodResult = method.invoke(_instance, arguments.toArray());
                return methodResult;
            }
            method.invoke(_instance, arguments.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Object parseValue(Class type, Object value) {
        if(type.equals(UUID.class)) {
            return UUID.fromString((String) value);
        }
        if(type.isEnum()) {
            return Enum.valueOf(type, (String) value);
        }
        if(!type.equals(String.class) && value instanceof String str) {
            return dataParser.deserialize(str, type);
        }
        return value;
    }
}