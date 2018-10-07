package handlers;

import impl.ApplicationService;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;

import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "resource_available")
public class NotifyResourceAvailableHandler implements ActionHandler {

    private final ApplicationService application;

    public NotifyResourceAvailableHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
        application.getResourceModule().consumeResource();
        return null;
    }

}
