package com.github.xdcrafts.swarm.async;

import com.github.xdcrafts.swarm.async.impl.Channel;
import com.github.xdcrafts.swarm.javaz.trym.ITryM;
import com.github.xdcrafts.swarm.util.LangUtils;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * Tests for async channels.
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

    public static <B> Async.AsyncCompletionHandler<B> putN(int n) {
        return new Async.AsyncCompletionHandler<B>() {
            volatile int counter = 1;
            @Override
            public void handle(ITryM<B> result, Async.Completion completion) {
                if (counter++ >= n) {
                    completion.done();
                }
            }
        };
    }

    public static <B> Async.AsyncCompletionHandler<B> takeN(int n, Consumer<B> consumer) {
        return new Async.AsyncCompletionHandler<B>() {
            volatile int counter = 1;
            @Override
            public void handle(ITryM<B> result, Async.Completion completion) {
                result.foreach(consumer);
                if (counter++ >= n) {
                    completion.done();
                }
            }
        };
    }

    @Test
    public void simpleTest() throws ExecutionException, InterruptedException {
        final IChannel<String, String> channel = Channel.<String>channel().get();
        channel.put(LangUtils.supply("value"));
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
        channel.put(LangUtils.supply("value"));
        final CompletableFuture<Optional<Supplier<String>>> timedOut = channel.put(LangUtils.supply("value"));
        Thread.sleep(15);
        tryToRun(timedOut::get);
    }

    @Test(expected = AsyncException.class)
    public void putToClosedChannelTest() throws Throwable {
        final IChannel<String, String> channel = Channel.<String>channel().get();
        channel.close();
        final CompletableFuture<Optional<Supplier<String>>> failed = channel.put(LangUtils.supply("value"));
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
        channel.put(LangUtils.supply("value")); // goes to buffer
        channel.put(LangUtils.supply("value")); // goes to queue
        tryToRun(() -> channel.put(LangUtils.supply("value")).get());
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
        final Async.Completion first = Async.takeLoop(channel, takeN(8, values::add));
        Async.putLoop(channel, LangUtils.supply("value"), putN(10));
        first.await();
        assertEquals(8, values.size());
        final Async.Completion second = Async.takeLoop(channel, takeN(2, values::add));
        second.await();
        assertEquals(10, values.size());
    }

    @Test
    public void pipeTest() throws InterruptedException, ExecutionException {
        final IChannel<Integer, Integer> integerChannel = Channel.<Integer>channel().get();
        final IChannel<String, String> stringChannel = Channel.<String>channel().get();
        Async.pipe(integerChannel, stringChannel, Object::toString);
        integerChannel.put(LangUtils.supply(0));
        assertEquals("0", stringChannel.take().get());
    }
}
