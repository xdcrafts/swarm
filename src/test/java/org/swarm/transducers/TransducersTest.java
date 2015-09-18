package org.swarm.transducers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.swarm.transducers.Implementations.map;
import static org.swarm.transducers.Implementations.filter;
import static org.swarm.transducers.Implementations.cat;
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
}
