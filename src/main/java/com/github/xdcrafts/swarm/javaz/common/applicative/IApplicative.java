package com.github.xdcrafts.swarm.javaz.common.applicative;

import com.github.xdcrafts.swarm.javaz.common.functor.IFunctor;

import java.util.function.Function;

/**
 * Interface of Applicative.
 * @param <T> value type
 * @param <M> concrete applicative type
 */
public interface IApplicative<T, M extends IApplicative<?, M>> extends IFunctor<T, M> {

    /**
     * Maps function within applicative on value of type T within this
     * applicative and produces new applicative with value of type U.
     * @param applicativeFunction function within applicative container
     * @param <U> new content type
     * @param <MM> type of function within applicative
     * @return new applicative of type M[U]
     */
    <U, MM extends IApplicative<Function<T, U>, M>> M applicativeMap(MM applicativeFunction);
}
