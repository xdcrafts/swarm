package com.github.xdcrafts.swarm.async;

/**
 * Swarm async exception.
 */
public class AsyncException extends RuntimeException {
    public AsyncException(String message) {
        super(message);
    }
    public AsyncException(Throwable cause) {
        super(cause);
    }
    public AsyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
