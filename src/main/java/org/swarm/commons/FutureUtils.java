/*******************************************************************************
 * Copyright (C) PlaceIQ, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by placeiq 2015
 *******************************************************************************/
package org.swarm.commons;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.stream.Collectors.toList;

/**
 *
 */
public class FutureUtils {

    /**
     *
     * @param futures
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<List<T>> sequence(Collection<CompletableFuture<T>> futures) {
        final CompletableFuture<Void> allDoneFuture = allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(toList()));
    }

    /**
     * Completes {@code future} with value from {@code resultSupplier}.
     * If {@code resultSupplier} throws an exception, then {@code future} will be {@code completedExceptionally()}
     * with that exception.
     * @param future - future to complete
     * @param resultSupplier - function that may throw an exception
     */
    public static <T> void completeSafely(CompletableFuture<T> future, Supplier<T> resultSupplier) {
        try {
            future.complete(resultSupplier.get());
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
    }

    /**
     * Completes {@code future} exceptionally with exception from {@code exceptionSupplier}.
     * If {@code exceptionSupplier} throws an exception, then {@code future} will be {@code completedExceptionally()}
     * with this new exception.
     * @param future - future to complete
     * @param exceptionSupplier - function that may throw an exception
     */
    public static <T> void completeSafelyWithException(
            CompletableFuture<T> future,
            Supplier<Throwable> exceptionSupplier
    ) {
        try {
            future.completeExceptionally(exceptionSupplier.get());
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
    }

    public static final Supplier<ScheduledExecutorService> TIMEOUT_SCHEDULER = LangUtils.lazy(() ->
        new ScheduledThreadPoolExecutor(
            1,
            runnable -> {
                Thread daemonThread = new Thread(runnable);
                daemonThread.setDaemon(true);
                return daemonThread;
            }
        )
    );

    /**
     * Creates new timeout future that completes exceptionally after duration.
     */
    public static <T> CompletableFuture<T> timeout(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        TIMEOUT_SCHEDULER.get().schedule(
            () -> promise.completeExceptionally(new TimeoutException("Timeout after " + duration)),
            duration.toMillis(),
            TimeUnit.MILLISECONDS
        );
        return promise;
    }

    /**
     * Returns new future that returns with timeout exception if not done within timeout duration.
     */
    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = timeout(duration);
        return future.applyToEither(timeout, Function.identity());
    }
}
