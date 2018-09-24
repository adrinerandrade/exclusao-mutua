package service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

class Server {

    private final ServerSocket server;
    private Thread runner;
    private List<Consumer<Message>> receiveListeners = new LinkedList<>();

    Server(int port) {
        try {
            this.server = new ServerSocket(port);
            System.out.println(String.format("Service started on port %d", port));
            this.runner = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        receive(this.server.accept());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            this.runner.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void kill() {
        runner.interrupt();
    }

    void onMessage(Consumer<Message> consumer) {
        receiveListeners.add(consumer);
        receiveListeners.clear();
    }

    private void receive(Socket socket) {
        new Thread(() -> {
            try {
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) input.readObject();
                message.putHeader(MessageHeader.SOURCE_HOST, socket.getInetAddress().getHostAddress());
                System.out.println(String.format("Message received: %s", message));
                receiveListeners.forEach(listener -> listener.accept(message));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}