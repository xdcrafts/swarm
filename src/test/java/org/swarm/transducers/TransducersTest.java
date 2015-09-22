package org.swarm.transducers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.swarm.transducers.Implementations.cat;
import static org.swarm.transducers.Implementations.dedupe;
import static org.swarm.transducers.Implementations.drop;
import static org.swarm.transducers.Implementations.dropWhile;
import static org.swarm.transducers.Implementations.filter;
import static org.swarm.transducers.Implementations.keep;
import static org.swarm.transducers.Implementations.keepIndexed;
import static org.swarm.transducers.Implementations.map;
import static org.swarm.transducers.Implementations.mapcat;
import static org.swarm.transducers.Implementations.partitionAll;
import static org.swarm.transducers.Implementations.partitionBy;
import static org.swarm.transducers.Implementations.replace;
import static org.swarm.transducers.Implementations.take;
import static org.swarm.transducers.Implementations.takeNth;
import static org.swarm.transducers.Implementations.takeWhile;
import static org.swarm.transducers.Reduction.reduction;
import static org.swarm.transducers.Transducers.transduce;

/**
 * Tests for transducers.
 */
public class TransducersTest {

    private static List<Integer> ints(final int n) {
        return new ArrayList<Integer>(n) {{
            for(int i = 0; i < n; i++) {
                add(i);
            }
        }};
    }

    private static List<Long> longs(final long n) {
        return new ArrayList<Long>((int)n) {{
            for(long i = 0l; i < n; i++) {
                add(i);
            }
        }};
    }

    private static <T> IReducer<List<T>, T> addReducer(Class<T> tClass) {
        return (result, input) -> {
            result.add(input);
            return reduction(result);
        };
    }

    @Test
    public void testMap() throws Exception {
        final ITransducer<String, Long> stringify = map(Object::toString);
        final Reduction<List<String>> reduction =
            transduce(stringify, addReducer(String.class), new ArrayList<>(), longs(10));
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final String[] expected = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testFilter() throws Exception {
        final ITransducer<Integer, Integer> odds = filter(integer -> integer % 2 != 0);
        final Reduction<List<Integer>> reduction = transduce(
                odds, addReducer(Integer.class), new ArrayList<>(), ints(10)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {1, 3, 5, 7, 9};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testCat() throws Exception {
        final ITransducer<Integer, Iterable<Integer>> transducer = cat();
        final List<Iterable<Integer>> data = new ArrayList<Iterable<Integer>>() {{
            add(ints(10));
            add(ints(20));
        }};
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), data
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        assertEquals(
            Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19
            ),
            reduction.get()
        );
    }

    @Test
    public void testMapcat() throws Exception {
        final ITransducer<Character, Integer> transducer = mapcat(integer -> {
                final String s = integer.toString();
                return new ArrayList<Character>(s.length()) {{
                    for (char c : s.toCharArray())
                        add(c);
                }};
            });
        final Reduction<List<Character>> reduction = transduce(
            transducer, addReducer(Character.class), new ArrayList<>(), ints(10)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Character[] expected = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testComp() throws Exception {
        final ITransducer<Integer, Integer> filter = filter(integer -> integer % 2 != 0);
        final ITransducer<String, Integer> map = map(Object::toString);
        final ITransducer<String, Integer> transducer = filter.compose(map);
        final Reduction<List<String>> reduction
            = transduce(transducer, addReducer(String.class), new ArrayList<>(), ints(10));
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        String[] expected = {"1", "3", "5", "7", "9"};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testTake() throws Exception {
        final ITransducer<Integer, Integer> transducer = take(5);
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(20)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {0, 1, 2, 3, 4};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testTakeWhile() throws Exception {
        final ITransducer<Integer, Integer> transducer = takeWhile(integer -> integer < 5);
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(20));
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {0, 1, 2, 3, 4};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testDrop() throws Exception {
        final ITransducer<Integer, Integer> transducer = drop(5);
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(10)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {5, 6, 7, 8, 9};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testDropWhile() throws Exception {
        final ITransducer<Integer, Integer> transducer = dropWhile(integer -> integer < 15);
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(20)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {15, 16, 17, 18, 19};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testTakeNth() throws Exception {
        final ITransducer<Integer, Integer> transducer = takeNth(2);
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(10)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {0, 2, 4, 6, 8};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testReplace() throws Exception {
        final ITransducer<Integer, Integer> transducer = replace(new HashMap<Integer, Integer>() {{
            put(3, 42);
        }});
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(5)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {0, 1, 2, 42, 4};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testKeep() throws Exception {
        final ITransducer<Integer, Integer> transducer = keep(integer ->
                        (integer % 2 == 0) ? Optional.<Integer>empty() : Optional.of(integer)
        );
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(10)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {1, 3, 5, 7,9};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testKeepIndexed() throws Exception {
        final ITransducer<Integer, Integer> transducer = keepIndexed((idx, integer) ->
                        (idx == 1l || idx == 4l) ? Optional.of(integer) : Optional.<Integer>empty()
        );
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), ints(10)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {1, 4};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testDedupe() throws Exception {
        final Integer[] seed = {1, 2, 2, 3, 4, 5, 5, 5, 5, 5, 5, 5, 0};
        final ITransducer<Integer, Integer> transducer = dedupe();
        final Reduction<List<Integer>> reduction = transduce(
            transducer, addReducer(Integer.class), new ArrayList<>(), Arrays.asList(seed)
        );
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final Integer[] expected = {1, 2, 3, 4, 5, 0};
        assertEquals(Arrays.asList(expected), reduction.get());
    }

    @Test
    public void testPartitionBy() throws Exception {
        final Integer[] seed = {1, 1, 1, 2, 2, 3, 4, 5, 5};
        final ITransducer<Iterable<Integer>, Integer> transducer = partitionBy(Function.identity());
        final Reduction<List<List<Integer>>> reduction = transduce(
            transducer,
            (List<List<Integer>> result, Iterable<Integer> input) -> {
                final List<Integer> ret = new ArrayList<>();
                input.forEach(ret::add);
                result.add(ret);
                return reduction(result);
            },
            new ArrayList<>(),
            Arrays.asList(seed)
        );

        final Integer[] a = {1,1,1};
        final Integer[] b = {2,2};
        final Integer[] c = {3};
        final Integer[] d = {4};
        final Integer[] e = {5,5};

        final List<List<Integer>> expected = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(a));
            add(Arrays.asList(b));
            add(Arrays.asList(c));
            add(Arrays.asList(d));
            add(Arrays.asList(e));
        }};

        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        assertEquals(expected, reduction.get());
    }

    @Test
    public void testPartitionAll() throws Exception {
        final ITransducer<Iterable<Integer>, Integer> transducer = partitionAll(3);
        final Reduction<List<List<Integer>>> reduction = transduce(
            transducer,
            (List<List<Integer>> result, Iterable<Integer> input) -> {
                final List<Integer> ret = new ArrayList<>();
                input.forEach(ret::add);
                result.add(ret);
                return reduction(result);
            },
            new ArrayList<>(),
            ints(10)
        );

        final Integer[] a = {0,1,2};
        final Integer[] b = {3,4,5};
        final Integer[] c = {6,7,8};
        final Integer[] d = {9};

        List<List<Integer>> expected = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(a));
            add(Arrays.asList(b));
            add(Arrays.asList(c));
            add(Arrays.asList(d));
        }};

        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        assertEquals(expected, reduction.get());
    }
}
