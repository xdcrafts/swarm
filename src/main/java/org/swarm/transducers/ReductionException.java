package org.swarm.transducers;

/**
 * Reduction exception that may occurred during reduction process.
 */
public class ReductionException extends RuntimeException {
    public ReductionException(String message) {
        super(message);
    }
    public ReductionException(Throwable cause) {
        super(cause);
    }
    public ReductionException(String message, Throwable cause) {
        super(message, cause);
    }
}
