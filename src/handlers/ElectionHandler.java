package handlers;

import impl.ApplicationService;
import impl.PayloadKeys;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequestHandler(action = "election")
public class ElectionHandler implements ActionHandler {

    private final ApplicationService application;

    public ElectionHandler(ApplicationService application) {
        this.application = application;
    }

    @Override
    public CompletableFuture<Payload> onRequest(Address sourceAddress, Payload payload) {
        LinkedList<Integer> pids = payload.get(PayloadKeys.PIDS.name());
        if (!isRingComplete(pids)) {
            if (!application.getCoordinatorModule().isOnElection()) {
                application.getCoordinatorModule().setOnElection(true);
                pids.add(application.getInfoModule().getPid());
                continueElection(payload);
            }
        } else {
            beginCoordinatorCycle(pids);
        }
        return null;
    }

    private void continueElection(Payload payload) {
        application.getInfoModule().getSuccessor()
                .ifPresent(successor -> application.request(successor.getAddress(), ElectionHandler.class, payload)
                        .onTimeout(() -> continueElection(payload)));
    }

    private void beginCoordinatorCycle(List<Integer> pids) {
        Payload payload = new Payload();
        payload.put(PayloadKeys.PIDS.name(), pids);
        application.getInfoModule().getSuccessor()
                .ifPresent(successor -> application.request(successor.getAddress(), CoordinatorHandler.class, payload)
                        .onTimeout(() -> beginCoordinatorCycle(pids)));
    }

    private boolean isRingComplete(LinkedList<Integer> pids) {
        return pids.contains(application.getInfoModule().getPid());
    }

}
