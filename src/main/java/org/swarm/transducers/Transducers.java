package org.swarm.transducers;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * Simple implementation of IReducer.
     * @param <T> input value type
     * @param <R> result value type
     */
    private static final class Reducer<T, R> implements IReducer<T, R> {
        private final Optional<Supplier<R>> init;
        private final Function<R, R> complete;
        private final BiFunction<R, T, IReduction<R>> reducer;
        Reducer(Optional<Supplier<R>> init, Function<R, R> complete, BiFunction<R, T, IReduction<R>> reducer) {
            this.init = init;
            this.complete = complete;
            this.reducer = reducer;
        }
        @Override
        public Optional<Supplier<R>> init() {
            return this.init;
        }
        @Override
        public Function<R, R> complete() {
            return this.complete;
        }
        @Override
        public BiFunction<R, T, IReduction<R>> reducer() {
            return this.reducer;
        }
    }

    /**
     * Builder for Reducer container.
     * @param <T> input value type
     * @param <R> result value type
     */
    public static final class ReducerBuilder<T, R> implements Supplier<IReducer<T, R>> {
        private final BiFunction<R, T, IReduction<R>> reducer;
        private volatile Optional<Supplier<R>> init = Optional.empty();
        private volatile Function<R, R> complete = Function.identity();
        private ReducerBuilder(BiFunction<R, T, IReduction<R>> reducer) {
            this.reducer = reducer;
        }

        /**
         * Setup init function.
         */
        public ReducerBuilder<T, R> withInit(Supplier<R> initSupplier) {
            this.init = Optional.of(initSupplier);
            return this;
        }

        /**
         * Setup complete function.
         */
        public ReducerBuilder<T, R> withComplete(Function<R, R> completeFunction) {
            this.complete = completeFunction;
            return this;
        }
        @Override
        public IReducer<T, R> get() {
            return new Reducer<>(this.init, this.complete, this.reducer);
        }
    }

    /**
     * Creates new IReducer instance.
     */
    public static <T, R> ReducerBuilder<T, R> reducer(BiFunction<R, T, IReduction<R>> reducer) {
        return new ReducerBuilder<>(reducer);
    }

    /**
     * Applies given reducing function to current result and each T in input, using
     * the result returned from each reduction step as input to the next step. Returns
     * final result.
     */
    public static <T, R> IReduction<R> reduce(IReducer<T, R> reducer, R initValue, Iterable<T> input) {
        R result = initValue;
        for (T inputValue: input) {
            final IReduction<R> reduction = reducer.reducer().apply(result, inputValue);
            result = reduction.get();
            if (reduction.isFailed()) {
                return reduction;
            }
            if (reduction.isReduced()) {
                break;
            }
        }
        return reduction(reducer.complete().apply(result), true, Optional.<ReductionException>empty());
    }
}
