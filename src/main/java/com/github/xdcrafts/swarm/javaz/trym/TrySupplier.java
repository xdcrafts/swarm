package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.util.function.ISupplier;
import com.github.xdcrafts.swarm.util.function.IThrowingSupplier;

/**
 * Supplier that is able to produce try monad in case of failure.
 * @param <T> value type
 */
public class TrySupplier<T> implements ISupplier<ITryM<T>> {

    private final IThrowingSupplier<T> supplier;

    /**
     * Converts throwing supplier to try.
     * @param supplier supplier function
     * @param <T> return type
     * @return try supplier
     */
    public static <T> TrySupplier<T> trySupplier(IThrowingSupplier<T> supplier) {
        return new TrySupplier<>(supplier);
    }

    public TrySupplier(IThrowingSupplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Closure for TrySupplier<T> can not be null!");
        }
        this.supplier = supplier;
    }

    @Override
    public ITryM<T> get() {
        return TryMOps.tryM(this.supplier);
    }
}

