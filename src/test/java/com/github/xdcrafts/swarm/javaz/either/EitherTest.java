package com.github.xdcrafts.swarm.javaz.either;

import org.junit.Test;

import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.either.EitherOps.left;
import static com.github.xdcrafts.swarm.javaz.either.EitherOps.right;

import static org.junit.Assert.assertEquals;

/**
 * Created by vdubs
 * on 8/22/14.
 */
@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
public class EitherTest {

    @Test
    public void fmapTest() {
        final Function<String, String> function = s -> s + "two";
        final IEither<String, String> left = left("one");
        assertEquals(left("one"), left.map(function));
        assertEquals(right("onetwo"), right("one").map(function));
    }

    @Test
    public void amapTest() {
        final Function<String, String> function = s -> s + "two";
        final IEither<?, Function<String, String>> rightF = EitherOps.ID.pure(function);
        final IEither<String, Function<String, String>> leftF = left("nofun");
        final IEither<String, String> left = left("one");
        assertEquals(left("one"), left.applicativeMap(rightF));
        assertEquals(left("nofun"), left.applicativeMap(leftF));
        assertEquals(right("onetwo"), right("one").applicativeMap(rightF));
        assertEquals(left("nofun"), right("one").applicativeMap(leftF));
    }

    @Test
    public void mmapTest() {
        final Function<String, IEither<String, String>> rightFunction = s -> right(s + "right");
        final Function<String, IEither<String, String>> leftFunction = s -> left(s + "left");
        final IEither<String, String> left = left("one");
        assertEquals(left("one"), left.flatMap(rightFunction));
        assertEquals(left("one"), left.flatMap(leftFunction));
        assertEquals(right("oneright"), right("one").flatMap(rightFunction));
        assertEquals(left("oneleft"), right("one").flatMap(leftFunction));
    }

    @Test
    public void yieldForTestRight() {
        final Either<String, Integer> either = EitherOps.ID.yieldFor(
            right("first"), right("second"), right("third").map(String::length),
            (s1, s2, i3) -> s1.length() + s2.length() + i3
        );
        assertEquals(right(16), either);
    }

    @Test
    public void yieldForTestLeft() {
        final Either<String, Integer> either = EitherOps.ID.yieldFor(
            right("first"), left("fail"), right("third").map(String::length),
            (String s1, String s2, Integer i3) -> s1.length() + s2.length() + i3
        );
        assertEquals(left("fail"), either);
    }
}
