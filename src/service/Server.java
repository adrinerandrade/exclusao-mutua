package service;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

class Server {

    private final int port;
    private final ServerSocket server;
    private Thread runner;
    private List<Consumer<Message>> receiveListeners = new LinkedList<>();

    Server(int port) {
        this.port = port;
        try {
            this.server = new ServerSocket(port);
            System.out.println(String.format("Service started on port %d", port));
            this.runner = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        receive(this.server.accept());
                    } catch (IOException e) {
                        if (!"socket closed".equals(e.getMessage())) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            this.runner.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void kill() {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        runner.interrupt();
        receiveListeners.clear();
        System.out.println("Service stopped!");
    }

    void onMessage(Consumer<Message> consumer) {
        receiveListeners.add(consumer);
    }

    private void receive(Socket socket) {
        String hostAddress = socket.getInetAddress().getHostAddress();
        new Thread(() -> {
            try {
                ObjectInputStream input = getObjectInputStream(socket);
                if (input == null) {
                    return;
                }
                Message message = (Message) input.readObject();
                message.putHeader(MessageHeader.SOURCE_HOST, hostAddress);
                System.out.println(String.format("Message received: %s", message));
                receiveListeners.forEach(listener -> listener.accept(message));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(String.format("Erro no servidor de porta: %s", this.port), e);
            }
        }).start();
    }

    private ObjectInputStream getObjectInputStream(Socket socket) throws IOException {
        try {
            return new ObjectInputStream(socket.getInputStream());
        } catch (EOFException e) {
            return null;
        }
    }

}