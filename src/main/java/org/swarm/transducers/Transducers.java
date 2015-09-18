package org.swarm.transducers;

import java.util.Collection;
import java.util.Optional;

/**
 * Transducers namespace.
 */
public final class Transducers {

    private Transducers() {
        // Nothing
    }

    /**
     * Simple IReduction implementation.
     * @param <T> value type
     */
    private static final class Reduction<T> implements IReduction<T> {
        private final T value;
        private final boolean isReduced;
        private final Optional<ReductionException> error;
        Reduction(T value, boolean isReduced, Optional<ReductionException> error) {
            this.value = value;
            this.isReduced = isReduced;
            this.error = error;
        }
        @Override
        public boolean isReduced() {
            return this.isReduced;
        }
        @Override
        public boolean isFailed() {
            return this.error.isPresent();
        }
        @Override
        public Optional<ReductionException> getError() {
            return this.error;
        }
        @Override
        public T get() {
            return this.value;
        }
    }

    /**
     * Creates new IReduction instance.
     */
    public static <T> IReduction<T> reduction(T value, boolean isReduced, Optional<ReductionException> error) {
        return new Reduction<>(value, isReduced, error);
    }

    /**
     * Applies given reducing function to current result and each T in input, using
     * the result returned from each reduction step as input to the next step. Returns
     * final result.
     */
    public static <T, R> IReduction<R> reduce(IReducer<R, T> reducer, R initValue, Iterable<T> input) {
        R result = initValue;
        try {
            for (T inputValue : input) {
                final IReduction<R> reduction = reducer.apply(result, inputValue);
                result = reduction.get();
                if (reduction.isFailed()) {
                    return reduction;
                }
                if (reduction.isReduced()) {
                    break;
                }
            }
            return reduction(reducer.complete(result), true, Optional.<ReductionException>empty());
        } catch (Throwable t) {
            return reduction(result, false, Optional.of(new ReductionException(t)));
        }
    }

    /**
     * Reduces input using transformed reducing function. Transforms reducing function by applying
     * transducer. Reducer must implement init function to start reducing process.
     */
    public static <R, A, B> IReduction<R> transduce(
        ITransducer<A, B> transducer, IReducer<R, A> reducer, Iterable<B> input
    ) {
        return reduce(transducer.apply(reducer), reducer.init().get().get(), input);
    }

    /**
     * Reduces input using transformed reducing function. Transforms reducing function by applying
     * transducer. Accepts initial value for reducing process as argument.
     */
    public static <R, A, B> IReduction<R> transduce(
        ITransducer<A, B> transducer, IReducer<R, A> reducer, R initialValue, Iterable<B> input
    ) {
        return reduce(transducer.apply(reducer), initialValue, input);
    }

    /**
     * Transduces input into collection using built-in reducing function.
     */
    public static <R extends Collection<A>, A, B> IReduction<R> into(
        ITransducer<A, B> transducer, R initialValue, Iterable<B> input
    ) {
        final IReducer<R, A> reductionFunction = (result, inputValue) -> {
            result.add(inputValue);
            return reduction(result, false, Optional.<ReductionException>empty());
        };
        return transduce(transducer, reductionFunction, initialValue, input);
    }

    /**
     * Composes a transducer with another transducer, yielding a new transducer.
     */
    public static <A, B, C> ITransducer<A, C> compose(final ITransducer<B, C> left, final ITransducer<A, B> right) {
        return left.compose(right);
    }

}
