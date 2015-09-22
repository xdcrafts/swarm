package org.swarm.transducers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.swarm.transducers.Reduction.reduction;

/**
 * Transducers namespace.
 */
public final class Transducers {

    private Transducers() {
        // Nothing
    }

    /**
     * Constructs new reducer.
     */
    public static <T, V, R> IReducer<R, T> reducer(
        IReducer<R, V> reducer, BiFunction<R, T, Reduction<R>> reductionBiFunction
    ) {
        return new IReducer<R, T>() {
            @Override
            public Optional<R> init() {
                return reducer.init();
            }
            @Override
            public Reduction<R> complete(Reduction<R> reduction) {
                return reducer.complete(reduction);
            }
            @Override
            public Reduction<R> apply(R result, T input) {
                return reductionBiFunction.apply(result, input);
            }
        };
    }

    /**
     * Applies given reducing function to current result and each T in input, using
     * the result returned from each reduction step as input to the next step. Returns
     * final result.
     */
    public static <T, R> Reduction<R> reduce(IReducer<R, ? super T> reducer, R initValue, Iterable<T> input) {
        R result = initValue;
        try {
            for (T inputValue : input) {
                final Reduction<R> reduction = reducer.apply(result, inputValue);
                result = reduction.get();
                if (reduction.isFailed()) {
                    return reduction;
                }
                if (reduction.isReduced()) {
                    break;
                }
            }
            return reducer.complete(reduction(result).setIsReduced(true));
        } catch (Throwable t) {
            return reduction(result).setReductionException(new ReductionException(t));
        }
    }

    /**
     * Reduces input using transformed reducing function. Transforms reducing function by applying
     * transducer. Reducer must implement init function to start reducing process.
     */
    public static <R, A, B> Reduction<R> transduce(
        ITransducer<A, B> transducer, IReducer<R, A> reducer, Iterable<B> input
    ) {
        return reduce(transducer.apply(reducer), reducer.init().get(), input);
    }

    /**
     * Reduces input using transformed reducing function. Transforms reducing function by applying
     * transducer. Accepts initial value for reducing process as argument.
     */
    public static <R, A, B> Reduction<R> transduce(
        ITransducer<A, B> transducer, IReducer<R, A> reducer, R initialValue, Iterable<B> input
    ) {
        return reduce(transducer.apply(reducer), initialValue, input);
    }

    /**
     * Transduces input into collection using built-in reducing function.
     */
    public static <R extends Collection<A>, A, B> Reduction<R> into(
        ITransducer<A, B> transducer, R initialValue, Iterable<B> input
    ) {
        final IReducer<R, A> reductionFunction = (result, inputValue) -> {
            result.add(inputValue);
            return reduction(result);
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
