package impl;

import handlers.RequestResourceHandler;
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
    private final ResourceModule resourceModule;

    public ApplicationService(int port) {
    	ServerNameResponse serverNameResponse = new ServerName().notifyCreation(port);
    	
        this.service = new Service(this, port);
        this.infoModule = new InfoModule(this);
        this.coordinatorModule = new CoordinatorModule(this);
        this.resourceModule = new ResourceModule(this);
        this.init(serverNameResponse);
    }

    public void init(ServerNameResponse serverNameResponse) {
        this.infoModule.loadAllServicesInfo(serverNameResponse);
    }

    public InfoModule getInfoModule() {
        return infoModule;
    }

    public CoordinatorModule getCoordinatorModule() {
        return coordinatorModule;
    }
    
    public ResourceModule getResourceModule() {
    	return resourceModule;
    }

    public Request request(Address address, Class<? extends ActionHandler> handler, Payload payload) {
        Request request = this.service.request(address, handler, payload);
        request.onTimeout(() -> {
            System.err.println(String.format("Serviço no endereço '%s' não respondeu a tempo", address));
            infoModule.remove(address);
        });
        return request;
    }

    public void stop() {
        service.stop();
    }

    @Override
    public List<ActionHandler> getHandlers() {
        return Arrays.asList(
                new CoordinatorHandler(this),
                new RequestResourceHandler(this),
                new ElectionHandler(this),
                new GetInfoRequestHandler(this),
                new RequestResourceHandler(this)
        );
    }

}
