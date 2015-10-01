package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction3;
import com.github.xdcrafts.swarm.util.function.extra.IFunction4;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> third argument type
 * @param <A4> fourth argument type
 * @param <B> return type
 */
public final class Carried4<A1, A2, A3, A4, B> implements IFunction3<A2, A3, A4, B> {
    private final A1 a1;
    private final IFunction4<A1, A2, A3, A4, B> function4;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function4 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, A4, B> IFunction3<A2, A3, A4, B> carry(A1 a1, IFunction4<A1, A2, A3, A4, B> function4) {
        return new Carried4<>(a1, function4);
    }
    private Carried4(A1 a1, IFunction4<A1, A2, A3, A4, B> function4) {
        this.a1 = a1;
        this.function4 = function4;
    }
    @Override public B apply(A2 a2, A3 a3, A4 a4) {
        return this.function4.apply(a1, a2, a3, a4);
    }
}
