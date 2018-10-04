package nameserver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
	static HashSet<Client> clients = new HashSet<>();
	public static void main(String[] args) throws IOException {
			
		ServerSocket server = new ServerSocket(8870);
		try {
			System.out.println("Server is online on " + server.getInetAddress().getHostName() + " on port: "
					+ server.getLocalPort());
			while (true) {
				Socket socket = server.accept();
				
				new Thread() {
					public void run() {
						ObjectInputStream in;
						try {
							in = new ObjectInputStream(socket.getInputStream());
							ClientRequestMessage clientRequest;
							while ((clientRequest = (ClientRequestMessage) in.readObject()) != null) {
								if(clientRequest.getType() == ClientRequestType.REGISTER) {
									Client c = new Client(clientRequest.getPort(), socket.getInetAddress().getHostAddress(), socket);
									clients.add(c);
									
									String response = clients.stream().filter(client -> Optional.ofNullable(client.getSocket())
											.map(Socket::isConnected).orElse(false)).map(el -> el.toString()).collect(Collectors.joining(", "));
									PrintStream escritaCliente = new PrintStream(socket.getOutputStream());
									escritaCliente.println("[ "+ response + "]");
									System.out.println("Client registered: " + socket.getInetAddress().getHostAddress());
								}
							}						
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
						}						
					}; 
				}.start();
			}
		} catch (IOException  e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
	}
}
