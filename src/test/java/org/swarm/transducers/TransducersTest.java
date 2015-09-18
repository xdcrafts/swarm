package org.swarm.transducers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.swarm.transducers.Implementations.map;
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

    private static ITransducer<String, Long> stringify = map(Object::toString);

    private static IReducer<List<String>, String> addString = (result, input) -> {
        result.add(input);
        return reduction(result);
    };

    @Test
    public void testMap() throws Exception {
        final Reduction<List<String>> reduction = transduce(stringify, addString, new ArrayList<>(), longs(10));
        assertFalse(reduction.isFailed());
        assertTrue(reduction.isReduced());
        final String[] expected = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        assertEquals(Arrays.asList(expected), reduction.get());
    }
}
