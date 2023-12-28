package pl.pwr.ite.service;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class ServerBase {

    protected final DataParser dataParser = DataParser.getInstance();
    protected ByteBuffer dataBuffer = ByteBuffer.allocate(2056);
    private boolean running = false;
    private final String host;
    private final int port;
    private Thread serverThread;

    protected ServerBase(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        this.serverThread = new Thread(this::startInner);
        serverThread.start();
    }

    @SneakyThrows
    private void startInner() {
        var selector = Selector.open();
        var serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(host, port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        running = true;

        while (running) {
            selector.select();
            var selectedKeys = selector.selectedKeys();
            var iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                if(key.isAcceptable()) {
                    registerClient(selector, serverSocket);
                }
                if(key.isReadable()) {
                    process(key);
                }
                iterator.remove();
            }
        }
        serverSocket.close();
        serverThread.interrupt();
    }

    public void stop() {
        this.running = false;
    }

    @SneakyThrows
    private void process(SelectionKey key) {
        try(var channel = (SocketChannel) key.channel()) {
            int readBytes = channel.read(dataBuffer);
            if(readBytes == -1) {
                closeConnection(key, channel);
            }
            dataBuffer.flip();
            var deserializedData = dataParser.deserialize(dataBuffer);
            var result = process(deserializedData);
            String serializedData;
            if(result instanceof String str) {
                serializedData = str;
            } else {
                serializedData = dataParser.serialize(result);
            }
            dataBuffer.clear();
            dataBuffer = ByteBuffer.wrap(serializedData.getBytes());
            channel.write(dataBuffer);
            dataBuffer = ByteBuffer.allocate(2056);
        }
    }

    @SneakyThrows
    private void closeConnection(SelectionKey key, SocketChannel channel) {
        key.cancel();
        channel.close();
        System.out.println("Client disconnected.");
    }

    @SneakyThrows
    private void registerClient(Selector selector, ServerSocketChannel serverSocket) {
        var client = serverSocket.accept();
        if(client == null) {
            return;
        }
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    protected abstract Object process(String data);
}
