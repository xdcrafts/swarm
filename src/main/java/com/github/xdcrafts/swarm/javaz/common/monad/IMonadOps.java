package com.github.xdcrafts.swarm.javaz.common.monad;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicativeOps;
import com.github.xdcrafts.swarm.util.function.extra.IFunction10;
import com.github.xdcrafts.swarm.util.function.extra.IFunction2;
import com.github.xdcrafts.swarm.util.function.extra.IFunction3;
import com.github.xdcrafts.swarm.util.function.extra.IFunction4;
import com.github.xdcrafts.swarm.util.function.extra.IFunction5;
import com.github.xdcrafts.swarm.util.function.extra.IFunction6;
import com.github.xdcrafts.swarm.util.function.extra.IFunction7;
import com.github.xdcrafts.swarm.util.function.extra.IFunction8;
import com.github.xdcrafts.swarm.util.function.extra.IFunction9;

import java.util.function.Function;

/**
 * Utility functions that can be applied to monads of type M.
 * @param <M>
 */
@SuppressWarnings("unchecked")
public interface IMonadOps<M extends IMonad<?, M>> extends IApplicativeOps<M> {

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param function2 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, U, M1 extends IMonad<T1, M>, M2 extends IMonad<T2, M>, MU extends IMonad<U, M>> MU yieldFor(
        M1 m1, M2 m2, IFunction2<T1, T2, U> function2) {
        final IMonad<Function<T2, U>, M> applicativeFunction = (IMonad<Function<T2, U>, M>) m1.map(function2::carry);
        return (MU) m2.applicativeMap(applicativeFunction);
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param function3 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, IFunction3<T1, T2, T3, U> function3
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, function3.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param function4 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, IFunction4<T1, T2, T3, T4, U> function4
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, function4.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param m5 monadic argument
     * @param function5 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <T5> m5 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <M5> fifth monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, T5, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            M5 extends IMonad<T5, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, M5 m5, IFunction5<T1, T2, T3, T4, T5, U> function5
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, m5, function5.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param m5 monadic argument
     * @param m6 monadic argument
     * @param function6 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <T5> m5 value type
     * @param <T6> m5 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <M5> fifth monad type
     * @param <M6> sixth monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, T5, T6, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            M5 extends IMonad<T5, M>,
            M6 extends IMonad<T6, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, M5 m5, M6 m6, IFunction6<T1, T2, T3, T4, T5, T6, U> function6
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, m5, m6, function6.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param m5 monadic argument
     * @param m6 monadic argument
     * @param m7 monadic argument
     * @param function7 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <T5> m5 value type
     * @param <T6> m5 value type
     * @param <T7> m6 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <M5> fifth monad type
     * @param <M6> sixth monad type
     * @param <M7> seventh monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, T5, T6, T7, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            M5 extends IMonad<T5, M>,
            M6 extends IMonad<T6, M>,
            M7 extends IMonad<T7, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, M5 m5, M6 m6, M7 m7, IFunction7<T1, T2, T3, T4, T5, T6, T7, U> function7
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, m5, m6, m7, function7.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param m5 monadic argument
     * @param m6 monadic argument
     * @param m7 monadic argument
     * @param m8 monadic argument
     * @param function8 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <T5> m5 value type
     * @param <T6> m5 value type
     * @param <T7> m6 value type
     * @param <T8> m8 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <M5> fifth monad type
     * @param <M6> sixth monad type
     * @param <M7> seventh monad type
     * @param <M8> eighth monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, T5, T6, T7, T8, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            M5 extends IMonad<T5, M>,
            M6 extends IMonad<T6, M>,
            M7 extends IMonad<T7, M>,
            M8 extends IMonad<T8, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, M5 m5, M6 m6, M7 m7, M8 m8, IFunction8<T1, T2, T3, T4, T5, T6, T7, T8, U> function8
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, m5, m6, m7, m8, function8.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param m5 monadic argument
     * @param m6 monadic argument
     * @param m7 monadic argument
     * @param m8 monadic argument
     * @param m9 monadic argument
     * @param function9 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <T5> m5 value type
     * @param <T6> m5 value type
     * @param <T7> m6 value type
     * @param <T8> m8 value type
     * @param <T9> m9 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <M5> fifth monad type
     * @param <M6> sixth monad type
     * @param <M7> seventh monad type
     * @param <M8> eighth monad type
     * @param <M9> ninth monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            M5 extends IMonad<T5, M>,
            M6 extends IMonad<T6, M>,
            M7 extends IMonad<T7, M>,
            M8 extends IMonad<T8, M>,
            M9 extends IMonad<T9, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, M5 m5, M6 m6, M7 m7, M8 m8, M9 m9,
        IFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, U> function9
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, m5, m6, m7, m8, m9, function9.carry(t1)));
    }

    /**
     * Applies function for all monadic values and yield result wrapped in monad.
     * @param m1 monadic argument
     * @param m2 monadic argument
     * @param m3 monadic argument
     * @param m4 monadic argument
     * @param m5 monadic argument
     * @param m6 monadic argument
     * @param m7 monadic argument
     * @param m8 monadic argument
     * @param m9 monadic argument
     * @param m10 monadic argument
     * @param function10 function to apply
     * @param <T1> m1 value type
     * @param <T2> m2 value type
     * @param <T3> m3 value type
     * @param <T4> m4 value type
     * @param <T5> m5 value type
     * @param <T6> m6 value type
     * @param <T7> m7 value type
     * @param <T8> m8 value type
     * @param <T9> m9 value type
     * @param <T10> m10 value type
     * @param <U> yielding monad value type
     * @param <M1> first monad type
     * @param <M2> second monad type
     * @param <M3> third monad type
     * @param <M4> fourth monad type
     * @param <M5> fifth monad type
     * @param <M6> sixth monad type
     * @param <M7> seventh monad type
     * @param <M8> eighth monad type
     * @param <M9> ninth monad type
     * @param <M10> tenth monad type
     * @param <MU> yielding monad type
     * @return new monad M with value of type U
     */
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, U,
            M1 extends IMonad<T1, M>,
            M2 extends IMonad<T2, M>,
            M3 extends IMonad<T3, M>,
            M4 extends IMonad<T4, M>,
            M5 extends IMonad<T5, M>,
            M6 extends IMonad<T6, M>,
            M7 extends IMonad<T7, M>,
            M8 extends IMonad<T8, M>,
            M9 extends IMonad<T9, M>,
            M10 extends IMonad<T10, M>,
            MU extends IMonad<U, M>
        > MU yieldFor(
        M1 m1, M2 m2, M3 m3, M4 m4, M5 m5, M6 m6, M7 m7, M8 m8, M9 m9, M10 m10,
        IFunction10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, U> function10
    ) {
        return (MU) m1.flatMap((t1) -> (MU) yieldFor(m2, m3, m4, m5, m6, m7, m8, m9, m10, function10.carry(t1)));
    }
}
