package com.github.xdcrafts.swarm.javaz.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Instance of option monad that contains value.
 * @param <T> value type
 */
public class Some<T> extends Option<T> {

    private final T value;

    public Some(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value of Some<T> can not be null!");
        }
        this.value = value;
    }

    /**
     * Returns new some instance of option monad.
     * @param value value of type U
     * @param <U> value type
     * @return option monad
     */
    public static <U> Some<U> some(U value) {
        return new Some<>(value);
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public T orNull() {
        return this.value;
    }

    @Override
    public T orElse(T elseValue) {
        return this.value;
    }

    @Override
    public IOption<T> or(IOption<T> elseValue) {
        return this;
    }

    @Override
    public <U> IOption<U> map(Function<T, U> function) {
        return some(function.apply(this.value));
    }

    @Override
    public IOption<T> filter(Predicate<T> predicate) {
        return predicate.test(this.value) ? this : new None<>();
    }

    @Override
    public void foreach(Consumer<T> function) {
        function.accept(this.value);
    }

    @Override
    public <U> U fold(U ifNone, Function<T, U> function) {
        return function.apply(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Some some = (Some) o;
        return value.equals(some.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "Some(" + this.value + ')';
    }
}
