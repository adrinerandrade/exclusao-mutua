package service;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ServerNameConnection {
	
	private Socket socket;
	private int port;
	
	public ServerNameConnection(Socket socket, int port) {
		this.socket = socket;
		this.port = port;
	}

	public String sendRegisterMessage() throws IOException {
		PrintStream escritaCliente = new PrintStream(socket.getOutputStream());
		escritaCliente.println(port);
		Scanner leitura = new Scanner(socket.getInputStream());
		return leitura.nextLine();
	}
}
