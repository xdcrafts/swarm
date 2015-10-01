package com.github.xdcrafts.swarm.javaz.option;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.filterable.IFilterable;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Optional monad that represents value that may be absent.
 * @param <T> value type
 */
public interface IOption<T> extends IMonad<T, IOption<?>>, IFilterable<T, IOption<?>> {

    @Override
    <U> IOption<U> map(Function<T, U> function);

    @Override
    <U, MM extends IApplicative<Function<T, U>, IOption<?>>> IOption<U> applicativeMap(MM applicativeFunction);

    @Override
    <U, MM extends IMonad<U, IOption<?>>> IOption<U> flatMap(Function<T, MM> function);

    @Override
    IOption<T> filter(Predicate<T> function);

    /**
     * Is value present.
     * @return boolean
     */
    boolean isDefined();

    /**
     * Returns contained value or throws exception otherwise.
     * @return value of type T
     */
    T get();

    /**
     * Returns contained value or null.
     * @return value of type T
     */
    T orNull();

    /**
     * Returns contained value or else value.
     * @param elseValue default value
     * @return value of type T
     */
    T orElse(T elseValue);

    /**
     * Returns this option if value is present or else value option.
     * @param elseValue default option
     * @return option
     */
    IOption<T> or(IOption<T> elseValue);

    /**
     * If value is present than applies function to it and returns new value of type U.
     * If value is not present than returns if none value.
     * @param ifNone default value
     * @param function function to apply
     * @param <U> new value type
     * @return value of type U
     */
    <U> U fold(U ifNone, Function<T, U> function);

    /**
     * Converts this Option to default java Optional.
     * @return optional
     */
    default Optional<T> toOptional() {
        return isDefined() ? Optional.of(get()) : Optional.<T>empty();
    }
}
