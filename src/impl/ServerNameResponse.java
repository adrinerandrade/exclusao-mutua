package impl;

import service.Address;

import java.io.Serializable;
import java.util.List;

public class ServerNameResponse implements Serializable {

    private final List<Address> allServices;
    private final int pid;

    public ServerNameResponse(List<Address> allServices, int pid) {
        this.allServices = allServices;
        this.pid = pid;
    }

    public List<Address> getAllServices() {
        return allServices;
    }

    public int getPid() {
        return pid;
    }

}
