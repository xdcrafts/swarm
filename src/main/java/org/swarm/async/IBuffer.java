package org.swarm.async;

import java.util.Optional;

/**
 * Buffer interface.
 * @param <T> value type
 */
public interface IBuffer<T> {

    /**
     * Add new value to buffer. Returns true if successfully added.
     */
    boolean add(T value);

    /**
     * Takes value from buffer if present.
     */
    Optional<T> remove();

    /**
     * Is this buffer full.
     */
    boolean isFull();

    /**
     * Is this buffer empty.
     */
    boolean isEmpty();

    /**
     * Returns size of elements in buffer.
     */
    int size();
}
