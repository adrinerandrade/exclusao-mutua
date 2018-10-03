package impl;

import handlers.CoordinatorHandler;
import service.Address;
import service.Payload;

import java.util.List;
import java.util.Optional;

public class CoordinatorModule {

    private ApplicationService service;
    private Address coordinatorAddress;

    public CoordinatorModule(ApplicationService service) {
        this.service = service;
    }

    public Optional<Address> getCoordinatorAddress() {
        return Optional.ofNullable(coordinatorAddress);
    }

    public void setCoordinatorAddress(Address coordinatorAddress) {
        this.coordinatorAddress = coordinatorAddress;
    }

    public boolean isCoordinator() {
        return coordinatorAddress != null;
    }

    public void makeCoordinator() {
        this.coordinatorAddress = null;
        System.out.println("Serviço atribuido como Coordenador.");
    }

    public void newCoordinator(Address address) {
        this.coordinatorAddress = address;
        System.out.println(String.format("Novo coordenador: %s.", address));
    }

    public void updateCoordinator(List<Integer> pids) {
        if (pids.isEmpty()) {
            makeCoordinator();
            return;
        }
        int maxPid = pids.stream()
                .reduce(Integer::max)
                .orElseThrow(() -> new IllegalArgumentException("Never, never, never should happen!"));
        if (service.getInfoModule().getPid() == maxPid) {
            makeCoordinator();
        }
        this.coordinatorAddress = service.getInfoModule().getExternalAddress(maxPid)
                .orElseThrow(() -> new IllegalArgumentException("Never, never, never should happen!"));
    }

}
