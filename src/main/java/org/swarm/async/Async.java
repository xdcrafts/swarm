package org.swarm.async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.swarm.commons.OptionalUtils.flatten;

/**
 * Namespace for async helper functions.
 */
public final class Async {

    private Async() {
        // Nothing
    }


    /**
     * Put loop.
     */
    public static <T, I> IChannel<T, I> putLoop(
        IChannel<T, I> channel, Supplier<I> supplier, Consumer<Throwable> exceptionHandler
    ) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> {
                    if (err != null) {
                        exceptionHandler.accept(err);
                    }
                    putLoop(channel, supplier, exceptionHandler);
                });
        }
        return channel;
    }

    /**
     * Put loop.
     */
    public static <T, I> IChannel<T, I> putLoop(IChannel<T, I> channel, Supplier<I> supplier) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> putLoop(channel, supplier));
        }
        return channel;
    }

    /**
     * Put loop.
     */
    public static <T, I> IChannel<T, I> putWhileLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        BiPredicate<Optional<Supplier<T>>, Optional<Throwable>> predicate
    ) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> {
                    if (predicate.test(flatten(ofNullable(res)), ofNullable(err))) {
                        putWhileLoop(channel, supplier, predicate);
                    }
                });
        }
        return channel;
    }

    /**
     * Take loop.
     */
    public static <T, I> IChannel<T, I> takeLoop(
        IChannel<T, I> channel, Consumer<T> consumer, Consumer<Throwable> exceptionHandler
    ) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res != null) {
                        consumer.accept(res);
                    }
                    if (err != null) {
                        exceptionHandler.accept(err);
                    }
                    takeLoop(channel, consumer, exceptionHandler);
                });
        }
        return channel;
    }

    /**
     * Take loop.
     */
    public static <T, I> IChannel<T, I> takeLoop(IChannel<T, I> channel, Consumer<T> consumer) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res != null) {
                        consumer.accept(res);
                    }
                    takeLoop(channel, consumer);
                });
        }
        return channel;
    }

    /**
     * Take loop.
     */
    public static <T, I> IChannel<T, I> takeWhileLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer,
        BiPredicate<Optional<T>, Optional<Throwable>> predicate
    ) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res != null) {
                        consumer.accept(res);
                    }
                    if (predicate.test(ofNullable(res), ofNullable(err))) {
                        takeWhileLoop(channel, consumer, predicate);
                    }
                });
        }
        return channel;
    }

    /**
     * Pipe.
     */
    public static <R, V, T, I> IChannel<T, I> pipe(IChannel<T, I> left, IChannel<R, V> right, Function<T, V> mapper) {
        if (!left.isClosed() && !right.isClosed()) {
            left.take().whenComplete((res, err) -> {
                    CompletableFuture<Optional<Supplier<R>>> put = CompletableFuture.completedFuture(empty());
                    if (res != null) {
                        put = right.put(() -> mapper.apply(res));
                    }
                    put.whenComplete((putRes, putErr) -> pipe(left, right, mapper));
                });
        }
        return left;
    }

    /**
     * Pipe.
     */
    public static <R, T, I> IChannel<T, I> pipe(IChannel<T, I> left, IChannel<R, T> right) {
        return pipe(left, right, Function.<T>identity());
    }
}
