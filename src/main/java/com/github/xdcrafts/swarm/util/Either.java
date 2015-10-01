package com.github.xdcrafts.swarm.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Contains either left or right value.
 * @param <L> left value type
 * @param <R> right value type
 */
public final class Either<L, R> {

    /**
     * Constructs left value.
     * @param <L> left value type
     * @param <R> right value type
     * @param left left value
     * @return left version of either
     */
    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(Optional.of(left), Optional.<R>empty());
    }

    /**
     * Constructs right value.
     * @param <L> left value type
     * @param <R> right value type
     * @param right right value
     * @return right version of either
     */
    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(Optional.empty(), Optional.of(right));
    }

    /**
     * Constructs new either value.
     * @param <L> left value type
     * @param <R> right value type
     * @param left left value
     * @param right value
     * @return constructs new either value
     */
    public static <L, R> Either<L, R> either(L left, R right) {
        if (right != null) {
            return right(right);
        } else if (left != null) {
            return left(left);
        } else {
            throw new IllegalArgumentException("Left or right value must not be null.");
        }
    }

    /**
     * Constructs new either value.
     * @param <L> left value type
     * @param <R> right value type
     * @param left left optional value
     * @param right right optional value
     * @return constructs new either value
     */
    public static <L, R> Either<L, R> either(Optional<L> left, Optional<R> right) {
        if (left.isPresent() && right.isPresent() || !left.isPresent() && !right.isPresent()) {
            throw new IllegalArgumentException("Only one of two options must present");
        }
        return new Either<>(left, right);
    }

    private final Optional<L> left;
    private final Optional<R> right;

    private Either(Optional<L> left, Optional<R> right) {
        this.left = left;
        this.right = right;
    }

    public boolean isLeft() {
        return this.left.isPresent();
    }

    public boolean isRight() {
        return this.right.isPresent();
    }

    /**
     * Left value.
     * @return left value
     */
    public L left() {
        return this.left.get();
    }

    /**
     * Left optional value.
     * @return left value as optional
     */
    public Optional<L> leftOption() {
        return this.left;
    }

    /**
     * Right value.
     * @return right value
     */
    public R right() {
        return this.right.get();
    }

    /**
     * Right optional value.
     * @return right value as optional
     */
    public R rightOption() {
        return this.right.get();
    }

    /**
     * Map function for both values.
     * @param <L2> new left value type
     * @param <R2> new right value type
     * @param leftMap left mapper
     * @param rightMap right mapper
     * @return new either to which leftMap and rightMap was applied
     */
    public <L2, R2> Either<L2, R2> map(Function<L, L2> leftMap, Function<R, R2> rightMap) {
        return new Either<>(this.left.map(leftMap), this.right.map(rightMap));
    }

    /**
     * Map function for left value.
     * @param <L2> new left value type
     * @param leftMap left mapper
     * @return new either to which leftMap was applied
     */
    public <L2> Either<L2, R> mapLeft(Function<L, L2> leftMap) {
        return new Either<>(this.left.map(leftMap), this.right);
    }

    /**
     * Map function for right value.
     * @param <R2> new right value type
     * @param rightMap right mapper
     * @return new either to which rightMap was applied
     */
    public <R2> Either<L, R2> mapRight(Function<R, R2> rightMap) {
        return new Either<>(this.left, this.right.map(rightMap));
    }

    /**
     * Consumer function for both values.
     * @param leftConsumer left consumer
     * @param rightConsumer right consumer
     */
    public void consume(Consumer<L> leftConsumer, Consumer<R> rightConsumer) {
        this.left.ifPresent(leftConsumer);
        this.right.ifPresent(rightConsumer);
    }

    /**
     * Consume function for left value.
     * @param leftConsumer left consumer
     */
    public void consumeLeft(Consumer<L> leftConsumer) {
        this.left.ifPresent(leftConsumer);
    }

    /**
     * Consume function for right value.
     * @param rightConsumer right consumer
     */
    public void consumeRight(Consumer<R> rightConsumer) {
        this.right.ifPresent(rightConsumer);
    }
}
