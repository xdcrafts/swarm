package org.swarm.async;

import org.swarm.monads.Either;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Namespace for async helper functions.
 */
public final class Async {

    private Async() {
        // Nothing
    }

    /**
     * Internal completion hook.
     */
    public static class Completion {
        private final CompletableFuture<Void> completion = new CompletableFuture<>();
        /**
         * Completes with exception.
         */
        public void exceptionally(Throwable error) {
            this.completion.completeExceptionally(error);
        }
        /**
         * Completes normally.
         */
        public void done() {
            this.completion.complete(null);
        }
        public boolean isDone() {
            return this.completion.isDone();
        }
        /**
         * Completion hook.
         */
        public CompletableFuture<Void> get() {
            return this.completion;
        }
    }

    /**
     * Handler interface for async result of completable future and completion hook.
     * @param <V>
     */
    public interface AsyncCompletionHandler<V> {
        /**
         * Handles async result with completion hook.
         */
        void handle(Either<Throwable, V> result, Completion completion);
    }

    /**
     * Put loop.
     */
    private static <T, I> void putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        AsyncCompletionHandler<Supplier<T>> asyncCompletionHandler,
        Completion completion
    ) {
        if (!channel.isClosed()) {
            channel.put(supplier).whenComplete((res, err) -> {
                    if (res == null && err == null) {
                        completion.exceptionally(
                            new AsyncException("No response, no error in result of completable future.")
                        );
                    }
                    try {
                        asyncCompletionHandler.handle(Either.either(ofNullable(err), res), completion);
                    } catch (Throwable t) {
                        completion.exceptionally(t);
                    }
                    if (!completion.isDone()) {
                        putLoop(channel, supplier, asyncCompletionHandler, completion);
                    }
                });
        } else {
            completion.done();
        }
    }

    /**
     * Put loop.
     */
    public static <T, I> CompletableFuture<Void> putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        AsyncCompletionHandler<Supplier<T>> asyncCompletionHandler
    ) {
        final Completion completion = new Completion();
        putLoop(channel, supplier, asyncCompletionHandler, completion);
        return completion.get();
    }

    /**
     * Put loop.
     */
    public static <T, I> CompletableFuture<Void> putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier
    ) {
        final Completion completion = new Completion();
        putLoop(
            channel,
            supplier,
            (res, cmp) -> {
            },
            completion
        );
        return completion.get();
    }

    /**
     * Take loop.
     */
    private static <T, I> void takeLoop(
        IChannel<T, I> channel,
        AsyncCompletionHandler<T> asyncCompletionHandler,
        Completion completion
    ) {
        if (!channel.isClosed()) {
            channel.take().whenComplete((res, err) -> {
                    if (res == null && err == null) {
                        completion.exceptionally(
                            new AsyncException("No response, no error in result of completable future.")
                        );
                    }
                    try {
                        asyncCompletionHandler.handle(Either.either(err, res), completion);
                    } catch (Throwable t) {
                        completion.exceptionally(t);
                    }
                    if (!completion.isDone()) {
                        takeLoop(channel, asyncCompletionHandler, completion);
                    }
                });
        } else {
            completion.done();
        }
    }

    /**
     * Take loop.
     */
    public static <T, I> CompletableFuture<Void> takeLoop(
        IChannel<T, I> channel,
        AsyncCompletionHandler<T> asyncCompletionHandler
    ) {
        final Completion completion = new Completion();
        takeLoop(channel, asyncCompletionHandler, completion);
        return completion.get();
    }

    /**
     * Take loop.
     */
    public static <T, I> CompletableFuture<Void> takeLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer
    ) {
        final Completion completion = new Completion();
        takeLoop(channel, (res, cmp) -> res.consumeRight(consumer), completion);
        return completion.get();
    }

    /**
     * Take loop.
     */
    public static <T, I> CompletableFuture<Void> takeLoop(
        IChannel<T, I> channel,
        BiConsumer<T, Completion> consumer
    ) {
        final Completion completion = new Completion();
        takeLoop(channel, (res, cmp) -> res.consumeRight(r -> consumer.accept(r, cmp)), completion);
        return completion.get();
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
