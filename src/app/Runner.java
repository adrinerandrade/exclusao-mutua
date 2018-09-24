package app;

import impl.ApplicationService;
import service.Service;

public class Runner {

    public static void main(String[] args) {
        new Service(new ApplicationService(), 3050);
    }

}
