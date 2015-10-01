package com.github.xdcrafts.swarm.javaz.option;

import com.github.xdcrafts.swarm.util.function.IFunction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Empty instance of option monad.
 * @param <T> value type
 */
public class None<T> extends Option<T> {

    public None() {
        // Nothing
    }

    /**
     * Returns new none instance.
     * @param <U> value type
     * @return option monad
     */
    public static <U> None<U> none() {
        return new None<>();
    }

    @Override
    public boolean isDefined() {
        return false;
    }

    @Override
    public T get() {
        throw new IllegalAccessError();
    }

    @Override
    public T orNull() {
        return null;
    }

    @Override
    public T orElse(T elseValue) {
        return elseValue;
    }

    @Override
    public IOption<T> or(IOption<T> elseValue) {
        return elseValue;
    }

    @Override
    public <U> IOption<U> map(Function<T, U> function) {
        return none();
    }

    @Override
    public IOption<T> filter(Predicate<T> predicate) {
        return none();
    }

    @Override
    public void foreach(Consumer<T> function) {
    }

    @Override
    public <U> U fold(U ifNone, Function<T, U> function) {
        return ifNone;
    }

    @Override
    public boolean equals(Object o) {
        return !(o == null || getClass() != o.getClass());
    }

    @Override
    public String toString() {
        return "None";
    }
}
