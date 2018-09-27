package handlers;

import impl.ApplicationService;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;

import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "coordinator_action")
public class CoordinatorActionHandler implements ActionHandler {

    private final ApplicationService application;

    public CoordinatorActionHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
        return null;
    }

}
