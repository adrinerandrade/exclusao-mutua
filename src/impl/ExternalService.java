package impl;

import service.Address;

import java.util.Objects;

public class ExternalService {

    private final Address address;
    private final int rank;

    public ExternalService(Address address, int rank) {
        this.address = address;
        this.rank = rank;
    }

    public Address getAddress() {
        return address;
    }

    public int getRank() {
        return rank;
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
}
