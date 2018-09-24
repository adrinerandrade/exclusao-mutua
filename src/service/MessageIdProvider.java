package service;

final class MessageIdProvider {

    private static int lastId;

    private MessageIdProvider() {}

    synchronized static int newId() {
        return ++lastId;
    }

}
