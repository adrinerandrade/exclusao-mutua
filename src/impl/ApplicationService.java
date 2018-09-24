package impl;

import handlers.CoordinatorActionHandler;
import handlers.CoordinatorHandler;
import handlers.ElectionHandler;
import handlers.NewProcessRequestHandler;
import service.ActionHandler;
import service.ServiceScope;

import java.util.Arrays;
import java.util.List;

public class ApplicationService implements ServiceScope {

    @Override
    public List<ActionHandler> getHandlers() {
        return Arrays.asList(
                new CoordinatorHandler(this),
                new CoordinatorActionHandler(this),
                new ElectionHandler(this),
                new NewProcessRequestHandler(this)
        );
    }

}
