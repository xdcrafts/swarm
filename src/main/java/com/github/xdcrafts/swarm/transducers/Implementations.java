package com.github.xdcrafts.swarm.transducers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.xdcrafts.swarm.transducers.Reduction.reduction;
import static com.github.xdcrafts.swarm.transducers.Transducers.reduce;
import static com.github.xdcrafts.swarm.transducers.Transducers.reducer;

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
    public static <A> ITransducer<A, A> id() {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return reducer;
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function by applying a mapping
     * function to each input.
     */
    public static <A, B> ITransducer<A, B> map(final Function<B, ? extends A> function) {
        return new ITransducer<A, B>() {
            @Override
            public <T> IReducer<T, B> apply(IReducer<T, A> reducer) {
                return (result, input) -> {
                        try {
                            return reducer.apply(result, function.apply(input));
                        } catch (Throwable t) {
                            t.printStackTrace();
                            return reduction(result).setReductionException(new ReductionException(t));
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
                return reducer(reducer,
                    (result, input) -> {
                        try {
                            return predicate.test(input) ? reducer.apply(result, input) : reduction(result);
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    }
                );
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
                return reducer(reducer, (result, input) -> reduce(reducer, result, input).setIsReduced(false));
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
                return reducer(reducer,
                    (result, input) -> {
                        try {
                            return predicate.test(input) ? reduction(result) : reducer.apply(result, input);
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    }
                );
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
                return reducer(reducer, new IReducer<T, A>() {
                    volatile long counter = 0;
                    @Override
                    public Reduction<T> apply(T result, A input) {
                        Reduction<T> reductionResult = reduction(result);
                        try {
                            if (counter < n) {
                                counter++;
                                reductionResult = reducer.apply(result, input);
                            } else {
                                reductionResult.setIsReduced(true);
                            }
                            return reductionResult;
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    }
                });
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
                return reducer(reducer, (result, input) -> {
                        Reduction<T> reductionResult = reduction(result);
                        try {
                            if (predicate.test(input)) {
                                reductionResult = reducer.apply(result, input);
                            } else {
                                reductionResult.setIsReduced(true);
                            }
                            return reductionResult;
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    });
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
                return reducer(reducer, new IReducer<T, A>() {
                    volatile long counter = 0;
                    @Override
                    public Reduction<T> apply(T result, A input) {
                        Reduction<T> reductionResult = reduction(result);
                        try {
                            if (counter < n) {
                                counter++;
                            } else {
                                reductionResult = reducer.apply(result, input);
                            }
                            return reductionResult;
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    }
                });
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
                return reducer(reducer, (result, input) -> {
                        Reduction<T> reductionResult = reduction(result);
                        try {
                            if (!predicate.test(input)) {
                                reductionResult = reducer.apply(result, input);
                            }
                            return reductionResult;
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    });
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * it processes every nth input.
     */
    public static <A> ITransducer<A, A> takeNth(final long n) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return reducer(reducer, new IReducer<T, A>() {
                    volatile long counter = 0;
                    @Override
                    public Reduction<T> apply(T result, A input) {
                        try {
                            return (counter++ % n) == 0
                                ? reducer.apply(result, input) : reduction(result);
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    }
                });
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * inputs that are keys in the provided map are replaced by the corresponding
     * value in the map.
     */
    public static <A> ITransducer<A, A> replace(final Map<A, A> map) {
        return map(value -> map.containsKey(value) ? map.get(value) : value);
    }

    /**
     * Creates a transducer that transforms a reducing function by applying a
     * function to each input and processing the resulting value, ignoring values
     * that are empty.
     */
    public static <A> ITransducer<A, A> keep(final Function<A, Optional<A>> function) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return reducer(reducer, (result, input) -> {
                        try {
                            return function.apply(input)
                                .map(value -> reducer.apply(result, value))
                                .orElse(reduction(result));
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    });
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function by applying a
     * function to each input and processing the resulting value, ignoring values
     * that are empty.
     */
    public static <A> ITransducer<A, A> keepIndexed(final BiFunction<Long, A, Optional<A>> function) {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return reducer(reducer, new IReducer<T, A>() {
                    volatile long index = 0;
                    @Override
                    public Reduction<T> apply(T result, A input) {
                        try {
                            return function.apply(index++, input)
                                    .map(value -> reducer.apply(result, value))
                                    .orElse(reduction(result));
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                    }
                });
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * consecutive identical input values are removed, only a single value
     * is processed.
     */
    public static <A> ITransducer<A, A> dedupe() {
        return new ITransducer<A, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, A> reducer) {
                return reducer(reducer, new IReducer<T, A>() {
                    volatile A previous = null;
                    @Override
                    public Reduction<T> apply(T result, A value) {
                        Reduction<T> reductionResult = reduction(result);
                        try {
                            if (this.previous == null || !this.previous.equals(value)) {
                                this.previous = value;
                                reductionResult = reducer.apply(result, value);
                            }
                        } catch (Throwable t) {
                            return reduction(result).setReductionException(new ReductionException(t));
                        }
                        return reductionResult;
                    }
                });
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function such that
     * it has the specified probability of processing each input.
     */
    public static <A> ITransducer<A, A> randomSample(final Double probability) {
        return filter(val -> Math.random() < probability);
    }

    /**
     * Creates a transducer that transforms a reducing function that processes
     * iterables of input into a reducing function that processes individual inputs
     * by gathering series of inputs for which the provided partitioning function returns
     * the same value, only forwarding them to the next reducing function when the value
     * the partitioning function returns for a given input is different from the value
     * returned for the previous input.
     */
    public static <A, P> ITransducer<Iterable<A>, A> partitionBy(final Function<A, P> function) {
        return new ITransducer<Iterable<A>, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, Iterable<A>> reducer) {
                return new IReducer<T, A>() {
                    volatile List<A> part = new ArrayList<>();
                    volatile Object mark = new Object();
                    volatile Object previous = mark;

                    @Override
                    public Optional<T> init() {
                        return reducer.init();
                    }

                    @Override
                    public Reduction<T> complete(Reduction<T> result) {
                        Reduction<T> finalReduction = result;
                        if (!this.part.isEmpty()) {
                            final List<A> copy = new ArrayList<>(part);
                            this.part.clear();
                            if (!result.isFailed()) {
                                finalReduction = reducer.apply(result.get(), copy);
                            }
                        }
                        return reducer.complete(finalReduction).setIsReduced(true);
                    }

                    @Override
                    public Reduction<T> apply(T result, A input) {
                        final P checksum = function.apply(input);
                        if ((previous == mark) || (previous.equals(checksum))) {
                            previous = checksum;
                            this.part.add(input);
                            return reduction(result);
                        } else {
                            final List<A> copy = new ArrayList<>(part);
                            previous = checksum;
                            part.clear();
                            Reduction<T> reduction = reducer.apply(result, copy);
                            if (!reduction.isReduced()) {
                                part.add(input);
                            }
                            return reduction;
                        }
                    }
                };
            }
        };
    }

    /**
     * Creates a transducer that transforms a reducing function that processes
     * iterables of input into a reducing function that processes individual inputs
     * by gathering series of inputs into partitions of a given size, only forwarding
     * them to the next reducing function when enough inputs have been accrued. Processes
     * any remaining buffered inputs when the reducing process completes.
     */
    public static <A> ITransducer<Iterable<A>, A> partitionAll(final int n) {
        return new ITransducer<Iterable<A>, A>() {
            @Override
            public <T> IReducer<T, A> apply(IReducer<T, Iterable<A>> reducer) {
                return new IReducer<T, A>() {
                    volatile List<A> part = new ArrayList<>(n);

                    @Override
                    public Optional<T> init() {
                        return reducer.init();
                    }

                    @Override
                    public Reduction<T> complete(Reduction<T> result) {
                        Reduction<T> finalReduction = result;
                        if (!this.part.isEmpty()) {
                            final List<A> copy = new ArrayList<>(part);
                            this.part.clear();
                            if (!result.isFailed()) {
                                finalReduction = reducer.apply(result.get(), copy);
                            }
                        }
                        return reducer.complete(finalReduction).setIsReduced(true);
                    }

                    @Override
                    public Reduction<T> apply(T result, A input) {
                        this.part.add(input);
                        if (n == part.size()) {
                            List<A> copy = new ArrayList<>(part);
                            part.clear();
                            return reducer.apply(result, copy);
                        }
                        return reduction(result);
                    }
                };
            }
        };
    }
}
