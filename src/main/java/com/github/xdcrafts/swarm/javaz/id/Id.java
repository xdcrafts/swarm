package com.github.xdcrafts.swarm.javaz.id;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Id monad, just simple container for value of type T.
 * @param <T> value type
 */
@SuppressWarnings("unchecked")
public final class Id<T> implements IMonad<T, Id<?>> {

    private final T value;

    public Id(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value of Id<T> can not be null!");
        }
        this.value = value;
    }

    /**
     * Unwrap value.
     * @return value of type T
     */
    public T get() {
        return this.value;
    }

    @Override
    public <U> Id<U> map(Function<T, U> function) {
        return new Id(function.apply(this.value));
    }

    @Override
    public void foreach(Consumer<T> function) {
        function.accept(this.value);
    }

    @Override
    public <U, MM extends IApplicative<Function<T, U>, Id<?>>> Id<U> applicativeMap(MM applicativeFunction) {
        return (Id<U>) applicativeFunction.map(this::map).value;
    }

    @Override
    public <U, MM extends IMonad<U, Id<?>>> Id<U> flatMap(Function<T, MM> function) {
        return (Id<U>) map(function).value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Id id = (Id) o;
        return this.value.equals(id.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "Id(" + this.value + ')';
    }
}
