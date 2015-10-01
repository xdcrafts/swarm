package com.github.xdcrafts.swarm.javaz.either;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Right instance of either monad.
 * @param <L> left value type
 * @param <R> right value type
 */
@SuppressWarnings("unchecked")
public class Right<L, R> extends Either<L, R> {

    private final R rightValue;

    public Right(R rightValue) {
        if (rightValue == null) {
            throw new IllegalArgumentException("Value of Right<L, R> can not be null!");
        }
        this.rightValue = rightValue;
    }

    /**
     * Returns new right instance of either monad.
     * @param value right value
     * @param <L> left value type
     * @param <R> right value type
     * @return either monad
     */
    public static <L, R> Right<L, R> right(R value) {
        return new Right<>(value);
    }

    @Override
    public <U> IEither<L, U> map(Function<R, U> function) {
        return right(function.apply(this.rightValue));
    }

    @Override
    public void foreach(Consumer<R> function) {
        function.accept(this.rightValue);
    }

    @Override
    public <U> IEither<U, R> mapLeft(Function<L, U> function) {
        return right(this.rightValue);
    }

    @Override
    public void foreachLeft(Consumer<L> function) {
    }

    @Override
    public R right() {
        return this.rightValue;
    }

    @Override
    public L left() {
        throw new IllegalAccessError();
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public <U> U foldRight(U ifLeft, Function<R, U> function) {
        return function.apply(this.rightValue);
    }

    @Override
    public <U> U foldLeft(U ifRight, Function<L, U> function) {
        return ifRight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Right right = (Right) o;
        return rightValue.equals(right.rightValue);
    }

    @Override
    public int hashCode() {
        return this.rightValue.hashCode();
    }

    @Override
    public String toString() {
        return "Right(" + this.rightValue + ')';
    }
}
