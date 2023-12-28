package pl.pwr.ite.service;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class ClientBase {
    protected final DataParser dataParser = DataParser.getInstance();
    private SocketChannel clientSocket;
    private final String host;
    private final int port;

    public ClientBase(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SneakyThrows
    public void start() {
        clientSocket = SocketChannel.open(new InetSocketAddress(host, port));
    }

    @SneakyThrows
    private void restartConnection() {
        stop();
        start();
    }

    @SneakyThrows
    public void stop() {
        clientSocket.close();
    }


    @SneakyThrows
    public String send(String data) {
        var dataBuffer = ByteBuffer.wrap(data.getBytes());
        clientSocket.write(dataBuffer);
        dataBuffer = ByteBuffer.allocate(1024);
        clientSocket.read(dataBuffer);
        var responseString = dataParser.deserialize(dataBuffer);
        dataBuffer.clear();
        restartConnection();
        return responseString;
    }
}
