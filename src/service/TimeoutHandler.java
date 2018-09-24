package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class TimeoutHandler {

    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    private Map<Integer, Timeout> timeouts = new HashMap<>();

    Timeout waitForResponse(Message message) {
        Timeout timeout = new Timeout(DEFAULT_TIMEOUT_MILLIS);
        int messageId = message.getId();
        timeouts.put(messageId, timeout);
        timeout.onTimeout(() -> {
            System.err.print(String.format("\nMessage not answered: %s", message));
            timeouts.remove(messageId);
        });
        return timeout;
    }

    void answered(Message message) {
        int messageId = message.getId();
        Optional.ofNullable(timeouts.get(messageId))
                .ifPresent(Timeout::complete);
        timeouts.remove(messageId);
    }

    void clearALl() {
        timeouts.values().forEach(Timeout::complete);
        timeouts.clear();
    }

}
