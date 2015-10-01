package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.extra.IFunction2;
import com.github.xdcrafts.swarm.util.function.extra.IFunction3;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <A3> second argument type
 * @param <B> return type
 */
public final class Carried3<A1, A2, A3, B> implements IFunction2<A2, A3, B> {
    private final A1 a1;
    private final IFunction3<A1, A2, A3, B> function3;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function3 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> second argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, A3, B> IFunction2<A2, A3, B> carry(A1 a1, IFunction3<A1, A2, A3, B> function3) {
        return new Carried3<>(a1, function3);
    }
    private Carried3(A1 a1, IFunction3<A1, A2, A3, B> function3) {
        this.a1 = a1;
        this.function3 = function3;
    }
    @Override public B apply(A2 a2, A3 a3) {
        return this.function3.apply(a1, a2, a3);
    }
}
