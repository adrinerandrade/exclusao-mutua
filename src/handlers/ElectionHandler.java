package handlers;

import impl.ApplicationService;
import service.ActionHandler;
import service.Payload;
import service.RequestHandler;

import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "election")
public class ElectionHandler implements ActionHandler {

    private final ApplicationService application;

    public ElectionHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Payload payload) {
        return null;
    }

}
