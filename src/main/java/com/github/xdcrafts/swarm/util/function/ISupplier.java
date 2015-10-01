package com.github.xdcrafts.swarm.util.function;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Boosted version of java supplier.
 * @param <T> value type
 */
public interface ISupplier<T> extends Supplier<T> {

    /**
     * Returns new supplier by applying of function.
     * @param <U> new value type
     * @param function mapper from T to U
     * @return new supplier of type U
     */
    default <U> ISupplier<U> map(Function<T, U> function) {
        return () -> function.apply(ISupplier.this.get());
    }

    /**
     * Returns new supplier by applying of function.
     * @param <U> new value type
     * @param function mapper from T to Supplier U
     * @return new supplier of type U
     */
    default <U> ISupplier<U> flatMap(Function<T, ISupplier<U>> function) {
        return function.apply(ISupplier.this.get());
    }

    /**
    * Returns new supplier by applying of predicate.
     * @param predicate filtering function for values of type T
     * @return new supplier of optional values of type T
    */
    default ISupplier<Optional<T>> filter(Predicate<T> predicate) {
        final T value = get();
        return predicate.test(value) ? () -> Optional.ofNullable(value) : Optional::empty;
    }
}
