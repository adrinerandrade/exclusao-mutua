package handlers;

import impl.ApplicationService;
import impl.PayloadKeys;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "coordinator")
public class CoordinatorHandler implements ActionHandler {

    private final ApplicationService application;

    public CoordinatorHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
        LinkedList<Integer> pids = payload.get(PayloadKeys.PIDS.name());
        application.getCoordinatorModule().updateCoordinator(pids);
        if (!isRingComplete(pids)) {
            continueCoordinatorCycle(pids);
        }
        return null;
    }

    private void continueCoordinatorCycle(LinkedList<Integer> pids) {
        Payload payload = new Payload();
        payload.put(PayloadKeys.PIDS.name(), pids);
        application.getInfoModule()
                .getSuccessor()
                .ifPresent(successor -> application.request(successor.getAddress(), CoordinatorHandler.class, payload)
                    .onTimeout(() -> continueCoordinatorCycle(pids)));
    }

    private boolean isRingComplete(LinkedList<Integer> pids) {
        return application.getInfoModule().getPid() == pids.getFirst();
    }

}
