package handlers;

import impl.ApplicationService;
import impl.PayloadKeys;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.RequestHandler;

import java.util.LinkedList;
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
            pids.add(application.getInfoModule().getPid());
            continueElection(payload);
        } else {

        }
        return null;
    }

    private void continueElection(Payload payload) {
        application.getInfoModule().getSuccessor()
                .ifPresent(successor -> application.request(successor.getAddress(), ElectionHandler.class, payload)
                        .onTimeout(() -> continueElection(payload)));
    }

    private void beginCoordinatorAction() {
        application.getInfoModule().getSuccessor()
                .ifPresent(successor -> application.request(successor.getAddress(), ElectionHandler.class, payload))
    }

    private boolean isRingComplete(LinkedList<Integer> pids) {
        return pids.contains(application.getInfoModule().getPid());
    }

}
