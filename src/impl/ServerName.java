package impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nameserver.Client;
import service.Address;
import service.ServerNameConnection;

public class ServerName {
	
	private final int SERVER_NAME_PORT = 9999; 
	private final String SERVER_NAME_HOST = "localhost";

    public ServerNameResponse notifyCreation(int port) throws IOException {
    	Socket socket = new Socket(SERVER_NAME_HOST, SERVER_NAME_PORT);
    	ServerNameConnection con = new ServerNameConnection(socket, port);
    	String response = con.sendRegisterMessage();
    	Gson gson = new Gson();
    	Type listType = new TypeToken<ArrayList<Client>>(){}.getType();
    	List<Client> clients = gson.fromJson(response, listType);
    	List<Address> allServices = clients.stream().map(client -> {
    		return new Address(client.getAddress(), client.getPort());
    	}).collect(Collectors.toList());
    	return new ServerNameResponse(allServices, new Random().nextInt());
    } 

}
