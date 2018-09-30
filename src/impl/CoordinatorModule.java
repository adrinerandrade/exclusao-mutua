package impl;

import service.Address;

import java.util.Optional;

public class CoordinatorModule {

    private ApplicationService service;
    private Address coordinatorAddress;
    private boolean isCoordinator;

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
        return isCoordinator;
    }

    public void makeCoordinator() {
        this.coordinatorAddress = null;
        this.isCoordinator = true;
        System.out.println("Serviço atribuido como Coordenador.");
    }

    public void newCoordinator(Address address) {
        this.coordinatorAddress = address;
        this.isCoordinator = false;
        System.out.println(String.format("Novo coordenador: %s.", address));
    }

}
