package service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

class ClientMessenger {

    private final int port;

    ClientMessenger(int port) {
        this.port = port;
    }

    void send(Message message) {
        message.putHeader(MessageHeader.SOURCE_PORT, this.port);

        String host = message.getHeader(MessageHeader.TARGET_HOST);
        Integer port = message.getHeader(MessageHeader.TARGET_PORT);
        System.out.println(String.format("Message sent: %s", message));
        try (Socket cliente = new Socket(host, port)) {
            PrintStream escrita = new PrintStream(cliente.getOutputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(escrita);
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
