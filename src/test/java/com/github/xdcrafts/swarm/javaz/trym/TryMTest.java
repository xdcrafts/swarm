package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.util.function.IThrowingFunction;
import com.github.xdcrafts.swarm.util.function.IThrowingSupplier;
import org.junit.Test;

import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.fail;
import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.success;
import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.tryFunction;
import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.trySupplier;

import static org.junit.Assert.assertEquals;

/**
 * Created by vdubs
 * on 8/22/14.
 */
@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
public class TryMTest {

    final Exception fail = new Exception("fail");

    final TryFunction<Void, String> successfull = tryFunction(s -> "success");

    final TryFunction<Void, String> failure = tryFunction(s -> {
        throw fail;
    });

    @Test
    public void fmapTest() {
        final Function<String, String> function = s -> s + "f";
        final Exception tfail = new Exception("tfail");
        final IThrowingFunction<String, String> throwableFunction = s -> { throw tfail; };
        assertEquals(success("successf"), successfull.apply(null).map(function));
        assertEquals(fail(tfail), successfull.apply(null).mapT(throwableFunction));
        assertEquals(fail(fail), failure.apply(null).map(function));
        assertEquals(fail(fail), failure.apply(null).mapT(throwableFunction));
    }

    @Test
    public void amapTest() {
        final Exception tfail = new Exception("tfail");
        final Function<String, String> function = s -> s + "f";
        final TryM<Function<String, String>> fas = success(function);
        final TryM<Function<String, String>> faf = fail(tfail);
        final IThrowingFunction<String, String> throwableFunction = s -> { throw tfail; };
        final TryM<IThrowingFunction<String, String>> tfas = success(throwableFunction);
        final TryM<IThrowingFunction<String, String>> tfaf = fail(tfail);
        assertEquals(success("successf"), successfull.apply(null).applicativeMap(fas));
        assertEquals(fail(tfail), successfull.apply(null).applicativeMapT(tfas));
        assertEquals(fail(tfail), successfull.apply(null).applicativeMap(faf));
        assertEquals(fail(tfail), successfull.apply(null).applicativeMapT(tfaf));
        assertEquals(fail(fail), failure.apply(null).applicativeMap(fas));
        assertEquals(fail(fail), failure.apply(null).applicativeMapT(tfas));
        assertEquals(fail(tfail), failure.apply(null).applicativeMap(faf));
        assertEquals(fail(tfail), failure.apply(null).applicativeMapT(tfaf));
    }

    @Test
    public void mmapTest() {
        final Exception tfail = new Exception("tfail");
        final Function<String, TryM<String>> fs = s -> success(s + "f");
        final Function<String, TryM<String>> ff = s -> fail(tfail);
        final IThrowingFunction<String, ITryM<String>> tft = s -> { throw tfail; };
        assertEquals(success("successf"), successfull.apply(null).flatMap(fs));
        assertEquals(fail(tfail), successfull.apply(null).flatMap(ff));
        assertEquals(fail(tfail), successfull.apply(null).flatMapT(tft));
        assertEquals(fail(fail), failure.apply(null).flatMap(fs));
        assertEquals(fail(fail), failure.apply(null).flatMap(ff));
        assertEquals(fail(fail), failure.apply(null).flatMapT(tft));
    }

    @Test
    public void closureTest() {
        final ITryM<String> success = trySupplier(() -> "test").get();
        final IThrowingSupplier<String> failing = () -> {throw this.fail;};
        final ITryM<String> failure = trySupplier(failing).get();
        assertEquals(success("TEST"), success.map(String::toUpperCase));
        assertEquals(fail(fail), failure.map(String::toUpperCase));
    }
}
