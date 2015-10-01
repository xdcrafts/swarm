package com.github.xdcrafts.swarm.javaz.either;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Left instance of either monad.
 * @param <L> left value type
 * @param <R> right value type
 */
public class Left<L, R> extends Either<L, R> {

    private final L leftValue;

    public Left(L leftValue) {
        if (leftValue == null) {
            throw new IllegalArgumentException("Value of Left<L, R> can not be null!");
        }
        this.leftValue = leftValue;
    }

    /**
     * Returns left instance of either monad.
     * @param value left value
     * @param <L> left value type
     * @param <R> right value type
     * @return either monad
     */
    public static <L, R> Left<L, R> left(L value) {
        return new Left<>(value);
    }

    @Override
    public <U> IEither<L, U> map(Function<R, U> function) {
        return left(this.leftValue);
    }

    @Override
    public void foreach(Consumer<R> function) {
    }

    @Override
    public <U> IEither<U, R> mapLeft(Function<L, U> function) {
        return left(function.apply(this.leftValue));
    }

    @Override
    public void foreachLeft(Consumer<L> function) {
        function.accept(this.leftValue);
    }

    @Override
    public R right() {
        throw new IllegalAccessError();
    }

    @Override
    public L left() {
        return this.leftValue;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public <U> U foldRight(U ifLeft, Function<R, U> function) {
        return ifLeft;
    }

    @Override
    public <U> U foldLeft(U ifRight, Function<L, U> function) {
        return function.apply(this.leftValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Left left = (Left) o;
        return leftValue.equals(left.leftValue);
    }

    @Override
    public int hashCode() {
        return leftValue.hashCode();
    }

    @Override
    public String toString() {
        return "Left(" + this.leftValue + ')';
    }
}
