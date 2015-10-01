package com.github.xdcrafts.swarm.javaz.future;

import com.github.xdcrafts.swarm.util.function.IThrowingSupplier;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonadOps;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Future operations.
 */
public final class FutureOps implements IMonadOps<IFuture<?>> {

    /**
     * Constructs new future of with supplied executor.
     * @param executor executor which would be used for async actions
     * @param <T> value type value type
     * @return future
     */
    public static <T> Future<T> future(Executor executor) {
        return new Future<>(executor, new CompletableFuture<>());
    }

    /**
     * Constructs new future of with supplied executor and completable future.
     * @param executor executor which would be used for async actions
     * @param completableFuture completable future
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> future(Executor executor, CompletableFuture<T> completableFuture) {
        return new Future<>(executor, completableFuture);
    }

    /**
     * Constructs new future of with supplied executor and supplier.
     * @param executor executor which would be used for async actions
     * @param supplier supplier of value
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> future(Executor executor, IThrowingSupplier<T> supplier) {
        return new Future<>(executor, CompletableFuture.supplyAsync(supplier.omit()::get, executor));
    }

    /**
     * Constructs new future of with supplied executor and value.
     * @param executor executor which would be used for async actions
     * @param value plain value
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> completed(Executor executor, T value) {
        return new Future<>(executor, CompletableFuture.completedFuture(value));
    }

    /**
     * Constructs new future of with supplied executor and exception.
     * @param executor executor which would be used for async actions
     * @param t exception
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> completed(Executor executor, Throwable t) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(t);
        return new Future<>(executor, future);
    }

    /**
     * Constructs new future of with fork join executor.
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> future() {
        return future(ForkJoinPool.commonPool());
    }

    /**
     * Constructs new future of with fork join executor and completable future.
     * @param completableFuture completable future
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> future(CompletableFuture<T> completableFuture) {
        return future(ForkJoinPool.commonPool(), completableFuture);
    }

    /**
     * Constructs new future of with fork join executor and supplier.
     * @param supplier supplier of value of type T
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> future(IThrowingSupplier<T> supplier) {
        return future(ForkJoinPool.commonPool(), supplier);
    }

    /**
     * Constructs new future of with fork join executor and value.
     * @param value plain value of type T
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> completed(T value) {
        return completed(ForkJoinPool.commonPool(), value);
    }

    /**
     * Constructs new future of with fork join executor and exception.
     * @param t exception
     * @param <T> value type
     * @return future
     */
    public static <T> Future<T> completed(Throwable t) {
        return completed(ForkJoinPool.commonPool(), t);
    }

    private FutureOps() {
        // Nothing
    }

    /**
     * Future operations instance.
     */
    public static final FutureOps ID = new FutureOps();

    @Override
    public <U> IFuture<?> pure(U value) {
        return completed(value);
    }
}
