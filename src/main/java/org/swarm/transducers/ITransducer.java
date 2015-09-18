package org.swarm.transducers;

/**
 * Functional interface that transforms reduce function.
 * @param <A> result type of initial reduce function
 * @param <B> result type of transformed reduce function
 */
public interface ITransducer<A, B> {

    /**
     * Transforms reducing function.
     */
    <T> IReducer<T, B> apply(IReducer<T, ? super A> reducer);

    /**
     * Composes two transducers.
     */
    default <R> ITransducer<R, B> compose(final ITransducer<R, ? super A> transducer) {
        return new ITransducer<R, B>() {
            @Override
            public <V> IReducer<V, B> apply(IReducer<V, ? super R> reducer) {
                return ITransducer.this.apply(transducer.apply(reducer));
            }
        };
    }
}
