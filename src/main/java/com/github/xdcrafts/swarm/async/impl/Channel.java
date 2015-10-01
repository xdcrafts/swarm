package com.github.xdcrafts.swarm.async.impl;

import com.github.xdcrafts.swarm.async.AsyncException;
import com.github.xdcrafts.swarm.async.IBuffer;
import com.github.xdcrafts.swarm.async.IChannel;
import com.github.xdcrafts.swarm.transducers.IReducer;
import com.github.xdcrafts.swarm.transducers.ITransducer;
import com.github.xdcrafts.swarm.transducers.Implementations;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.github.xdcrafts.swarm.transducers.Reduction.reduction;
import static com.github.xdcrafts.swarm.util.FutureUtils.within;

/**
 * Implementation of IChannel.
 * @param <T> value type
 * @param <I> input value type
 */
public final class Channel<T, I> implements IChannel<T, I> {

    /**
     * Builder API for channel.
     * @param <T> value type
     * @param <I> input value type
     */
    public static final class ChannelBuilder<T, I> implements Supplier<Channel<T, I>> {
        private final ITransducer<Supplier<T>, Supplier<I>> transducer;
        private volatile Executor executor = ForkJoinPool.commonPool();
        private volatile IBuffer<Supplier<T>> buffer = new FixedBuffer<>(1);
        private volatile int maxPutRequests = 16384;
        private volatile int maxTakeRequests = 16384;
        private volatile Duration takeDuration = Duration.ofSeconds(1);
        private volatile Duration putDuration = Duration.ofSeconds(1);
        public ChannelBuilder(ITransducer<Supplier<T>, Supplier<I>> transducer) {
            this.transducer = transducer;
        }
        /**
         * Setup executor.
         * @param e executor
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withExecutor(Executor e) {
            this.executor = e;
            return this;
        }
        /**
         * Setup buffer.
         * @param b buffer
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withBuffer(IBuffer<Supplier<T>> b) {
            this.buffer = b;
            return this;
        }
        /**
         * Setup capacity.
         * @param n size of fixed buffer
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withCapacity(int n) {
            this.buffer = new FixedBuffer<>(n);
            return this;
        }
        /**
         * Setup max put requests.
         * @param  max max number of put requests
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withMaxPutRequests(int max) {
            this.maxPutRequests = max;
            return this;
        }
        /**
         * Setup max take requests.
         * @param  max max number of take requests
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withMaxTakeRequests(int max) {
            this.maxTakeRequests = max;
            return this;
        }
        /**
         * Setup take timeout.
         * @param timeout take timeout duration
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withTakeTimeout(Duration timeout) {
            this.takeDuration = timeout;
            return this;
        }
        /**
         * Setup put timeout.
         * @param timeout put timeout duration
         * @return this builder instance
         */
        public ChannelBuilder<T, I> withPutTimeout(Duration timeout) {
            this.putDuration = timeout;
            return this;
        }
        @Override
        public Channel<T, I> get() {
            return new Channel<>(
                this.executor,
                this.buffer,
                this.transducer,
                this.maxPutRequests,
                this.maxTakeRequests,
                this.takeDuration,
                this.putDuration
            );
        }
    }

    /**
     * Container for put request data.
     */
    private final class PutRequest {
        final Supplier<T> supplier;
        final CompletableFuture<Optional<Supplier<T>>> requestFuture;
        PutRequest(Supplier<T> supplier, CompletableFuture<Optional<Supplier<T>>> requestFuture) {
            this.supplier = supplier;
            this.requestFuture = requestFuture;
        }
    }

    /**
     * Creates new channel instance.
     * @param <T> channel values type
     * @return new channel builder from T to T
     */
    public static <T> ChannelBuilder<T, T> channel() {
        return new ChannelBuilder<>(Implementations.id());
    }

    /**
     * Creates new channel instance.
     * @param transducer transducer
     * @param <T> channel result type
     * @param <I> channel input type
     * @return new channel builder from I to T
     */
    public static <T, I> ChannelBuilder<T, I> channel(ITransducer<Supplier<T>, Supplier<I>> transducer) {
        return new ChannelBuilder<>(transducer);
    }

    private final IReducer<CompletableFuture<Optional<Supplier<T>>>, Supplier<I>> transducedReducer;

    private final Executor executor;

    private final IBuffer<Supplier<T>> buffer;

    private final ConcurrentLinkedDeque<PutRequest> putRequests = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<CompletableFuture<T>> takeRequests = new ConcurrentLinkedDeque<>();

    private final int maxPutRequests;
    private final int maxTakeRequests;

    private final Duration takeTimeout;
    private final Duration putTimeout;

    private final AtomicInteger currentPutRequestsCount = new AtomicInteger();
    private final AtomicInteger currentTakeRequestsCount = new AtomicInteger();

    private volatile boolean isClosed = false;

    private Channel(
        Executor executor,
        IBuffer<Supplier<T>> buffer,
        ITransducer<Supplier<T>, Supplier<I>> transducer,
        int maxPutRequests,
        int maxTakeRequests,
        Duration takeTimeout,
        Duration putTimeout
    ) {
        this.executor = executor;
        this.buffer = buffer;
        this.maxPutRequests = maxPutRequests;
        this.maxTakeRequests = maxTakeRequests;
        this.takeTimeout = takeTimeout;
        this.putTimeout = putTimeout;
        this.transducedReducer = transducer.apply((resultFuture, inputSupplier) -> {
                final CompletableFuture<Optional<Supplier<T>>> putRequest = within(
                    new CompletableFuture<>(), this.putTimeout);
                if (this.buffer.isFull()) {
                    if (this.currentPutRequestsCount.get() < this.maxPutRequests) {
                        this.currentPutRequestsCount.incrementAndGet();
                        this.putRequests.offer(new PutRequest(inputSupplier, putRequest));
                    } else {
                        putRequest.completeExceptionally(new AsyncException("Request queue is full."));
                    }
                } else {
                    final boolean isAdded = this.buffer.add(inputSupplier);
                    if (isAdded) {
                        putRequest.complete(Optional.of(inputSupplier));
                        CompletableFuture<T> takeRequest = this.takeRequests.poll();
                        while (takeRequest != null && takeRequest.isDone()) {
                            takeRequest = this.takeRequests.poll();
                        }
                        Optional.ofNullable(takeRequest).ifPresent(request -> {
                                final Optional<Supplier<T>> valueFromBuffer = this.buffer.remove();
                                if (valueFromBuffer.isPresent()) {
                                    CompletableFuture.supplyAsync(valueFromBuffer.get(), this.executor)
                                            .whenComplete((res, exc) -> {
                                                    if (res != null) {
                                                        request.complete(res);
                                                    } else {
                                                        request.completeExceptionally(exc);
                                                    }
                                                });
                                    this.currentTakeRequestsCount.decrementAndGet();
                                } else {
                                    this.takeRequests.addFirst(request);
                                }
                            });
                    } else {
                        if (this.currentPutRequestsCount.get() < this.maxPutRequests) {
                            this.currentPutRequestsCount.incrementAndGet();
                            this.putRequests.offer(new PutRequest(inputSupplier, putRequest));
                        } else {
                            putRequest.completeExceptionally(new AsyncException("Request queue is full."));
                        }
                    }
                }
                return reduction(putRequest);
            });
    }

    @Override
    public void close() {
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public synchronized CompletableFuture<T> take() {
        final Optional<Supplier<T>> valueOption = this.buffer.remove();
        final CompletableFuture<T> takeRequest = within(new CompletableFuture<>(), this.takeTimeout);
        if (this.isClosed) {
            takeRequest.completeExceptionally(new AsyncException("Channel is closed."));
        } else {
            if (valueOption.isPresent()) {
                final Supplier<T> valueSupplier = valueOption.get();
                PutRequest putRequest = this.putRequests.poll();
                while (putRequest != null && putRequest.requestFuture.isDone()) {
                    putRequest = this.putRequests.poll();
                }
                Optional.ofNullable(putRequest).ifPresent(request -> {
                        final boolean isAdded = this.buffer.add(valueSupplier);
                        if (isAdded) {
                            request.requestFuture.complete(Optional.of(valueSupplier));
                            this.currentPutRequestsCount.decrementAndGet();
                        } else {
                            this.putRequests.addFirst(request);
                        }
                    });
                CompletableFuture.supplyAsync(valueOption.get(), this.executor).whenComplete((res, exc) -> {
                        if (res != null) {
                            takeRequest.complete(res);
                        } else {
                            takeRequest.completeExceptionally(exc);
                        }
                    });
            } else {
                if (this.currentTakeRequestsCount.get() >= this.maxTakeRequests) {
                    takeRequest.completeExceptionally(new AsyncException("Take queue is full."));
                } else {
                    this.currentTakeRequestsCount.incrementAndGet();
                    this.takeRequests.offer(takeRequest);
                }
            }
        }
        return takeRequest;
    }

    @Override
    public synchronized CompletableFuture<Optional<Supplier<T>>> put(Supplier<I> value) {
        final CompletableFuture<Optional<Supplier<T>>> putRequest = new CompletableFuture<>();
        if (this.isClosed) {
            putRequest.completeExceptionally(new AsyncException("Channel is closed."));
        } else {
            this.transducedReducer.apply(CompletableFuture.completedFuture(Optional.empty()), value).get()
                .whenComplete((res, exc) -> {
                        if (res != null) {
                            putRequest.complete(res);
                        } else {
                            putRequest.completeExceptionally(exc);
                        }
                    });
        }
        return putRequest;
    }

}
