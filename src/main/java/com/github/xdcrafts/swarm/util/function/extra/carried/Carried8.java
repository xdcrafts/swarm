package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction7;
import com.github.xdcrafts.swarm.util.function.extra.IFunction8;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <A4> fourth argument type
 * @param <A5> fifth argument type
 * @param <A6> sixth argument type
 * @param <A7> seventh argument type
 * @param <A8> eighth argument type
 * @param <B> return type
 */
public final class Carried8<A1, A2, A3, A4, A5, A6, A7, A8, B> implements IFunction7<A2, A3, A4, A5, A6, A7, A8, B> {
    private final A1 a1;
    private final IFunction8<A1, A2, A3, A4, A5, A6, A7, A8, B> function8;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function8 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <A7> seventh argument type
     * @param <A8> eighth argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, A8, B> IFunction7<A2, A3, A4, A5, A6, A7, A8, B> carry(
        A1 a1, IFunction8<A1, A2, A3, A4, A5, A6, A7, A8, B> function8) {
        return new Carried8<>(a1, function8);
    }
    private Carried8(A1 a1, IFunction8<A1, A2, A3, A4, A5, A6, A7, A8, B> function8) {
        this.a1 = a1;
        this.function8 = function8;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8) {
        return this.function8.apply(a1, a2, a3, a4, a5, a6, a7, a8);
    }
}
