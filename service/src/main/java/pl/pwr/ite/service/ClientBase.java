package pl.pwr.ite.service;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class ClientBase {
    private final DataParser dataParser = DataParser.getInstance();
    private SocketChannel clientSocket;
    private ByteBuffer dataBuffer = ByteBuffer.allocate(2056);
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
    public void send(String data) {
        dataBuffer = ByteBuffer.wrap(data.getBytes());
        clientSocket.write(dataBuffer);
        dataBuffer.clear();
        restartConnection();
    }
}
