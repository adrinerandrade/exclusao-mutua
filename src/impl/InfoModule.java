package impl;

import handlers.GetInfoRequestHandler;
import service.Address;
import service.Payload;

import java.util.*;

public class InfoModule {

    private final ApplicationService service;
    private final int priority;
    private final Set<ExternalService> aliveAddresses = new HashSet<>();

    public InfoModule(ApplicationService service) {
        this.service = service;
        this.priority = new Random().nextInt(10000);
    }

    public void loadAllServicesInfo() {
        List<Address> allServices = new ServerNameRequest().getAllServices();
        if (allServices.isEmpty()) {
            service.getCoordinatorModule().setCoordinatorAddress(null);
            service.getCoordinatorModule().setCoordinator(true);
        } else {
            Payload payload = new Payload();
            payload.put(PayloadKeys.PRIORITY.name(), this.priority);
            allServices.forEach(address -> service
                    .request(address, GetInfoRequestHandler.class, payload)
                    .whenReplied()
                    .thenAccept(response -> handleInfoResponse(address, response)));
        }
    }

    private void handleInfoResponse(Address address, Payload response) {
        aliveAddresses.add(new ExternalService(address, response.<Integer>get(PayloadKeys.PRIORITY.name())));
        if (Boolean.TRUE.equals(response.get(PayloadKeys.IS_COORDINATOR.name()))) {
            service.getCoordinatorModule().setCoordinator(false);
            service.getCoordinatorModule().setCoordinatorAddress(address);
        }
    }

    public int getPriority() {
        return priority;
    }

    public Set<ExternalService> getAliveAddresses() {
        return aliveAddresses;
    }

}
