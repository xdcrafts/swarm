package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.IFunction;
import com.github.xdcrafts.swarm.util.function.ISupplier;
import com.github.xdcrafts.swarm.util.function.extra.IFunction10;
import com.github.xdcrafts.swarm.util.function.extra.IFunction2;
import com.github.xdcrafts.swarm.util.function.extra.IFunction3;
import com.github.xdcrafts.swarm.util.function.extra.IFunction4;
import com.github.xdcrafts.swarm.util.function.extra.IFunction5;
import com.github.xdcrafts.swarm.util.function.extra.IFunction6;
import com.github.xdcrafts.swarm.util.function.extra.IFunction7;
import com.github.xdcrafts.swarm.util.function.extra.IFunction8;
import com.github.xdcrafts.swarm.util.function.extra.IFunction9;

/**
 * Carry functions.
 */
public class Carry {
    /**
     * Carries function.
     * @param a argument
     * @param function function
     * @param <A> argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A, B> ISupplier<B> carry(A a, IFunction<A, B> function) {
        return Carried.carry(a, function);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function2 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, B> IFunction<A2, B> carry(A1 a1, IFunction2<A1, A2, B> function2) {
        return Carried2.carry(a1, function2);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function3 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, B> IFunction2<A2, A3, B> carry(A1 a1, IFunction3<A1, A2, A3, B> function3) {
        return Carried3.carry(a1, function3);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function4 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, B> IFunction3<A2, A3, A4, B> carry(A1 a1, IFunction4<A1, A2, A3, A4, B> function4) {
        return Carried4.carry(a1, function4);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function5 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, B> IFunction4<A2, A3, A4, A5, B> carry(
        A1 a1, IFunction5<A1, A2, A3, A4, A5, B> function5) {
        return Carried5.carry(a1, function5);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function6 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, B> IFunction5<A2, A3, A4, A5, A6, B> carry(
        A1 a1, IFunction6<A1, A2, A3, A4, A5, A6, B> function6) {
        return Carried6.carry(a1, function6);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function7 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <A7> seventh argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, B> IFunction6<A2, A3, A4, A5, A6, A7, B> carry(
        A1 a1, IFunction7<A1, A2, A3, A4, A5, A6, A7, B> function7) {
        return Carried7.carry(a1, function7);
    }
    /**
     * Carries function.
     * @param a1 argument
     * @param function8 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <A3> third argument type
     * @param <A4> fourth argument type
     * @param <A5> fifth argument type
     * @param <A6> sixth argument type
     * @param <A7> seventh argument type
     * @param <A8> eighth argument type
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, A8, B> IFunction7<A2, A3, A4, A5, A6, A7, A8, B> carry(
        A1 a1, IFunction8<A1, A2, A3, A4, A5, A6, A7, A8, B> function8) {
        return Carried8.carry(a1, function8);
    }
    /**
     * Carries function.
     * @param a1 argument
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
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, B> IFunction8<A2, A3, A4, A5, A6, A7, A8, A9, B> carry(
        A1 a1, IFunction9<A1, A2, A3, A4, A5, A6, A7, A8, A9, B> function9) {
        return Carried9.carry(a1, function9);
    }
    /**
     * Carries function.
     * @param a1 argument
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
     * @param <B> return value type
     * @return carried function
     */
    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> IFunction9<A2, A3, A4, A5, A6, A7, A8, A9, A10, B> carry(
        A1 a1, IFunction10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, B> function10) {
        return Carried10.carry(a1, function10);
    }
}
