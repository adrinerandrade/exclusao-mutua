package impl;

import service.Address;

import java.util.Objects;

public class ExternalService implements Comparable<ExternalService> {

    private final Address address;
    private final int pid;

    public ExternalService(Address address, int pid) {
        this.address = address;
        this.pid = pid;
    }

    public Address getAddress() {
        return address;
    }

    public int getPid() {
        return pid;
    }

    @Override
    public int compareTo(ExternalService o) {
        return Integer.compare(pid, o.getPid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalService that = (ExternalService) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return "ExternalService{" +
                "address=" + address +
                ", pid=" + pid +
                '}';
    }

}
