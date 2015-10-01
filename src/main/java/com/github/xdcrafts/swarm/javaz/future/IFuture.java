package com.github.xdcrafts.swarm.javaz.future;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.javaz.common.tuple.Tuple;
import com.github.xdcrafts.swarm.javaz.option.IOption;
import com.github.xdcrafts.swarm.javaz.trym.ITryM;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Future monad that represents asynchronous calculations.
 * @param <T> value type
 */
public interface IFuture<T> extends IMonad<T, IFuture<?>> {
    @Override <U> IFuture<U> map(Function<T, U> function);
    @Override <U, MM extends IApplicative<Function<T, U>, IFuture<?>>> IFuture<U> applicativeMap(MM applicativeFunction);
    @Override <U, MM extends IMonad<U, IFuture<?>>> IFuture<U> flatMap(Function<T, MM> function);
    @Override default void foreach(Consumer<T> function) {
        onSuccess(function);
    }
    /**
     * Completes future with value.
     * @param value completion value
     * @return success status
     */
    boolean complete(T value);
    /**
     * Completes future with exception.
     * @param t throwable
     * @return success status
     */
    boolean complete(Throwable t);
    /**
     * Completes future with try monad.
     * @param t try monad
     * @return success status
     */
    boolean complete(ITryM<T> t);
    /**
     * Calls onSuccess when future completes with success.
     * @param onSuccess callback
     */
    void onSuccess(Consumer<T> onSuccess);
    /**
     * Calls onFailure when future completes with failure.
     * @param onFailure callback
     */
    void onFailure(Consumer<Throwable> onFailure);
    /**
     * Calls onComplete when future completes.
     * @param onComplete callback
     */
    void onComplete(Consumer<ITryM<T>> onComplete);
    /**
     * Is this future completed.
     * @return boolean
     */
    boolean isCompleted();
    /**
     * If future fails, recovers with recover function.
     * @param recover recovery function
     * @return recovered function
     */
    IFuture<T> recover(Function<Throwable, T> recover);
    /**
     * If future fails, recovers with recover function.
     * @param recover recovery function
     * @return recovered function
     */
    IFuture<T> recoverWith(Function<Throwable, IFuture<T>> recover);
    /**
     * Returns value right now: try monad if completed, empty otherwise.
     * @return optional of try monad
     */
    IOption<ITryM<T>> value();
    /**
     * Blocks and returns result as try monad.
     * @return try monad
     */
    ITryM<T> get();
    /**
     * Blocks and returns result as try monad within timeout.
     * @param timeout absolute value
     * @param timeUnit time unit
     * @return try monad
     */
    ITryM<T> get(long timeout, TimeUnit timeUnit);
    /**
     * Zips with another future.
     * @param another another future
     * @param <U> another future value type
     * @return future with tuple of values T and U
     */
    <U> IFuture<Tuple<T, U>> zip(IFuture<U> another);
    /**
     * Converts this future to completable future.
     * @return completable future
     */
    CompletableFuture<T> toCompletableFuture();
}
