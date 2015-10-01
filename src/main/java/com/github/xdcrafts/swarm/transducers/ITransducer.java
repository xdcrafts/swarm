package com.github.xdcrafts.swarm.transducers;

/**
 * Functional interface that transforms reduce function.
 * @param <A> result type of initial reduce function
 * @param <B> result type of transformed reduce function
 */
public interface ITransducer<A, B> {

    /**
     * Transforms reducing function.
     * @param reducer reducing function
     * @param <T> reduction result type
     * @return new reducer function
     */
    <T> IReducer<T, B> apply(IReducer<T, A> reducer);

    /**
     * Composes two transducers.
     * @param transducer another transducer
     * @param <R> type
     * @return new transducer
     */
    default <R> ITransducer<R, B> compose(final ITransducer<R, A> transducer) {
        return new ITransducer<R, B>() {
            @Override
            public <V> IReducer<V, B> apply(IReducer<V, R> reducer) {
                return ITransducer.this.apply(transducer.apply(reducer));
            }
        };
    }
}
