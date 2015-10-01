package com.github.xdcrafts.swarm.async;

import java.util.Optional;

/**
 * Buffer interface.
 * @param <T> value type
 */
public interface IBuffer<T> {

    /**
     * Add new value to buffer. Returns true if successfully added.
     * @param value value of type T
     * @return  is add successful
     */
    boolean add(T value);

    /**
     * Takes value from buffer if present.
     * @return optional value
     */
    Optional<T> remove();

    /**
     * Is this buffer full.
     * @return boolean
     */
    boolean isFull();

    /**
     * Is this buffer empty.
     * @return boolean
     */
    boolean isEmpty();

    /**
     * Returns size of elements in buffer.
     * @return size
     */
    int size();
}
