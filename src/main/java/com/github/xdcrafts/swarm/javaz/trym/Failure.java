package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.util.function.IThrowingFunction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Failure instance of try monad.
 * @param <T> value type
 */
public class Failure<T> extends TryM<T> {

    private final Throwable throwable;

    public Failure(Throwable throwable) {
        if (throwable == null) {
            throw new IllegalArgumentException("Value of Success<T> can not be null!");
        }
        this.throwable = throwable;
    }

    /**
     * Returns new failed instance of try monad.
     * @param throwable cause
     * @param <U> type of monad value
     * @return failed try monad
     */
    public static <U> Failure<U> fail(Throwable throwable) {
        return new Failure<>(throwable);
    }

    @Override
    public <U> ITryM<U> map(Function<T, U> function) {
        return fail(this.throwable);
    }

    @Override
    public void foreach(Consumer<T> function) {
    }

    @Override
    public void foreachFailure(Consumer<Throwable> function) {
        function.accept(this.throwable);
    }

    @Override
    public <U> ITryM<U> mapT(IThrowingFunction<T, U> function) {
        return fail(this.throwable);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public T value() {
        throw new IllegalAccessError();
    }
    @Override
    public Throwable throwable() {
        return this.throwable;
    }

    @Override
    public <U> U fold(Function<Throwable, U> ifFailure, Function<T, U> ifSuccess) {
        return ifFailure.apply(this.throwable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Failure failure = (Failure) o;
        return this.throwable.equals(failure.throwable);

    }

    @Override
    public int hashCode() {
        return this.throwable.hashCode();
    }

    @Override
    public String toString() {
        return "Failure(" + this.throwable + ')';
    }
}
