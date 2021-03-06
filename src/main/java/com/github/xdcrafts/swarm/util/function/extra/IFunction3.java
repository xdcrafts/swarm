package com.github.xdcrafts.swarm.util.function.extra;

import com.github.xdcrafts.swarm.util.function.extra.carried.Carry;

/**
 * Function of three arguments.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <B> return type
 */
public interface IFunction3<A1, A2, A3, B> {
    /**
     * Applies function.
     * @param a1 first argument
     * @param a2 second argument
     * @param a3 third argument
     * @return value of type B
     */
    B apply(A1 a1, A2 a2, A3 a3);
    /**
     * Carries function.
     * @param a1 first argument
     * @return carried function
     */
    default IFunction2<A2, A3, B> carry(A1 a1) {
        return Carry.carry(a1, this);
    }
}
