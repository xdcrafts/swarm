package com.github.xdcrafts.swarm.commons;

import java.util.Optional;

/**
 * Class utils.
 * @author ipogudin
 *
 */
public class ClassUtils {

    /**
     * Cast object to clazz and returns Optional. If it can not be cast then the Optional is empty.
     * @param o
     * @param clazz
     * @return
     */
    public static <T> Optional<T> cast(Object o, Class<T> clazz) {
        T result = null;
        /**
         * There is no null pointer checking for clazz to detect logic errors.
         */
        if (o != null && clazz.isInstance(o)) {
            result = (T) o;
        }
        return Optional.ofNullable(result);
    }
}
