package com.github.xdcrafts.swarm.javaz.id;

import org.junit.Test;

import static com.github.xdcrafts.swarm.javaz.id.IdOps.ID;
import static com.github.xdcrafts.swarm.javaz.id.IdOps.id;
import static org.junit.Assert.assertEquals;

/**
 * Created by vdubs
 * on 8/25/14.
 */
public class IdTest {

    @Test
    public void fmapTest() {
        assertEquals(id("onetwo"), id("one").map(s -> s + "two"));
    }

    @Test
    public void amapTest() {
        assertEquals(id("onetwo"), id("one").applicativeMap(ID.pure(s -> s + "two")));
    }

    @Test
    public void mmapTest() {
        assertEquals(id("onetwo"), id("one").flatMap(s -> ID.pure(s + "two")));
    }
}
