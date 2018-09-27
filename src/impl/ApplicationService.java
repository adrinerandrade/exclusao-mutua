package impl;

import handlers.CoordinatorActionHandler;
import handlers.CoordinatorHandler;
import handlers.ElectionHandler;
import handlers.GetInfoRequestHandler;
import service.*;

import java.util.Arrays;
import java.util.List;

public class ApplicationService implements ServiceScope {

    private final Service service;
    private final InfoModule infoModule;
    private final CoordinatorModule coordinatorModule;

    public ApplicationService(int port) {
        this.service = new Service(this, port);
        this.infoModule = new InfoModule(this);
        this.coordinatorModule = new CoordinatorModule(this);
        this.init();
    }

    public void init() {
        this.infoModule.loadAllServicesInfo();
    }

    public InfoModule getInfoModule() {
        return infoModule;
    }

    public CoordinatorModule getCoordinatorModule() {
        return coordinatorModule;
    }

    public Request request(Address address, Class<? extends ActionHandler> handler, Payload payload) {
        return this.service.request(address, handler, payload);
    }

    public void stop() {
        service.stop();
    }

    @Override
    public List<ActionHandler> getHandlers() {
        return Arrays.asList(
                new CoordinatorHandler(this),
                new CoordinatorActionHandler(this),
                new ElectionHandler(this),
                new GetInfoRequestHandler(this)
        );
    }

}
