package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction5;
import com.github.xdcrafts.swarm.util.function.extra.IFunction6;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <A4> fourth argument type
 * @param <A5> fifth argument type
 * @param <A6> sixth argument type
 * @param <B> return type
 */
public final class Carried6<A1, A2, A3, A4, A5, A6, B> implements IFunction5<A2, A3, A4, A5, A6, B> {
    private final A1 a1;
    private final IFunction6<A1, A2, A3, A4, A5, A6, B> function6;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function6 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, B> IFunction5<A2, A3, A4, A5, A6, B> carry(
        A1 a1, IFunction6<A1, A2, A3, A4, A5, A6, B> function6) {
        return new Carried6<>(a1, function6);
    }
    private Carried6(A1 a1, IFunction6<A1, A2, A3, A4, A5, A6, B> function6) {
        this.a1 = a1;
        this.function6 = function6;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
        return this.function6.apply(a1, a2, a3, a4, a5, a6);
    }
}
