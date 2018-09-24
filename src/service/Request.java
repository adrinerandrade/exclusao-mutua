package service;

import java.util.concurrent.CompletableFuture;

public final class Request {

    private final CompletableFuture<Payload> future;
    private final Timeout timeout;

    public Request(CompletableFuture<Payload> future, Timeout timeout) {
        this.future = future;
        this.timeout = timeout;
    }

    public CompletableFuture<Payload> whenReplied() {
        return future;
    }

    public void onTimeout(Runnable runnable) {
        timeout.onTimeout(runnable);
    }

}
