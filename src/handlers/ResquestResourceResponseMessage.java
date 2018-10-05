package handlers;

import java.util.concurrent.CompletableFuture;

import impl.ApplicationService;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;
import service.ResourceStatus;

@RequestHandler(action = "request_resource_response")
public class ResquestResourceResponseMessage implements ActionHandler {

	private final ApplicationService application;

	public ResquestResourceResponseMessage(ApplicationService application) {
		this.application = application;
	}

	@Override
	public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
		ResourceStatus status = (ResourceStatus) payload.get("status");
		switch (status) {
		case AVAILABLE:
			// Recurso dispon�vel para ser utilizado
			break;
		case IN_QUEUE:
			// Recurso na fila aguardando libera��o
			break;
		case RELEASED:
			// Endere�o terminou de usar o recurso
			break;
		}
		return null;
	}

}
