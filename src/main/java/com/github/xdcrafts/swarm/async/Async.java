package com.github.xdcrafts.swarm.async;

import com.github.xdcrafts.swarm.javaz.trym.ITryM;
import com.github.xdcrafts.swarm.javaz.trym.TryMOps;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
     * Completion hook that performs control over execution of async processes.
     */
    public static class Completion {
        private final CompletableFuture<Void> completion = new CompletableFuture<>();
        private volatile boolean isDone = false;
        /**
         * Causes completion of async process.
         */
        public void done() {
            this.isDone = true;
        }
        private boolean isDone() {
            return this.isDone;
        }
        /**
         * Completes with exception.
         * @param error exception
         */
        private void exceptionally(Throwable error) {
            this.isDone = true;
            this.completion.completeExceptionally(error);
        }
        /**
         * Completes normally.
         */
        private void complete() {
            this.isDone = true;
            this.completion.complete(null);
        }
        public boolean isCompleted() {
            return this.completion.isDone();
        }
        /**
         * Blocking till this completion isDone.
         * @throws ExecutionException in case of async exceptions
         * @throws InterruptedException in case of async exceptions
         */
        public void await() throws ExecutionException, InterruptedException {
            this.completion.get();
        }
        /**
         * Attaches completion consumer that is able to handle possible errors.
         * @param completionHandler callback
         */
        public void whenComplete(Consumer<Optional<Throwable>> completionHandler) {
            this.completion.whenComplete((res, err) -> completionHandler.accept(ofNullable(err)));
        }
    }

    /**
     * Handler interface for async result of completable future and completion hook.
     * @param <V> value type
     */
    public interface AsyncCompletionHandler<V> {
        /**
         * Handles async result with completion hook.
         * @param result result of async computation
         * @param completion completion hook
         */
        void handle(ITryM<V> result, Completion completion);
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
                        final ITryM<Supplier<T>> result = res != null && res.isPresent()
                            ? TryMOps.success(res.get()) : TryMOps.fail(err);
                        asyncCompletionHandler.handle(result, completion);
                    } catch (Throwable t) {
                        completion.exceptionally(t);
                        return;
                    }
                    if (!completion.isDone()) {
                        putLoop(channel, supplier, asyncCompletionHandler, completion);
                    } else {
                        completion.complete();
                    }
                });
        } else {
            completion.complete();
        }
    }

    /**
     * Put loop with specified channel, supplier and async completion handler.
     * Interrupts on channel close or explicit completion complete.
     * @param channel async channel
     * @param supplier supplier function
     * @param asyncCompletionHandler completion handler
     * @param <T> channel value type
     * @param <I> channel input type
     * @return completion hook
     */
    public static <T, I> Completion putLoop(
        IChannel<T, I> channel,
        Supplier<I> supplier,
        AsyncCompletionHandler<Supplier<T>> asyncCompletionHandler
    ) {
        final Completion completion = new Completion();
        putLoop(channel, supplier, asyncCompletionHandler, completion);
        return completion;
    }

    /**
     * Put loop with specified channel, supplier and async completion handler.
     * Interrupts on channel close or explicit completion complete.
     * @param channel async channel
     * @param supplier supplier function
     * @param <T> channel value type
     * @param <I> channel input type
     * @return completion hook
     */
    public static <T, I> Completion putLoop(
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
        return completion;
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
                        final ITryM<T> result = res != null ? TryMOps.success(res) : TryMOps.fail(err);
                        asyncCompletionHandler.handle(result, completion);
                    } catch (Throwable t) {
                        completion.exceptionally(t);
                        return;
                    }
                    if (!completion.isDone()) {
                        takeLoop(channel, asyncCompletionHandler, completion);
                    } else {
                        completion.complete();
                    }
                });
        } else {
            completion.complete();
        }
    }

    /**
     * Loops take for specified channel with async completion handler.
     * Interrupts on channel close or explicit completion complete.
     * @param channel async channel
     * @param asyncCompletionHandler async handler
     * @param <T> channel values type
     * @param <I> channel input type
     * @return completion hook
     */
    public static <T, I> Completion takeLoop(
        IChannel<T, I> channel,
        AsyncCompletionHandler<T> asyncCompletionHandler
    ) {
        final Completion completion = new Completion();
        takeLoop(channel, asyncCompletionHandler, completion);
        return completion;
    }

    /**
     * Loops take for specified channel with async completion handler.
     * Interrupts on channel close or explicit completion complete.
     * @param channel async channel
     * @param consumer async handler
     * @param <T> channel values type
     * @param <I> channel input type
     * @return completion hook
     */
    public static <T, I> Completion takeLoop(
        IChannel<T, I> channel,
        Consumer<T> consumer
    ) {
        final Completion completion = new Completion();
        takeLoop(channel, (res, cmp) -> res.foreach(consumer), completion);
        return completion;
    }

    /**
     * Loops take for specified channel with async completion handler.
     * Interrupts on channel close or explicit completion complete.
     * @param channel async channel
     * @param consumer async handler
     * @param <T> channel values type
     * @param <I> channel input type
     * @return completion hook
     */
    public static <T, I> Completion takeLoop(
        IChannel<T, I> channel,
        BiConsumer<T, Completion> consumer
    ) {
        final Completion completion = new Completion();
        takeLoop(channel, (res, cmp) -> res.foreach(r -> consumer.accept(r, cmp)), completion);
        return completion;
    }

    /**
     * Pipe.
     */
    private static <R, V, T, I> void pipe(
        IChannel<T, I> left,
        IChannel<R, V> right,
        Function<T, V> mapper,
        Completion completion
    ) {
        if (!left.isClosed() && !right.isClosed()) {
            left.take().whenComplete((res, err) -> {
                    CompletableFuture<Optional<Supplier<R>>> put = CompletableFuture.completedFuture(empty());
                    if (res != null) {
                        put = right.put(() -> mapper.apply(res));
                    }
                    put.whenComplete((putRes, putErr) -> {
                            if (!completion.isDone()) {
                                pipe(left, right, mapper);
                            }
                        });
                });
        } else {
            completion.complete();
        }
    }

    /**
     * Pipes all requests from left channel to right channel via mapping function.
     * @param left async channel
     * @param right async channel
     * @param mapper function from T to V
     * @param <R> right channel values type
     * @param <V> right channel input type
     * @param <T> left channel values type
     * @param <I> left channel input type
     * @return completion hook
     */
    public static <R, V, T, I> Completion pipe(
        IChannel<T, I> left,
        IChannel<R, V> right,
        Function<T, V> mapper
    ) {
        final Completion completion = new Completion();
        pipe(left, right, mapper, completion);
        return completion;
    }

    /**
     * Pipes all requests from left channel to right channel.
     * @param left async channel
     * @param right async channel
     * @param <R> right channel values type
     * @param <T> left channel values type
     * @param <I> left channel input type
     * @return completion hook
     */
    public static <R, T, I> Completion pipe(IChannel<T, I> left, IChannel<R, T> right) {
        return pipe(left, right, Function.<T>identity());
    }
}
