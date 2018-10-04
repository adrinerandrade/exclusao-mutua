package nameserver;

import java.io.Serializable;

public class ClientRequestMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private int port;
	private ClientRequestType type;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ClientRequestType getType() {
		return type;
	}

	public void setType(ClientRequestType type) {
		this.type = type;
	}
}
