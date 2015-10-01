package com.github.xdcrafts.swarm.javaz.writer;

import com.github.xdcrafts.swarm.javaz.common.monoid.Monoid;
import com.github.xdcrafts.swarm.javaz.common.tuple.Tuple;
import com.github.xdcrafts.swarm.javaz.id.Id;
import com.github.xdcrafts.swarm.javaz.id.IdOps;

import java.util.function.Function;

/**
 * Writer monad.
 * @param <T> value type
 * @param <MT> context monoid type
 * @param <A> context type
 */
@SuppressWarnings("unchecked")
public final class Writer<T, MT extends Monoid<?, MT>, A> extends WriterT<T, MT, A, Id<?>> {

    private Writer(A body, Monoid<T, MT> context) {
        super(IdOps.id(body), context);
    }

    /**
     * Creates new writer monad.
     * @param body value of type A
     * @param context context of type T
     * @param <T> context type
     * @param <MT> context monoid type
     * @param <A> value type
     * @return writer monad
     */
    public static <T, MT extends Monoid<?, MT>, A> Writer<T, MT, A> writer(A body, Monoid<T, MT> context) {
        return new Writer<>(body, context);
    }

    @Override
    public Id<Tuple<A, T>> run() {
        return (Id<Tuple<A, T>>) super.run();
    }

    /**
     * Same as run but unwraps tuple from Id monad.
     * @return tuple with value and context
     */
    public Tuple<A, T> runWriter() {
        return ((Id<Tuple<A, T>>) super.run()).get();
    }

    @Override
    public Id<T> exec() {
        return (Id<T>) super.exec();
    }

    /**
     * Same as exec but unwraps value from Id monad.
     * @return value of type T
     */
    public T execWriter() {
        return ((Id<T>) super.exec()).get();
    }

    @Override
    public Writer<T, MT, A> tell(final T t) {
        final WriterT<T, MT, A, Id<?>> wt = super.tell(t);
        return writer(((Id<A>) wt.body).get(), wt.context);
    }

    @Override
    public <U> Writer<T, MT, U> map(final Function<A, U> function) {
        final WriterT<T, MT, U, Id<?>> wt = super.map(function);
        return writer(((Id<U>) wt.body).get(), wt.context);
    }

    @Override
    public String toString() {
        return "Writer{" + this.body + '}';
    }
}
