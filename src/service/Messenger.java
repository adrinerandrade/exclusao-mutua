package service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

final class Messenger {

    private final Server server;
    private final ClientMessenger clientMessenger;
    private final List<Consumer<Message>> messageListeners = new LinkedList<>();

    Messenger(int port) {
        this.server = new Server(port);
        this.server.onMessage(this::onMessageReceived);
        this.clientMessenger = new ClientMessenger(port);
    }

    void send(Message message, Address address) {
        message.putHeader(MessageHeader.TARGET_HOST, address.getHost());
        message.putHeader(MessageHeader.TARGET_PORT, address.getPort());
        this.clientMessenger.send(message);
    }

    void onMessage(Consumer<Message> listener) {
        messageListeners.add(listener);
    }

    private void onMessageReceived(Message message) {
        messageListeners.forEach(consumer -> consumer.accept(message));
    }

    void close() {
        this.server.kill();
    }

}
