package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.util.function.IThrowingFunction;

import java.io.Serializable;
import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.fail;

/**
 * Try monad.
 * @param <T> value type.
 */
public abstract class TryM<T> implements Serializable, ITryM<T> {

    @Override
    public abstract <U> ITryM<U> map(Function<T, U> function);

    @Override
    public <U, MM extends IApplicative<Function<T, U>, ITryM<?>>> ITryM<U> applicativeMap(MM applicativeFunction) {
        //noinspection unchecked
        final ITryM<ITryM<U>> mapped = (ITryM<ITryM<U>>) applicativeFunction.map(this::map);
        final ITryM<U> result;
        if (mapped.isFailure()) {
            result = fail(mapped.throwable());
        } else {
            result = mapped.value();
        }
        return result;
    }

    @Override
    public <U, MM extends IMonad<U, ITryM<?>>> ITryM<U> flatMap(Function<T, MM> function) {
        //noinspection unchecked
        final ITryM<ITryM<U>> mapped = (ITryM<ITryM<U>>) map(function);
        final ITryM<U> result;
        if (mapped.isFailure()) {
            result = fail(mapped.throwable());
        } else {
            result = mapped.value();
        }
        return result;
    }

    @Override
    public abstract <U> ITryM<U> mapT(IThrowingFunction<T, U> function);

    @Override
    public <U> ITryM<U> applicativeMapT(ITryM<IThrowingFunction<T, U>> applicativeFunction) {
        final ITryM<ITryM<U>> mapped = applicativeFunction.mapT(this::mapT);
        final ITryM<U> result;
        if (mapped.isFailure()) {
            result = fail(mapped.throwable());
        } else {
            result = mapped.value();
        }
        return result;
    }

    @Override
    public <U> ITryM<U> flatMapT(IThrowingFunction<T, ITryM<U>> function) {
        final ITryM<ITryM<U>> mapped = mapT(function);
        final ITryM<U> result;
        if (mapped.isFailure()) {
            result = fail(mapped.throwable());
        } else {
            result = mapped.value();
        }
        return result;
    }

    @Override
    public abstract boolean isSuccess();
    @Override
    public abstract boolean isFailure();

    @Override
    public abstract T value();
    @Override
    public abstract Throwable throwable();

    @Override
    public abstract <U> U fold(Function<Throwable, U> ifFailure, Function<T, U> ifSuccess);
    @Override
    public <U> U fold(final U ifFailure, Function<T, U> ifSuccess) {
        return fold(
            (Function<Throwable, U>) throwable -> ifFailure,
            ifSuccess
        );
    }
}
