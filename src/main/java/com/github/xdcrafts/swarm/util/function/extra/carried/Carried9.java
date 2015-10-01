package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction8;
import com.github.xdcrafts.swarm.util.function.extra.IFunction9;

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
 * @param <A9> ninth argument type
 * @param <B> return type
 */
public final class Carried9<A1, A2, A3, A4, A5, A6, A7, A8, A9, B>
    implements IFunction8<A2, A3, A4, A5, A6, A7, A8, A9, B> {
    private final A1 a1;
    private final IFunction9<A1, A2, A3, A4, A5, A6, A7, A8, A9, B> function9;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function9 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <A7> seventh argument type
     * @param <A8> eighth argument type
     * @param <A9> ninth argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, B> IFunction8<A2, A3, A4, A5, A6, A7, A8, A9, B> carry(
        A1 a1, IFunction9<A1, A2, A3, A4, A5, A6, A7, A8, A9, B> function9) {
        return new Carried9<>(a1, function9);
    }
    private Carried9(A1 a1, IFunction9<A1, A2, A3, A4, A5, A6, A7, A8, A9, B> function9) {
        this.a1 = a1;
        this.function9 = function9;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9) {
        return this.function9.apply(a1, a2, a3, a4, a5, a6, a7, a8, a9);
    }
}
