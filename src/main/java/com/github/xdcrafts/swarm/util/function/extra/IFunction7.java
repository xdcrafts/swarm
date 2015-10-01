package com.github.xdcrafts.swarm.util.function.extra;

import com.github.xdcrafts.swarm.util.function.extra.carried.Carry;

/**
 * Function of seven arguments.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <A4> fourth argument type
 * @param <A5> fifth argument type
 * @param <A6> sixth argument type
 * @param <A7> seventh argument type
 * @param <B> return type
 */
public interface IFunction7<A1, A2, A3, A4, A5, A6, A7, B> {
    /**
     * Applies function.
     * @param a1 first argument
     * @param a2 second argument
     * @param a3 third argument
     * @param a4 fourth argument
     * @param a5 fifth argument
     * @param a6 sixth argument
     * @param a7 seventh argument
     * @return value of type B
     */
    B apply(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7);
    /**
     * Carries function
     * @param a1 first argument
     * @return carried function
     */
    default IFunction6<A2, A3, A4, A5, A6, A7, B> carry(A1 a1) {
        return Carry.carry(a1, this);
    }
}
