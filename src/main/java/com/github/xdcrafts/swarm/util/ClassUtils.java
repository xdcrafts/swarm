package com.github.xdcrafts.swarm.util;

import java.util.Optional;

/**
 * Class utils.
 */
public class ClassUtils {

    /**
     * Cast object to clazz and returns Optional. If it can not be cast then the Optional is empty.
     * @param <T> type to cast to
     * @param o object to cast
     * @param clazz class to cast to
     * @return optional value of type T
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
