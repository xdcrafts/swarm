package org.swarm.async;

import org.swarm.async.impl.Channel;
import org.swarm.commons.FutureUtils;

import java.time.Duration;

import static org.swarm.commons.LangUtils.mapSupply;
import static org.swarm.commons.LangUtils.supply;
import static org.swarm.transducers.Implementations.map;

/**
 * ChannelUsage.
 */
public class ChannelUsage {

    // todo move to tests

    /**
     * Main.
     */
    public static void main(String[] args) throws InterruptedException {
        final IChannel<String, Integer> channel = Channel
            .<String, Integer>channel(map(mapSupply(Object::toString))).get();
        Async.putLoop(channel, () -> {
                System.err.println("SUPPLY");
                return 42;
            });
        Async.takeLoop(channel, string -> {
                System.out.println(string);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        final IChannel<Integer, String> lengthChannel = Channel
            .channel(map(mapSupply(String::length))).get();
        Async.takeLoop(lengthChannel, length -> {
                System.err.println("String length: " + length);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        Async.pipe(channel, lengthChannel);

        channel.take().whenComplete((s, e) -> System.out.println(s + " - " + e));
        channel.take().whenComplete((s, e) -> System.out.println(s + " - " + e));
        channel.take().whenComplete((s, e) -> System.out.println(s + " - " + e));
        channel.put(supply(1));
        channel.put(supply(2)).whenComplete((s, e) -> System.out.println("put " + s + " - " + e));
        FutureUtils.within(channel.put(supply(3)), Duration.ofMillis(100));
        Thread.sleep(1000L);
        channel.take().whenComplete((s, e) -> System.out.println(s + " - " + e));
        channel.close();
        channel.take().whenComplete((s, e) -> System.out.println(s + " - " + e));
        Thread.sleep(1000L);
    }
}
