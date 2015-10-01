package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.util.function.IThrowingSupplier;
import com.github.xdcrafts.swarm.util.function.IThrowingFunction;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonadOps;

/**
 * Try monad operations.
 */
@SuppressWarnings("unchecked")
public final class TryMOps implements IMonadOps<ITryM<?>> {

    /**
     * Returns success instance of try monad.
     * @param value successful value
     * @param <U> value type
     * @return try monad
     */
    public static <U> Success<U> success(U value) {
        return Success.success(value);
    }

    /**
     * Returns failure instance of try monad.
     * @param throwable cause
     * @param <U> value type
     * @return try monad
     */
    public static <U> Failure<U> fail(Throwable throwable) {
        return Failure.fail(throwable);
    }

    /**
     * Executes throwing function and returns try monad instance.
     * @param throwing function to apply
     * @param a value
     * @param <A> value type
     * @param <B> return value type
     * @return try monad
     */
    public static <A, B> ITryM<B> tryM(IThrowingFunction<A, B> throwing, A a) {
        ITryM<B> result;
        try {
            result = success(throwing.apply(a));
        } catch (Throwable t) {
            result = fail(t);
        }
        return result;
    }

    /**
     * Executes throwing function and returns try monad instance.
     * @param throwing supplier to try
     * @param <T> value type
     * @return try monad
     */
    public static <T> ITryM<T> tryM(IThrowingSupplier<T> throwing) {
        ITryM<T> result;
        try {
            result = success(throwing.get());
        } catch (Throwable t) {
            result = fail(t);
        }
        return result;
    }

    /**
     * Converts throwing function to try function.
     * @param function function to convert
     * @param <A> input value type
     * @param <B> return value type
     * @return try function
     */
    public static <A, B> TryFunction<A, B> tryFunction(IThrowingFunction<A, B> function) {
        return TryFunction.tryFunction(function);
    }

    /**
     * Converts throwing supplier to try supplier.
     * @param supplier supplier to convert
     * @param <T> value type
     * @return try supplier
     */
    public static <T> TrySupplier<T> trySupplier(IThrowingSupplier<T> supplier) {
        return TrySupplier.trySupplier(supplier);
    }

    private TryMOps() {
        // Nothing
    }

    public static final TryMOps ID = new TryMOps();

    @Override
    public <U> ITryM<U> pure(U value) {
        return success(value);
    }
}
