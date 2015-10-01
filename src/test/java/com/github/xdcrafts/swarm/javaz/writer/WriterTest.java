package com.github.xdcrafts.swarm.javaz.writer;

import com.github.xdcrafts.swarm.javaz.common.monoid.impl.ListMonoid;
import com.github.xdcrafts.swarm.javaz.common.monoid.impl.StringMonoid;
import com.github.xdcrafts.swarm.javaz.common.tuple.Tuple;
import com.github.xdcrafts.swarm.javaz.option.IOption;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.option.OptionOps.option;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.none;
import static org.junit.Assert.assertEquals;

/**
 * Created by vdubs
 * on 1/21/15.
 */
public class WriterTest {

    @Test
    public void writerTest() {
        final Writer<String, StringMonoid, String> writer = Writer.writer("String body", StringMonoid.mempty());
        final Tuple<List<String>, String> expected = Tuple.t(
                new ArrayList<String>() {{
                    add("STRING");
                    add("BODY");
                }},
                "toUpperCase, splitting by whitespace"
        );
        final Tuple<List<String>, String> tuple = writer
            .tell("toUpperCase, ")
            .map(String::toUpperCase)
            .tell("splitting by whitespace")
            .map((s) -> Arrays.asList(s.split(" ")))
            .runWriter();
        assertEquals(expected, tuple);
    }

    @Test
    public void writerTTest() {
        final WriterT<List<String>, ListMonoid<?>, String, IOption<?>> writerT =
            WriterT.writerT(option("String body"), ListMonoid.mempty());
        final WriterT<List<String>, ListMonoid<?>, String, IOption<?>> emptyWriterT =
            WriterT.writerT(option(null), ListMonoid.mempty());
        final IOption<Tuple<List<String>, List<String>>> expected = option(Tuple.t(
                new ArrayList<String>() {{
                    add("STRING");
                    add("BODY");
                }},
                new ArrayList<String>() {{
                    add("toUpperCase");
                    add("splitting by whitespace");
                }}
        ));
        final Function<
                    WriterT<List<String>, ListMonoid<?>, String, IOption<?>>,
                    IOption<Tuple<List<String>, List<String>>>
                > fun =
                w -> w.tell(ListMonoid.asList("toUpperCase"))
                        .map(String::toUpperCase)
                        .tell(ListMonoid.asList("splitting by whitespace"))
                        .map((s) -> Arrays.asList(s.split(" ")))
                        .run();
        final IOption<Tuple<List<String>, List<String>>> actual = fun.apply(writerT);
        assertEquals(expected, actual);
        assertEquals(none(), fun.apply(emptyWriterT));
    }
}
