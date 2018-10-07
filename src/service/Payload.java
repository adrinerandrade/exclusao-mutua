package service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Payload implements Serializable {

    private Map<String, Object> data = new HashMap<>();

    public Payload() {
    }

    public Payload put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public <T> T get(String key) {
        return (T) data.get(key);
    }

    Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }

}
