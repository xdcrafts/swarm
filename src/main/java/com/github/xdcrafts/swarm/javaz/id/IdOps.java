package com.github.xdcrafts.swarm.javaz.id;

import com.github.xdcrafts.swarm.javaz.common.monad.IMonadOps;

/**
 * Id operations.
 */
@SuppressWarnings("unchecked")
public final class IdOps implements IMonadOps<Id<?>> {

    /**
     * Returns value wrapped with id monad.
     * @param value value of type U
     * @param <U> value type
     * @return id monad
     */
    public static <U> Id<U> id(U value) {
        return new Id<>(value);
    }

    private IdOps() {
        // Nothing
    }

    public static final IdOps ID = new IdOps();

    @Override
    public <U> Id<U> pure(U value) {
        return id(value);
    }
}
