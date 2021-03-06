package impl;

import handlers.GetInfoRequestHandler;
import service.Address;
import service.Payload;

import java.util.*;

public class InfoModule {

    private int pid;
    private final ApplicationService service;
    private final TreeSet<ExternalService> aliveAddresses = new TreeSet<>();

    public InfoModule(ApplicationService service) {
        this.service = service;
    }

    public void loadAllServicesInfo(ServerNameResponse serverNameResponse) {
        this.pid = serverNameResponse.getPid();
        List<Address> allServices = serverNameResponse.getAllServices();
        if (allServices.isEmpty()) {
            service.getCoordinatorModule().makeCoordinator();
        } else {
            Payload payload = new Payload();
            payload.put(PayloadKeys.PID.name(), this.pid);
            allServices.forEach(address -> service
                    .request(address, GetInfoRequestHandler.class, payload)
                    .whenReplied()
                    .thenAccept(response -> handleInfoResponse(address, response)));
        }
    }

    private void handleInfoResponse(Address address, Payload response) {
        aliveAddresses.add(new ExternalService(address, response.<Integer>get(PayloadKeys.PID.name())));
        if (Boolean.TRUE.equals(response.get(PayloadKeys.IS_COORDINATOR.name()))) {
            service.getCoordinatorModule().setCoordinatorAddress(address);
        }
    }

    public int getPid() {
        return pid;
    }

    public void newService(ExternalService externalService) {
        aliveAddresses.add(externalService);
        System.out.println(String.format("processo %s: %s", pid, aliveAddresses));
    }

    public void remove(Address address) {
        aliveAddresses.stream()
                .filter(e -> e.getAddress().equals(address))
                .findAny()
                .ifPresent(e -> aliveAddresses.remove(e));
    }

    public Optional<ExternalService> getSuccessor() {
        Optional<ExternalService> successor = findSuccessor();
        if (!successor.isPresent()) {
            service.getCoordinatorModule().makeCoordinator();
        }
        return successor;
    }

    public Optional<Address> getExternalAddress(int pid) {
        return aliveAddresses.stream()
                .filter(aliveAddresses -> aliveAddresses.getPid() == pid)
                .findFirst()
                .map(ExternalService::getAddress);
    }

    private Optional<ExternalService> findSuccessor() {
        if (aliveAddresses.isEmpty()) {
            return Optional.empty();
        }
        ExternalService successor = aliveAddresses.higher(new ExternalService(new Address("", 0), pid));
        if (successor == null) {
            return Optional.of(aliveAddresses.first());
        }

        return Optional.of(successor);
    }

}
