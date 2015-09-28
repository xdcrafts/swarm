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
    private static <T, I> void putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        Consumer<Throwable> exceptionHandler,
        CompletableFuture<Void> completion
    ) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> {
                    if (err != null) {
                        exceptionHandler.accept(err);
                    }
                    putLoop(channel, supplier, exceptionHandler, completion);
                });
        } else {
            completion.complete(null);
        }
    }

    /**
     * Put loop.
     */
    private static <T, I> CompletableFuture<Void> putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        Consumer<Throwable> exceptionHandler
    ) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        putLoop(channel, supplier, exceptionHandler, completion);
        return completion;
    }

    /**
     * Put loop.
     */
    private static <T, I> void putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        CompletableFuture<Void> completion
    ) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> putLoop(channel, supplier, completion));
        } else {
            completion.complete(null);
        }
    }

    /**
     * Put loop.
     */
    public static <T, I> CompletableFuture<Void> putLoop(IChannel<T, I> channel, Supplier<I> supplier) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        putLoop(channel, supplier, completion);
        return completion;
    }

    /**
     * Put loop.
     */
    private static <T, I> void putWhileLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        BiPredicate<Optional<Supplier<T>>, Optional<Throwable>> predicate,
        CompletableFuture<Void> completion
    ) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> {
                    if (predicate.test(flatten(ofNullable(res)), ofNullable(err))) {
                        putWhileLoop(channel, supplier, predicate, completion);
                    } else {
                        completion.complete(null);
                    }
                });
        } else {
            completion.complete(null);
        }
    }

    /**
     * Put loop.
     */
    public static <T, I> CompletableFuture<Void> putWhileLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        BiPredicate<Optional<Supplier<T>>, Optional<Throwable>> predicate
    ) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        putWhileLoop(channel, supplier, predicate, completion);
        return completion;
    }

    /**
     * Take loop.
     */
    private static <T, I> void takeLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer,
        Consumer<Throwable> exceptionHandler,
        CompletableFuture<Void> completion
    ) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res != null) {
                        consumer.accept(res);
                    }
                    if (err != null) {
                        exceptionHandler.accept(err);
                    }
                    takeLoop(channel, consumer, exceptionHandler, completion);
                });
        } else {
            completion.complete(null);
        }
    }

    /**
     * Take loop.
     */
    public static <T, I> CompletableFuture<Void> takeLoop(
        IChannel<T, I> channel, Consumer<T> consumer, Consumer<Throwable> exceptionHandler
    ) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        takeLoop(channel, consumer, exceptionHandler, completion);
        return completion;
    }

    /**
     * Take loop.
     */
    private static <T, I> void takeLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer,
        CompletableFuture<Void> completion
    ) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res != null) {
                        consumer.accept(res);
                    }
                    takeLoop(channel, consumer);
                });
        } else {
            completion.complete(null);
        }
    }

    /**
     * Take loop.
     */
    public static <T, I> CompletableFuture<Void> takeLoop(IChannel<T, I> channel, Consumer<T> consumer) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        takeLoop(channel, consumer, completion);
        return completion;
    }

    /**
     * Take loop.
     */
    private static <T, I> void takeWhileLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer,
        BiPredicate<Optional<T>, Optional<Throwable>> predicate,
        CompletableFuture<Void> completion
    ) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res != null) {
                        consumer.accept(res);
                    }
                    if (predicate.test(ofNullable(res), ofNullable(err))) {
                        takeWhileLoop(channel, consumer, predicate, completion);
                    } else {
                        completion.complete(null);
                    }
                });
        } else {
            completion.complete(null);
        }
    }

    /**
     * Take loop.
     */
    public static <T, I> CompletableFuture<Void> takeWhileLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer,
        BiPredicate<Optional<T>, Optional<Throwable>> predicate
    ) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        takeWhileLoop(channel, consumer, predicate, completion);
        return completion;
    }

    /**
     * Pipe.
     */
    private static <R, V, T, I> void pipe(
        IChannel<T, I> left,
        IChannel<R, V> right,
        Function<T, V> mapper,
        CompletableFuture<Void> completion
    ) {
        if (!left.isClosed() && !right.isClosed()) {
            left.take().whenComplete((res, err) -> {
                    CompletableFuture<Optional<Supplier<R>>> put = CompletableFuture.completedFuture(empty());
                    if (res != null) {
                        put = right.put(() -> mapper.apply(res));
                    }
                    put.whenComplete((putRes, putErr) -> pipe(left, right, mapper));
                });
        } else {
            completion.complete(null);
        }
    }

    /**
     * Pipe.
     */
    public static <R, V, T, I> CompletableFuture<Void> pipe(
        IChannel<T, I> left,
        IChannel<R, V> right,
        Function<T, V> mapper
    ) {
        final CompletableFuture<Void> completion = new CompletableFuture<>();
        pipe(left, right, mapper, completion);
        return completion;
    }

    /**
     * Pipe.
     */
    public static <R, T, I> CompletableFuture<Void> pipe(IChannel<T, I> left, IChannel<R, T> right) {
        return pipe(left, right, Function.<T>identity());
    }
}
