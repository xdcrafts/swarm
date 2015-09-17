package org.swarm.transducers;

/**
 * Functional interface that transforms reducer function.
 * @param <A> result type of initial reducer function
 * @param <B> result type of transformed reducer function
 */
public interface ITransducer<A, B> {

    /**
     * Transforms reducing function.
     */
    <T> IReducer<T, B> apply(IReducer<T, A> reducer);

    /**
     * Composes two transducers.
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
