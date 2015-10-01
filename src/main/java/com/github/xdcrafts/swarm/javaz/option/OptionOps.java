package com.github.xdcrafts.swarm.javaz.option;

import com.github.xdcrafts.swarm.javaz.common.monad.IMonadOps;

import java.util.Optional;

/**
 * Option monad operations.
 */
public final class OptionOps implements IMonadOps<IOption<?>> {

    /**
     * Constructs option monad from java optional.
     * @param optional java optional
     * @param <U> value type
     * @return option monad
     */
    public static <U> IOption<U> fromOptional(final Optional<U> optional) {
        return option(optional.orElse(null));
    }

    /**
     * Constructs option monad from nullable value.
     * @param value value of type U
     * @param <U> value type
     * @return option monad
     */
    public static <U> IOption<U> option(final U value) {
        final IOption<U> none = none();
        return value == null ? none : some(value);
    }

    /**
     * Returns none option monad instance.
     * @param <U> value type
     * @return option monad
     */
    public static <U> None<U> none() {
        return None.none();
    }

    /**
     * Returns some option monad instance.
     * @param value non-nullable value of type U
     * @param <U> value type
     * @return option monad
     */
    public static <U> Some<U> some(final U value) {
        return Some.some(value);
    }

    private OptionOps() {
        // Nothing
    }

    public static final OptionOps ID = new OptionOps();

    @Override
    public <U> IOption<U> pure(final U value) {
        return some(value);
    }
}
