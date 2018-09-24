package service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

final class Message {

    private final int id;
    private Map<MessageHeader, Object> headers = new EnumMap<>(MessageHeader.class);
    private Payload payload;

    Message(String action, Payload payload) {
        this(MessageIdProvider.newId(), action, payload, MessageType.REQUEST);
    }

    Message(int id, String action, Payload payload, MessageType type) {
        putHeader(MessageHeader.TYPE, type);
        putHeader(MessageHeader.ACTION, action);
        this.id = id;
        this.payload = payload;
    }

    int getId() {
        return id;
    }

    Payload getPayload() {
        return payload;
    }

    void putHeader(MessageHeader key, Object value) {
        this.headers.put(key, value);
    }

    <T> T getHeader(MessageHeader key) {
        return (T) this.headers.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", headers=" + headers +
                ", payload=" + payload +
                '}';
    }

}
