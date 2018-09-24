package service;

import java.util.concurrent.CompletableFuture;

public interface ActionHandler {

    CompletableFuture<Payload> onRequest(Payload payload);

}
