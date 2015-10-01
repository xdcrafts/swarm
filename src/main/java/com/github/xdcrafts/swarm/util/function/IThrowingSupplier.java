package com.github.xdcrafts.swarm.util.function;

/**
 * Supplier that can throw exceptions.
 * @param <T> supply value type
 */
public interface IThrowingSupplier<T> {
    /**
     * Applies function and returns value.
     * @return value of type T
     * @throws Throwable possible exceptions
     */
    T get() throws Throwable;
    /**
     * Omits exceptions.
     * @return simple supplier
     */
    default ISupplier<T> omit() {
        return () -> {
            try {
                return get();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        };
    }
}
