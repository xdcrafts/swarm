package com.github.xdcrafts.swarm.javaz.common.tuple;

/**
 * heterogeneous container for two values.
 * @param <A> first value type
 * @param <B> second value type
 */
public final class Tuple<A, B> {

    private final A first;
    private final B second;

    /**
     * Creates tuple from two values.
     * @param first first value
     * @param second second value
     * @param <A> first value type
     * @param <B> second value type
     * @return tuple
     */
    public static <A, B> Tuple<A, B> t(A first, B second) {
        return new Tuple<>(first, second);
    }

    private Tuple(A first, B second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Values of WriterT<W, A> can not be null!");
        }
        this.first = first;
        this.second = second;
    }

    /**
     * Returns first value.
     * @return value of type A
     */
    public A first() {
        return this.first;
    }

    /**
     * Returns second value.
     * @return value of type B
     */
    public B second() {
        return this.second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tuple tuple = (Tuple) o;
        return this.first.equals(tuple.first) && this.second.equals(tuple.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + this.first + ", " + this.second + ')';
    }
}
