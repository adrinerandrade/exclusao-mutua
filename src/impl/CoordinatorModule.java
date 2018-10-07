package impl;

import handlers.ElectionHandler;
import service.ActionHandler;
import service.Address;
import service.Payload;
import service.Request;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CoordinatorModule {

    private boolean onElection;
    private ApplicationService service;
    private Address coordinatorAddress;

    public CoordinatorModule(ApplicationService service) {
        this.service = service;
    }

    public void setCoordinatorAddress(Address coordinatorAddress) {
        this.coordinatorAddress = coordinatorAddress;
    }

    public boolean isCoordinator() {
        return coordinatorAddress == null;
    }

    public void makeCoordinator() {
        this.coordinatorAddress = null;
        System.out.println(String.format("Serviço de pid %d atribuido como Coordenador.",  service.getInfoModule().getPid()));
    }

    public void updateCoordinator(List<Integer> pids) {
        if (pids.isEmpty()) {
            makeCoordinator();
            return;
        }
        int maxPid = pids.stream()
                .reduce(Integer::max)
                .orElseThrow(() -> new IllegalArgumentException("Sempre deveria haver pid!"));
        if (service.getInfoModule().getPid() == maxPid) {
            makeCoordinator();
        } else {
            this.coordinatorAddress = service.getInfoModule().getExternalAddress(maxPid)
                    .orElseThrow(() -> new IllegalArgumentException("Never, never, never should happen!"));
        }
        onElection = false;
    }

    public void setOnElection(boolean onElection) {
        this.onElection = onElection;
    }

    public boolean isOnElection() {
        return onElection;
    }

    public Request request(Class<? extends ActionHandler> handler, Payload payload) {
        Request request = service.request(coordinatorAddress, handler, payload);
        request.onTimeout(this::startElection);
        return request;
    }

    private void startElection() {
        onElection = true;
        System.out.println("Processo de eleição inicializado.");
        Payload payload = new Payload();
        LinkedList<Integer> pids = new LinkedList<>();
        pids.add(service.getInfoModule().getPid());
        payload.put(PayloadKeys.PIDS.name(), pids);
        service.getInfoModule()
                .getSuccessor()
                .ifPresent(successor -> service.request(successor.getAddress(), ElectionHandler.class, payload));
    }

}
