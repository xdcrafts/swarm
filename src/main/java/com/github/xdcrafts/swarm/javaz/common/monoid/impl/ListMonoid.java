package com.github.xdcrafts.swarm.javaz.common.monoid.impl;

import com.github.xdcrafts.swarm.javaz.common.monoid.Monoid;
import com.github.xdcrafts.swarm.javaz.common.monoid.MonoidOps;

import java.util.ArrayList;
import java.util.List;

/**
 * List monoid of type T.
 * @param <T> values type
 */
public class ListMonoid<T> implements Monoid<List<T>, ListMonoid<?>> {
    private final List<T> value;
    public ListMonoid(List<T> value) {
        this.value = value;
    }
    /**
     * Zero value.
     * @param <T> monoid value type
     * @return new monoid instance
     */
    public static <T> ListMonoid<T> mempty() {
        return wrap(new ArrayList<>());
    }
    /**
     * Wraps value with monoid.
     * @param ts list
     * @param <T> value type
     * @return list monoid
     */
    public static <T> ListMonoid<T> wrap(List<T> ts) {
        return new ListMonoid<>(ts);
    }
    /**
     * Wraps single value of type T with list monoid.
     * @param ts value
     * @param <T> value type
     * @return list monoid
     */
    public static <T> ListMonoid<T> wrapOne(final T ts) {
        return new ListMonoid<>(new ArrayList<T>() {
                {
                    add(ts);
                }
            });
    }
    /**
     * Converts single value to list.
     * @param ts value
     * @param <T> type of value
     * @return list
     */
    public static <T> List<T> asList(final T ts) {
        return new ArrayList<T>() {
            {
                add(ts);
            }
        };
    }
    @Override
    public ListMonoid<T> mappend(Monoid<List<T>, ListMonoid<?>> another) {
        final List<T> newValue = new ArrayList<>();
        newValue.addAll(this.value());
        newValue.addAll(another.value());
        return (ListMonoid<T>) ops().wrap(newValue);
    }
    @Override
    public List<T> value() {
        return this.value;
    }
    @Override
    public MonoidOps<List<T>, ListMonoid<?>> ops() {
        return Ops.listMonoid();
    }
    @Override
    public String toString() {
        return this.value.toString();
    }

    /**
     * List monoid operations.
     * @param <T> type of values.
     */
    public static class Ops<T> implements MonoidOps<List<T>, ListMonoid<?>> {
        /**
         * Creates new instance of list monoid operations.
         * @param <T> type of values
         * @return ops instance
         */
        public static <T> Ops<T> listMonoid() {
            return new Ops<>();
        }
        @Override
        public ListMonoid<T> mempty() {
            return wrap(new ArrayList<>());
        }
        @Override
        public ListMonoid<T> wrap(List<T> ts) {
            return new ListMonoid<>(ts);
        }
    }
}
