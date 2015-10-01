package com.github.xdcrafts.swarm.util;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

/**
 * Collection utils.
 */
public class CollectionUtils {

    /**
     * Wait while collection is not empty with timeout.
     * @param c collection
     * @param timeout in milliseconds
     * @param step step to sleep during waiting
     * @throws InterruptedException if something fails
     * @throws TimeoutException if something fails
     */
    public static void waitWhileCollectionIsNotEmpty(
            Collection<?> c, long timeout, long step) throws InterruptedException, TimeoutException {
        long t1 = System.currentTimeMillis();
        while (c.size() > 0 && timeout > 0) {
            Thread.sleep(step);
            timeout -= System.currentTimeMillis() - t1;
        }

        if (timeout < 1) {
            throw new TimeoutException();
        }
    }

    /**
     * Wait while collection is not empty with timeout.
     * @param c collection
     * @param timeout in milliseconds
     * @throws InterruptedException if something fails
     * @throws TimeoutException if something fails
     */
    public static void waitWhileCollectionIsNotEmpty(
            Collection<?> c, long timeout) throws InterruptedException, TimeoutException {
        final long defaultStep = 50; //milliseconds
        waitWhileCollectionIsNotEmpty(c, timeout, defaultStep);
    }
}
