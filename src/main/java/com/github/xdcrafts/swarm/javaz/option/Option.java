package com.github.xdcrafts.swarm.javaz.option;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.xdcrafts.swarm.javaz.option.OptionOps.none;

/**
 * Implementation of option monad.
 * @param <T> value type
 */
public abstract class Option<T> implements Serializable, IOption<T> {

    private static final long serialVersionUID = 0;

    @Override
    public abstract <U> IOption<U> map(Function<T, U> function);

    @Override
    public abstract IOption<T> filter(Predicate<T> predicate);

    @Override
    public <U, MM extends IApplicative<Function<T, U>, IOption<?>>> IOption<U> applicativeMap(MM applicativeFunction) {
        //noinspection unchecked
        return isDefined() ? (IOption<U>) applicativeFunction.map(tuFunction -> tuFunction.apply(get())) : none();
    }

    @Override
    public <U, MM extends IMonad<U, IOption<?>>> IOption<U> flatMap(Function<T, MM> function) {
        //noinspection unchecked
        return isDefined() ? (IOption<U>) function.apply(get()) : none();
    }
}
