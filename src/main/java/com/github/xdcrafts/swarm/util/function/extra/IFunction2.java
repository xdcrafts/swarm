package com.github.xdcrafts.swarm.util.function.extra;

import com.github.xdcrafts.swarm.util.function.IFunction;
import com.github.xdcrafts.swarm.util.function.extra.carried.Carry;

import java.util.function.BiFunction;

/**
 * Function of two arguments.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <B> return type
 */
public interface IFunction2<A1, A2, B> extends BiFunction<A1, A2, B> {
    /**
     * Carries this function.
     * @param a1 first argument
     * @return carried function
     */
    default IFunction<A2, B> carry(A1 a1) {
        return Carry.carry(a1, this);
    }
}
