package service;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Service {

    private final ServiceScope serviceScope;
    private final Messenger messenger;
    private final TimeoutHandler timeoutHandler;
    private final HashMap<Integer, CompletableFuture<Payload>> waitingResponse = new HashMap<>();

    public Service(ServiceScope serviceScope, int port) {
        this.serviceScope = serviceScope;
        this.messenger = new Messenger(port);
        this.messenger.onMessage(this::onMessage);
        this.timeoutHandler = new TimeoutHandler();
    }

    public Request request(Address address, Class<? extends ActionHandler> handler, Payload payload) {
        Message message = new Message(RequestHandlerExtractor.getRequestHandler(handler).action(), payload);

        CompletableFuture<Payload> future = new CompletableFuture<>();
        future.exceptionally(ex -> {
            System.err.println(ex);
            return null;
        });
        this.messenger.send(message, address);
        waitingResponse.put(message.getId(), future);
        return new Request(future, timeoutHandler.waitForResponse(message));
    }

    private void onMessage(Message message) {
        switch (message.<MessageType> getHeader(MessageHeader.TYPE)) {
            case REQUEST:
                handleRequest(message);
                break;
            case RESPONSE:
                handleResponse(message);
                break;
        }
    }

    private void handleRequest(Message message) {
        String action = message.getHeader(MessageHeader.ACTION);
        String sourceHost = message.getHeader(MessageHeader.SOURCE_HOST);
        int sourcePort = message.getHeader(MessageHeader.SOURCE_PORT);
        Address sourceAddress = new Address(sourceHost, sourcePort);
        serviceScope.getHandlers().stream()
                .filter(handler -> RequestHandlerExtractor.getRequestHandler(handler).action().equals(action))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Handler not found for action: %s", action)))
                .onRequest(sourceAddress, message.getPayload())
                .thenAccept(payload -> new Reply(message).send(this.messenger, payload));
    }

    private void handleResponse(Message message) {
        timeoutHandler.answered(message);
        int messageId = message.getId();
        Optional.ofNullable(waitingResponse.get(messageId))
                .ifPresent(future -> future.complete(message.getPayload()));
    }

    public void stop() {
        this.messenger.close();
        this.timeoutHandler.clearALl();
        this.waitingResponse.clear();
    }

}
