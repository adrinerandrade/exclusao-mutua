package nameserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    static HashSet<Client> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(8873);

        System.out.println("Server is online on " + server.getInetAddress().getHostName() + " on port: "
                + server.getLocalPort());

        Thread listener = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = server.accept();

                    Scanner in;
                    in = new Scanner(socket.getInputStream());
                    String port = in.nextLine();
                    Client c = new Client(Integer.parseInt(port), socket.getInetAddress().getHostAddress(),
                            socket);
                    clients.add(c);
                    String response = clients.stream().filter(cli -> {
                        InetSocketAddress address = new InetSocketAddress(cli.getAddress(), cli.getPort());
                        try (Socket client = new Socket()) {
                            client.connect(address);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }).map(Client::toString).collect(Collectors.joining(", "));

                    PrintStream escritaCliente = new PrintStream(socket.getOutputStream());
                    escritaCliente.println("[ " + response + "]");
                    System.out.println("Client registered: " + c.getAddress() + " on port:" + c.getPort());
                }
            } catch (IOException e) {
                if (!"socket closed".equals(e.getMessage())) {
                    throw new RuntimeException(e);
                }
            }
        });
        listener.start();

        Scanner read = new Scanner(System.in);
        while (!"quit".equals(read.nextLine())) ;

        listener.interrupt();
        server.close();
    }
}
