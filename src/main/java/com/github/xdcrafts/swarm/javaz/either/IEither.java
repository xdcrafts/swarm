package com.github.xdcrafts.swarm.javaz.either;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.javaz.option.IOption;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Monad that represents one of two possible values.
 * @param <L> left value type
 * @param <R> right value type
 */
public interface IEither<L, R> extends IMonad<R, IEither<?, ?>> {

    @Override
    <U> IEither<L, U> map(Function<R, U> function);

    @Override
    <U, MM extends IApplicative<Function<R, U>, IEither<?, ?>>> IEither<L, U> applicativeMap(MM applicativeFunction);

    @Override
    <U, MM extends IMonad<U, IEither<?, ?>>> IEither<L, U> flatMap(Function<R, MM> function);

    /**
     * Same as map but for left value.
     * @param function mapper
     * @param <U> new left value type
     * @return new either monad
     */
    <U> IEither<U, R> mapLeft(Function<L, U> function);

    /**
     * Same as applicative map but for left value.
     * @param applicativeFunction mapper
     * @param <U> new left value type
     * @return new either monad
     */
    <U> IEither<U, R> applicativeMapLeft(IEither<?, Function<L, U>> applicativeFunction);

    /**
     * Same as flat map but for left value.
     * @param function mapper
     * @param <U> new left value type
     * @return new either monad
     */
    <U> IEither<U, R> flatMapLeft(Function<L, IEither<U, R>> function);

    /**
     * Same as foreach but for left value.
     * @param function mapper
     */
    void foreachLeft(Consumer<L> function);

    /**
     * Returns right value or throws an exception.
     * @return value of type R
     */
    R right();

    /**
     * Returns left value or throws an exception.
     * @return value of type L
     */
    L left();

    /**
     * Is this either left.
     * @return boolean
     */
    boolean isRight();

    /**
     * Is this either right.
     * @return boolean
     */
    boolean isLeft();

    /**
     * If right than applies function to right value, otherwise - returns default ifLeft value.
     * @param ifLeft default value
     * @param function function to apply
     * @param <U> result value type
     * @return value of type U
     */
    <U> U foldRight(U ifLeft, Function<R, U> function);

    /**
     * If left than applies function to right value, otherwise - returns default ifRight value.
     * @param ifRight default value
     * @param function function to apply
     * @param <U> result value type
     * @return value of type U
     */
    <U> U foldLeft(U ifRight, Function<L, U> function);

    /**
     * Converts this either to option.
     * @return some if right, left - otherwise
     */
    IOption<R> asOption();
}
