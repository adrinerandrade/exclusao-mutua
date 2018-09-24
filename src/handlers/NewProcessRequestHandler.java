package handlers;

import impl.ApplicationService;
import service.ActionHandler;
import service.Payload;
import service.RequestHandler;

import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "new_process_started")
public class NewProcessRequestHandler implements ActionHandler {

    private final ApplicationService application;

    public NewProcessRequestHandler(ApplicationService serviceContext) {
        this.application = serviceContext;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Payload payload) {
        return null;
    }

}
