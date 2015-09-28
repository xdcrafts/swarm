package org.swarm.commons;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

/**
 * Collection utils.
 * @author ipogudin
 *
 */
public class CollectionUtils {

    /**
     * Wait while collection is not empty with timeout.
     * @param c
     * @param timeout in milliseconds
     * @param step step to sleep during waiting
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
     * @param c
     * @param timeout in milliseconds
     * @throws InterruptedException
     */
    public static void waitWhileCollectionIsNotEmpty(
            Collection<?> c, long timeout) throws InterruptedException, TimeoutException {
        final long defaultStep = 50; //milliseconds
        waitWhileCollectionIsNotEmpty(c, timeout, defaultStep);
    }
}
