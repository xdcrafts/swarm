package org.swarm.transducers;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.swarm.transducers.Transducers.reduce;
import static org.swarm.transducers.Transducers.reduction;

/**
 * Class that contains basic transducers implementations.
 */
public final class Implementations {

    private Implementations() {
        // Nothing
    }

    /**
     * Creates a transducer that transforms a reducing function by applying a mapping
     * function to each input.
     */
    public static <A, B> ITransducer<A, B> map(final Function<B, A> function) {
        return new ITransducer<A, B>() {
            @Override
            public <T> IReducer<T, B> apply(IReducer<T, A> reducer) {
                return (result, input) -> {
                        try {
                            return reducer.apply(result, function.apply(input));
                        } catch (Throwable t) {
                            return reduction(result, false, Optional.of(new ReductionException(t)));
                        }
                    };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function by applying a
     * predicate to each input and processing only those inputs for which the
     * predicate is true.
     */
    public static <A> ITransducer<A, A> filter(final Predicate<A> predicate) {
        return new ITransducer<A, A>() {
            @Override
            public <R> IReducer<R, A> apply(IReducer<R, A> reducer) {
                return  (result, input) -> {
                        try {
                            return predicate.test(input)
                                ? reducer.apply(result, input)
                                : reduction(result, false, Optional.<ReductionException>empty());
                        } catch (Throwable t) {
                            return reduction(result, false, Optional.of(new ReductionException(t)));
                        }
                    };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function by accepting
     * an iterable of the expected input type and reducing it.
     */
    public static <A, B extends Iterable<A>> ITransducer<A, B> cat() {
        return new ITransducer<A, B>() {
            @Override
            public <T> IReducer<T, B> apply(IReducer<T, A> reducer) {
                return (result, input) -> reduce(reducer, result, input);
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function using
     * a composition of map and cat.
     */
    public static <A, B extends Iterable<A>, C> ITransducer<A, C> mapcat(Function<C, B> f) {
        return map(f).compose(Implementations.<A, B>cat());
    }

    /**
     * Creates a transducer that transforms a reducing function by applying a
     * predicate to each input and not processing those inputs for which the
     * predicate is true.
     */
    public static <A> ITransducer<A, A> remove(final Predicate<A> predicate) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return (result, input) -> {
                        try {
                            return predicate.test(input)
                                ? reduction(result, false, Optional.<ReductionException>empty())
                                : reducer.apply(result, input);
                        } catch (Throwable t) {
                            return reduction(result, false, Optional.of(new ReductionException(t)));
                        }
                    };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * it only processes n inputs, then the reducing process stops.
     */
    public static <A> ITransducer<A, A> take(final long n) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return new IReducer<T, A>() {
                    long counter = 0;
                    @Override
                    public IReduction<T> apply(T result, A input) {
                        IReduction<T> reductionResult = reduction(result, false, Optional.<ReductionException>empty());
                        try {
                            if (counter < n) {
                                reductionResult = reducer.apply(result, input);
                            } else {
                                reductionResult =
                                    reduction(reductionResult.get(), true, Optional.<ReductionException>empty());
                            }
                            return reductionResult;
                        } catch (Throwable t) {
                            return reduction(result, false, Optional.of(new ReductionException(t)));
                        }
                    }
                };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * it processes inputs as long as the provided predicate returns true.
     * If the predicate returns false, the reducing process stops.
     */
    public static <A> ITransducer<A, A> takeWhile(final Predicate<A> predicate) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return (result, input) -> {
                    IReduction<T> reductionResult = reduction(result, false, Optional.<ReductionException>empty());
                    try {
                        if (predicate.test(input)) {
                            reductionResult = reducer.apply(result, input);
                        } else {
                            reductionResult =
                                    reduction(reductionResult.get(), true, Optional.<ReductionException>empty());
                        }
                        return reductionResult;
                    } catch (Throwable t) {
                        return reduction(result, false, Optional.of(new ReductionException(t)));
                    }
                };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * it skips n inputs, then processes the rest of the inputs.
     */
    public static <A> ITransducer<A, A> drop(final long n) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return new IReducer<T, A>() {
                    long counter = 0;
                    @Override
                    public IReduction<T> apply(T result, A input) {
                        IReduction<T> reductionResult = reduction(result, false, Optional.<ReductionException>empty());
                        try {
                            if (counter < n) {
                                counter++;
                            } else {
                                reductionResult = reducer.apply(result, input);
                            }
                            return reductionResult;
                        } catch (Throwable t) {
                            return reduction(result, false, Optional.of(new ReductionException(t)));
                        }
                    }
                };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * it skips inputs as long as the provided predicate returns true.
     * Once the predicate returns false, the rest of the inputs are
     * processed.
     */
    public static <A> ITransducer<A, A> dropWhile(final Predicate<A> predicate) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return (result, input) -> {
                    IReduction<T> reductionResult = reduction(result, false, Optional.<ReductionException>empty());
                    try {
                        if (!predicate.test(input)) {
                            reductionResult = reducer.apply(result, input);
                        }
                        return reductionResult;
                    } catch (Throwable t) {
                        return reduction(result, false, Optional.of(new ReductionException(t)));
                    }
                };
            }
        };
    }
}
