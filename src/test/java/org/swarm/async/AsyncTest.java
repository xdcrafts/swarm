package org.swarm.async;

import org.junit.Test;
import org.swarm.async.impl.Channel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.swarm.async.Async.pipe;
import static org.swarm.async.Async.putWhileLoop;
import static org.swarm.async.Async.takeWhileLoop;
import static org.swarm.commons.LangUtils.supply;

/**
 * Tests fro async channels.
 */
public class AsyncTest {

    private interface FreeRunnable {
        void run() throws Throwable;
    }

    private static void tryToRun(FreeRunnable runnable) throws Throwable {
        try {
            runnable.run();
        } catch (Throwable e) {
            throw e.getCause();
        }
    }

    public static <B, T> BiPredicate<B, T> takeN(int n) {
        return new BiPredicate<B, T>() {
            volatile int counter = 1;
            @Override
            public boolean test(B b, T t) {
                return counter++ < n;
            }
        };
    }

    @Test
    public void simpleTest() throws ExecutionException, InterruptedException {
        final IChannel<String, String> channel = Channel.<String>channel().get();
        channel.put(supply("value"));
        final String value = channel.take().get();
        assertEquals("value", value);
    }

    @Test(expected = TimeoutException.class)
    public void takeTimeoutTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().withTakeTimeout(Duration.ofMillis(10)).get();
        final CompletableFuture<String> value = channel.take();
        Thread.sleep(15);
        tryToRun(value::get);
    }

    @Test(expected = TimeoutException.class)
    public void putTimeoutTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().withPutTimeout(Duration.ofMillis(10)).get();
        channel.put(supply("value"));
        final CompletableFuture<Optional<Supplier<String>>> timedOut = channel.put(supply("value"));
        Thread.sleep(15);
        tryToRun(timedOut::get);
    }

    @Test(expected = AsyncException.class)
    public void putToClosedChannelTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().get();
        channel.close();
        final CompletableFuture<Optional<Supplier<String>>> failed = channel.put(supply("value"));
        tryToRun(failed::get);
    }

    @Test(expected = AsyncException.class)
    public void takeFromClosedChannelTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().get();
        channel.close();
        tryToRun(() -> channel.take().get());
    }

    @Test(expected = AsyncException.class)
    public void putWithFullQueueChannelTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().withMaxPutRequests(1).get();
        channel.put(supply("value")); // goes to buffer
        channel.put(supply("value")); // goes to queue
        tryToRun(() -> channel.put(supply("value")).get());
    }

    @Test(expected = AsyncException.class)
    public void takeWithFullQueueChannelTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().withMaxTakeRequests(1).get();
        channel.take();
        tryToRun(() -> channel.take().get());
    }

    @Test
    public void takeAndPutWhileTest() throws InterruptedException, ExecutionException {
        final IChannel<String, String> channel = Channel.<String>channel().get();
        final List<String> values = new ArrayList<>();
        final CompletableFuture<Void> first = takeWhileLoop(channel, values::add, takeN(8));
        putWhileLoop(channel, supply("value"), takeN(10));
        first.get();
        assertEquals(8, values.size());
        final CompletableFuture<Void> second = takeWhileLoop(channel, values::add, takeN(2));
        second.get();
        assertEquals(10, values.size());
    }

    @Test
    public void pipeTest() throws InterruptedException, ExecutionException {
        final IChannel<Integer, Integer> integerChannel = Channel.<Integer>channel().get();
        final IChannel<String, String> stringChannel = Channel.<String>channel().get();
        pipe(integerChannel, stringChannel, Object::toString);
        integerChannel.put(supply(0));
        assertEquals("0", stringChannel.take().get());
    }
}
