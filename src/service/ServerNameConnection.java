package service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import nameserver.ClientRequestMessage;
import nameserver.ClientRequestType;

public class ServerNameConnection {
	
	private Socket socket;
	private int port;
	
	public ServerNameConnection(Socket socket, int port) {
		this.socket = socket;
		this.port = port;
	}
	
	public String sendRegisterMessage() throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		ClientRequestMessage clientRequest = new ClientRequestMessage();
		clientRequest.setPort(port);
		clientRequest.setType(ClientRequestType.REGISTER);
		out.writeObject(clientRequest);
		Scanner leitura = new Scanner(socket.getInputStream());
		return leitura.nextLine();
	}
}
