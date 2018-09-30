package handlers;

import impl.ApplicationService;
import impl.ExternalService;
import impl.PayloadKeys;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;

import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "get_info")
public class GetInfoRequestHandler implements ActionHandler {

    private final ApplicationService application;

    public GetInfoRequestHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
        int sourcePid = payload.get(PayloadKeys.PID.name());
        application.getInfoModule().newService(new ExternalService(sourceAddress, sourcePid));

        Payload response = new Payload();
        response.put(PayloadKeys.PID.name(), application.getInfoModule().getPid());
        response.put(PayloadKeys.IS_COORDINATOR.name(), application.getCoordinatorModule().getCoordinatorAddress());
        return CompletableFuture.completedFuture(response);
    }

}
