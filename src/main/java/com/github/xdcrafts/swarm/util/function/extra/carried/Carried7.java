package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction6;
import com.github.xdcrafts.swarm.util.function.extra.IFunction7;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <A4> fourth argument type
 * @param <A5> fifth argument type
 * @param <A6> sixth argument type
 * @param <A7> seventh argument type
 * @param <B> return type
 */
public final class Carried7<A1, A2, A3, A4, A5, A6, A7, B> implements IFunction6<A2, A3, A4, A5, A6, A7, B> {
    private final A1 a1;
    private final IFunction7<A1, A2, A3, A4, A5, A6, A7, B> function7;
    /**
     * Carries function.
     * @param a1 first argument.
     * @param function7 function.
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <A7> seventh argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, B> IFunction6<A2, A3, A4, A5, A6, A7, B> carry(
        A1 a1, IFunction7<A1, A2, A3, A4, A5, A6, A7, B> function7) {
        return new Carried7<>(a1, function7);
    }
    private Carried7(A1 a1, IFunction7<A1, A2, A3, A4, A5, A6, A7, B> function7) {
        this.a1 = a1;
        this.function7 = function7;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
        return this.function7.apply(a1, a2, a3, a4, a5, a6, a7);
    }
}
