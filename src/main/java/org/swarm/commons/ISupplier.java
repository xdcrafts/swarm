package org.swarm.commons;

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
     */
    default <U> ISupplier<U> map(Function<T, U> function) {
        return () -> function.apply(ISupplier.this.get());
    }

    /**
     * Returns new supplier by applying of function.
     */
    default <U> ISupplier<U> flatMap(Function<T, ISupplier<U>> function) {
        return function.apply(ISupplier.this.get());
    }

    /**
    * Returns new supplier by applying of predicate.
    */
    default ISupplier<Optional<T>> filter(Predicate<T> predicate) {
        final T value = get();
        return predicate.test(value) ? () -> Optional.ofNullable(value) : Optional::empty;
    }
}
