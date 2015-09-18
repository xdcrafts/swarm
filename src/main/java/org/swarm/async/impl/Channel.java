package org.swarm.async.impl;

import org.swarm.async.IBuffer;
import org.swarm.async.IChannel;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Implementation of IChannel.
 * @param <T> value type
 */
public class Channel<T> implements IChannel<T> {

    /**
     * Builder API for channel.
     * @param <T> value type
     */
    public static final class ChannelBuilder<T> implements Supplier<Channel<T>> {
        private volatile Executor executor = ForkJoinPool.commonPool();
        private volatile IBuffer<T> buffer = new FixedBuffer<>(1);
        private volatile int maxPutRequests = 16384;
        private volatile int maxTakeRequests = 16384;
        /**
         * Setup executor.
         */
        public ChannelBuilder withExecutor(Executor e) {
            this.executor = e;
            return this;
        }
        /**
         * Setup buffer.
         */
        public ChannelBuilder withBuffer(IBuffer<T> b) {
            this.buffer = b;
            return this;
        }
        /**
         * Setup capacity.
         */
        public ChannelBuilder withCapacity(int n) {
            this.buffer = new FixedBuffer<>(n);
            return this;
        }
        /**
         * Setup transducer.
         */
        public ChannelBuilder<T> withTransducer() {
            // todo this
            return this;
        }
        /**
         * Setup max put requests.
         */
        public ChannelBuilder withMaxPutRequests(int max) {
            this.maxPutRequests = max;
            return this;
        }
        /**
         * Setup max take requests.
         */
        public ChannelBuilder withMaxTakeRequests(int max) {
            this.maxTakeRequests = max;
            return this;
        }
        @Override
        public Channel<T> get() {
            return new Channel<>(this.executor, this.buffer, this.maxPutRequests, this.maxTakeRequests);
        }
    }

    /**
     * Container for put request data.
     */
    private final class PutRequest {
        final Supplier<T> supplier;
        final CompletableFuture<T> requestFuture;
        PutRequest(Supplier<T> supplier, CompletableFuture<T> requestFuture) {
            this.supplier = supplier;
            this.requestFuture = requestFuture;
        }
    }

    /**
     * Creates new channel instance.
     */
    public static <T> ChannelBuilder<T> channel() {
        return new ChannelBuilder<>();
    }

    private final Executor executor;

    private final IBuffer<T> buffer;

    private final ConcurrentLinkedDeque<PutRequest> putRequests = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<CompletableFuture<T>> takeRequests = new ConcurrentLinkedDeque<>();

    private final int maxPutRequests;
    private final int maxTakeRequests;

    private final AtomicInteger currentPutRequestsCount = new AtomicInteger();
    private final AtomicInteger currentTakeRequestsCount = new AtomicInteger();

    private volatile boolean isClosed = false;

    public Channel(Executor executor, IBuffer<T> buffer, int maxPutRequests, int maxTakeRequests) {
        this.executor = executor;
        this.buffer = buffer;
        this.maxPutRequests = maxPutRequests;
        this.maxTakeRequests = maxTakeRequests;
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
        final Optional<T> valueOption = this.buffer.remove();
        final CompletableFuture<T> takeRequest = new CompletableFuture<>();
        if (this.isClosed) {
            takeRequest.completeExceptionally(new Exception("Channel is closed!"));
        } else {
            if (valueOption.isPresent()) {
                Optional.ofNullable(this.putRequests.poll()).ifPresent(putRequest ->
                    CompletableFuture.supplyAsync(putRequest.supplier, this.executor).thenAccept(value -> {
                            final boolean isAdded = this.buffer.add(value);
                            if (isAdded) {
                                putRequest.requestFuture.complete(value);
                                this.currentPutRequestsCount.decrementAndGet();
                            } else {
                                this.putRequests.addFirst(putRequest);
                            }
                        })
                );
                takeRequest.complete(valueOption.get());
            } else {
                if (this.currentTakeRequestsCount.get() >= this.maxTakeRequests) {
                    takeRequest.completeExceptionally(new Exception("You can not pull!"));
                } else {
                    this.currentTakeRequestsCount.incrementAndGet();
                    this.takeRequests.offer(takeRequest);
                }
            }
        }
        return takeRequest;
    }

    @Override
    public synchronized CompletableFuture<T> put(Supplier<T> value) {
        final CompletableFuture<T> putRequest = new CompletableFuture<>();
        if (this.isClosed) {
            putRequest.completeExceptionally(new Exception("Channel is closed!"));
        } else {
            if (this.buffer.isFull()) {
                if (this.currentPutRequestsCount.get() < this.maxPutRequests) {
                    this.currentPutRequestsCount.incrementAndGet();
                    this.putRequests.offer(new PutRequest(value, putRequest));
                } else {
                    putRequest.completeExceptionally(new Exception("You can not put!"));
                }
            } else {
                CompletableFuture.supplyAsync(value, this.executor).thenAccept(valueToAdd -> {
                        final boolean isAdded = this.buffer.add(valueToAdd);
                        if (isAdded) {
                            putRequest.complete(valueToAdd);
                            final Optional<CompletableFuture<T>> takeRequestOption =
                                Optional.ofNullable(this.takeRequests.poll());
                            takeRequestOption.ifPresent(takeRequest -> {
                                    final Optional<T> valueFromBuffer = this.buffer.remove();
                                    if (valueFromBuffer.isPresent()) {
                                        takeRequest.complete(valueFromBuffer.get());
                                        this.currentTakeRequestsCount.decrementAndGet();
                                    } else {
                                        this.takeRequests.addFirst(takeRequest);
                                    }
                                });
                        } else {
                            if (this.currentPutRequestsCount.get() < this.maxPutRequests) {
                                this.currentPutRequestsCount.incrementAndGet();
                                this.putRequests.offer(new PutRequest(value, putRequest));
                            } else {
                                putRequest.completeExceptionally(new Exception("You can not put!"));
                            }
                        }
                    });
            }
        }
        return putRequest;
    }

}
