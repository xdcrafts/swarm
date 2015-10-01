package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.util.function.IThrowingFunction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Try monad that represents successful or failed computation.
 * @param <T> value type
 */
public interface ITryM<T> extends IMonad<T, ITryM<?>> {

    @Override
    <U> ITryM<U> map(Function<T, U> function);

    @Override
    <U, MM extends IApplicative<Function<T, U>, ITryM<?>>> ITryM<U> applicativeMap(MM applicativeFunction);

    @Override
    <U, MM extends IMonad<U, ITryM<?>>> ITryM<U> flatMap(Function<T, MM> function);

    /**
     * Same as map but for failed value.
     * @param function function to map
     * @param <U> content type
     * @return try monad
     */
    <U> ITryM<U> mapT(IThrowingFunction<T, U> function);

    /**
     * Same as applicativeMap but for failed value.
     * @param applicativeFunction function to map
     * @param <U> content type
     * @return try monad
     */
    <U> ITryM<U> applicativeMapT(ITryM<IThrowingFunction<T, U>> applicativeFunction);

    /**
     * Same as flatMap but for failed value.
     * @param function function to map
     * @param <U> content type
     * @return try monad
     */
    <U> ITryM<U> flatMapT(IThrowingFunction<T, ITryM<U>> function);

    /**
     * Is this try monad success.
     * @return boolean
     */
    boolean isSuccess();

    /**
     * Is this try monad failure.
     * @return boolean
     */
    boolean isFailure();

    /**
     * Plain value of throws failure cause.
     * @return value of type T
     */
    T value();

    /**
     * Failure cause or IllegalAccessException exception.
     * @return throwable
     */
    Throwable throwable();

    /**
     * Applies ifFailure function in case of failure or ifSuccess function in case of success.
     * @param ifFailure failure mapper
     * @param ifSuccess success mapper
     * @param <U> new value type
     * @return try monad
     */
    <U> U fold(Function<Throwable, U> ifFailure, Function<T, U> ifSuccess);

    /**
     * Returns ifFailure value in case of failure or applies ifSuccess function in case of success.
     * @param ifFailure return value in case of failure
     * @param ifSuccess success mapper
     * @param <U> new value type
     * @return try monad
     */
    <U> U fold(final U ifFailure, Function<T, U> ifSuccess);

    /**
     * Consumes throwable in case of failure.
     * @param applicable consumer
     */
    void foreachFailure(Consumer<Throwable> applicable);
}
