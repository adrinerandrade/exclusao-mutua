package handlers;

import impl.ApplicationService;
import impl.PayloadKeys;
import service.*;

import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "request_resource")
public class RequestResourceHandler implements ActionHandler {

    private final ApplicationService application;

    public RequestResourceHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
        ResourceStatus resourceStatus = application.getResourceModule().resourceRequested(sourceAddress);
        Payload response = new Payload();
        response.put(PayloadKeys.RESOURCE_STATUS.name(), resourceStatus);
        return CompletableFuture.completedFuture(response);
    }

}
