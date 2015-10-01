package com.github.xdcrafts.swarm.transducers;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Common implementation of IReduction.
 * @param <T> result type
 */
public final class Reduction<T> implements Supplier<T> {

    private volatile T value;
    private volatile boolean isReduced = false;
    private volatile Optional<ReductionException> reductionException = Optional.empty();

    /**
     * Creates new reduction instance.
     * @param value value to wrap with reduction
     * @param <T> value type
     * @return reduction instance
     */
    public static <T> Reduction<T> reduction(T value) {
        return new Reduction<>(value);
    }

    public Reduction(T value) {
        this.value = value;
    }

    public boolean isReduced() {
        return this.isReduced;
    }

    /**
     * Reduction termination setter.
     * @param reduced set up reduced flag
     * @return this reduction instance
     */
    public Reduction<T> setIsReduced(boolean reduced) {
        this.isReduced = reduced;
        return this;
    }

    public boolean isFailed() {
        return this.reductionException.isPresent();
    }

    public Optional<ReductionException> getError() {
        return this.reductionException;
    }

    /**
     * Reduction exception setter.
     * @param error exception
     * @return this reduction instance
     */
    public Reduction<T> setReductionException(ReductionException error) {
        this.reductionException = Optional.of(error);
        return this;
    }

    @Override
    public T get() {
        return this.value;
    }

    /**
     * Sets new result value.
     * @param newValue updated value
     * @return this reduction instance
     */
    public Reduction<T> set(T newValue) {
        this.value = newValue;
        return this;
    }

    @Override
    public String toString() {
        return "Reduction{"
                + "value=" + value
                + ", isReduced=" + isReduced
                + ", reductionException=" + reductionException
                + '}';
    }
}
