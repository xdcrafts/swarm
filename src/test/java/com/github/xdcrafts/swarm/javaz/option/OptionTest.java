package com.github.xdcrafts.swarm.javaz.option;

import com.github.xdcrafts.swarm.javaz.either.EitherOps;
import com.github.xdcrafts.swarm.javaz.either.IEither;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.option.OptionOps.ID;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.fromOptional;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.none;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.option;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.some;
import static org.junit.Assert.assertEquals;

/**
 * Created by vdubs
 * on 8/20/14.
 */
public class OptionTest {

    @Test
    public void fromToOptionalTest() {
        final Function<Optional<String>, Optional<String>> fun =
                (o) -> fromOptional(o.map(String::toUpperCase)).map(String::toLowerCase).toOptional();
        final Optional<String> optional = Optional.of("value");
        final Optional<String> emptyOptional = Optional.empty();
        final Optional<String> processedNonEmptyOptional = fun.apply(optional);
        final Optional<String> processedEmptyOptional = fun.apply(emptyOptional);
        assertEquals(optional, processedNonEmptyOptional);
        assertEquals(emptyOptional, processedEmptyOptional);
    }

    @Test
    public void maybeTest() {
        assertEquals(none(), option(null).flatMap((s) -> some("hello")));
        assertEquals(some("some string"), option("some string"));
        assertEquals(none(), option(null));
    }

    @Test
    public void maybeOrTest() {
        final IOption<String> first = none();
        final IOption<String> second = option("second");
        assertEquals("second", first.or(second).orElse("third"));
    }

    @Test
    public void maybeFMapTest() {
        assertEquals(some("onetwo"), option("one").map(s -> s + "two"));
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(none(), option((String) null).map(s -> s + "two"));
    }

    @Test
    public void maybeAMapTest() {
        assertEquals(some("onetwo"), option("one").applicativeMap(ID.pure(s -> s + "two")));
    }

    @Test
    public void maybeMMapTest() {
        assertEquals(some("onetwo"), option("one").flatMap(s -> some(s + "two")));
        assertEquals(none(), option(null));
    }

    @Test
    public void yieldForTest() {
        final Option<String> result = ID.yieldFor(
            ID.pure("1"), ID.pure("2"), ID.pure("3"),
            (s1, s2, s3) -> s1 + s2 + s3
        );
        assertEquals(some("123"), result);
    }

    @Test
    public void optionTTest() {
        final OptionT<String, IEither<?, ?>> rightSomeT = OptionT.optionT(
            EitherOps.<String, IOption<String>>right(some("right"))
        );
        final OptionT<String, IEither<?, ?>> rightNoneT = OptionT.optionT(
            EitherOps.<String, IOption<String>>right(none())
        );
        final OptionT<String, IEither<?, ?>> leftOptionT = OptionT.optionT(
            EitherOps.<String, IOption<String>>left("left")
        );
        final IEither<String, String> eitherSomeR = rightSomeT.map(String::toUpperCase).get();
        final IEither<String, IOption<String>> eitherNoneR = rightNoneT.map(String::toUpperCase).run();
        final IEither<String, String> eitherL = leftOptionT.map(String::toUpperCase).get();
        assertEquals(EitherOps.<String, String>right("RIGHT"), eitherSomeR);
        assertEquals(EitherOps.<String, Option<String>>right(none()), eitherNoneR);
        assertEquals(EitherOps.<String, String>left("left"), eitherL);
    }
}
