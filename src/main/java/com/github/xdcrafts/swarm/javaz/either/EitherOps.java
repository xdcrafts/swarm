package com.github.xdcrafts.swarm.javaz.either;

import com.github.xdcrafts.swarm.javaz.common.monad.IMonadOps;

/**
 * Either monad operations.
 */
public final class EitherOps implements IMonadOps<IEither<?, ?>> {

    /**
     * Returns left either monad instance.
     * @param leftValue left value
     * @param <L> left value type
     * @param <R> right value type
     * @return either monad
     */
    public static <L, R> IEither<L, R> left(L leftValue) {
        return Left.left(leftValue);
    }

    /**
     * Returns right either monad instance.
     * @param rightValue right value
     * @param <L> left value type
     * @param <R> right value type
     * @return either monad
     */
    public static <L, R> IEither<L, R> right(R rightValue) {
        return Right.right(rightValue);
    }

    private EitherOps() {
        // Nothing
    }

    public static final EitherOps ID = new EitherOps();

    @Override
    public <U> IEither<?, U> pure(final U value) {
        return right(value);
    }
}

