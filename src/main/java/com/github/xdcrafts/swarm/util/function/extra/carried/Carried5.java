package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction4;
import com.github.xdcrafts.swarm.util.function.extra.IFunction5;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <A4> fourth argument type
 * @param <A5> fifth argument type
 * @param <B> return type
 */
public final class Carried5<A1, A2, A3, A4, A5, B> implements IFunction4<A2, A3, A4, A5, B> {
    private final A1 a1;
    private final IFunction5<A1, A2, A3, A4, A5, B> function5;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function5 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, B> IFunction4<A2, A3, A4, A5, B> carry(
        A1 a1, IFunction5<A1, A2, A3, A4, A5, B> function5) {
        return new Carried5<>(a1, function5);
    }
    private Carried5(A1 a1, IFunction5<A1, A2, A3, A4, A5, B> function5) {
        this.a1 = a1;
        this.function5 = function5;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4, A5 a5) {
        return this.function5.apply(a1, a2, a3, a4, a5);
    }
}
