package com.github.xdcrafts.swarm.javaz.common.functor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Functor interface that can map function over it's content.
 * @param <T> content type
 * @param <M> functor type
 */
public interface IFunctor<T, M extends IFunctor<?, M>> {
    /**
     * Maps function over functor's content and returns new functor with value of type U.
     * @param function mapper
     * @param <U> new value type
     * @return new functor with value of type U
     */
    <U> M map(Function<T, U> function);
    /**
     * Applies function to content of this functor.
     * @param function consumer of value of type T
     */
    void foreach(Consumer<T> function);
}
