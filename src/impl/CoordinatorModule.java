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

    public boolean getCoordinator() {
        return isCoordinator;
    }

    public void setCoordinator(boolean coordinator) {
        this.isCoordinator = coordinator;
    }

}
