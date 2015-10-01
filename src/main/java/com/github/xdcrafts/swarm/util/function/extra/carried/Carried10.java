package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction10;
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
 * @param <A10> tenth argument type
 * @param <B> return type
 */
public final class Carried10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B>
    implements IFunction9<A2, A3, A4, A5, A6, A7, A8, A9, A10, B> {
    private final A1 a1;
    private final IFunction10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> function10;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function10 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <A7> seventh argument type
     * @param <A8> eighth argument type
     * @param <A9> ninth argument type
     * @param <A10> tenth argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> IFunction9<A2, A3, A4, A5, A6, A7, A8, A9, A10, B> carry(
        A1 a1, IFunction10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> function10) {
        return new Carried10<>(a1, function10);
    }
    private Carried10(A1 a1, IFunction10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> function10) {
        this.a1 = a1;
        this.function10 = function10;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7, A8 a8, A9 a9, A10 a10) {
        return this.function10.apply(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
    }
}
