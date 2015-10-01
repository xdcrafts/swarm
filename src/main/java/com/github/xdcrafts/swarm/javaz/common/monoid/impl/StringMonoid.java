package com.github.xdcrafts.swarm.javaz.common.monoid.impl;

import com.github.xdcrafts.swarm.javaz.common.monoid.Monoid;
import com.github.xdcrafts.swarm.javaz.common.monoid.MonoidOps;

/**
 * String monoid.
 */
public final class StringMonoid implements Monoid<String, StringMonoid> {
    private final String value;
    private StringMonoid(String value) {
        this.value = value;
    }
    /**
     * Empty string wrapped with monoid.
     * @return string monoid
     */
    public static StringMonoid mempty() {
        return wrap("");
    }
    /**
     * Wraps string with monoid.
     * @param s string
     * @return string monoid
     */
    public static StringMonoid wrap(String s) {
        return new StringMonoid(s);
    }
    @Override
    public StringMonoid mappend(Monoid<String, StringMonoid> another) {
        return Ops.STRING_MONOID.wrap(this.value() + another.value());
    }
    @Override
    public String value() {
        return this.value;
    }
    @Override
    public MonoidOps<String, StringMonoid> ops() {
        return Ops.STRING_MONOID;
    }
    @Override
    public String toString() {
        return value;
    }

    /**
     * String monoid operations.
     */
    public static final class Ops implements MonoidOps<String, StringMonoid> {
        public static final Ops STRING_MONOID = new Ops();
        private Ops() {
            // Nothing
        }
        @Override
        public StringMonoid mempty() {
            return wrap("");
        }
        @Override
        public StringMonoid wrap(String s) {
            return new StringMonoid(s);
        }
    }
}
