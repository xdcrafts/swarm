package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.util.FunctionUtils;
import com.github.xdcrafts.swarm.util.function.IThrowingFunction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Successful instance of try monad.
 * @param <T> value type
 */
public class Success<T> extends TryM<T> {

    private final T value;

    public Success(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value of Success<T> can not be null!");
        }
        this.value = value;
    }

    /**
     * Returns new success instance of try monad.
     * @param value successful value
     * @param <U> type of value
     * @return successfully try monad
     */
    public static <U> Success<U> success(U value) {
        return new Success<>(value);
    }

    @Override
    public <U> ITryM<U> map(Function<T, U> function) {
        return mapT(FunctionUtils.asTf(function));
    }

    @Override
    public void foreach(Consumer<T> function) {
        function.accept(this.value);
    }

    @Override
    public void foreachFailure(Consumer<Throwable> function) {
    }

    @Override
    public <U> ITryM<U> mapT(IThrowingFunction<T, U> function)  {
        return TryMOps.tryM(function, this.value);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public Throwable throwable() {
        throw new IllegalAccessError();
    }

    @Override
    public <U> U fold(Function<Throwable, U> ifFailure, Function<T, U> ifSuccess) {
        return ifSuccess.apply(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Success success = (Success) o;
        return this.value.equals(success.value);

    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "Success(" + this.value + ')';
    }
}
