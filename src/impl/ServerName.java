package impl;

import com.google.gson.Gson;

import java.net.Socket;

public class ServerName {
	
	private final int SERVER_NAME_PORT = 8873; 
	private final String SERVER_NAME_HOST = "localhost";

    public ServerNameResponse notifyCreation(int port) {
    	try(Socket socket = new Socket(SERVER_NAME_HOST, SERVER_NAME_PORT)) {
    		ServerNameConnection con = new ServerNameConnection(socket, port);
    		String response = con.sendRegisterMessage();
    		Gson gson = new Gson();
    		return gson.fromJson(response, ServerNameResponse.class);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
		}
    	
    } 

}
