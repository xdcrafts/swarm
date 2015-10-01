package com.github.xdcrafts.swarm.javaz.writer;

import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.javaz.common.monoid.Monoid;
import com.github.xdcrafts.swarm.javaz.common.tuple.Tuple;

import java.util.function.Function;

/**
 * Writer monad transformer.
 * @param <T> context value type
 * @param <MT> context monoid type
 * @param <A> value type
 * @param <MA> monad value type
 */
@SuppressWarnings("unchecked")
public class WriterT<T, MT extends Monoid<?, MT>, A, MA extends IMonad<?, MA>> {

    protected final Monoid<T, MT> context;
    protected final IMonad<A, MA> body;

    protected WriterT(IMonad<A, MA> body, Monoid<T, MT> context) {
        this.context = context;
        this.body = body;
    }

    /**
     * Creates new instance of writer transformer.
     * @param body value wrapped in monad
     * @param context context wrapped in monoid
     * @param <T> context value type
     * @param <MT> context monoid type
     * @param <A> value type
     * @param <MA> monad value type
     * @return writer transformer
     */
    public static <T, MT extends Monoid<?, MT>, A, MA extends IMonad<?, MA>> WriterT<T, MT, A, MA> writerT(
        IMonad<A, MA> body, Monoid<T, MT> context) {
        return new WriterT<>(body, context);
    }

    /**
     * Returns value and context tuple wrapped with monad.
     * @param <MAT> return monad type
     * @return monad with tuple of value and context
     */
    public <MAT extends IMonad<Tuple<A, T>, MA>> MAT run() {
        return (MAT) this.body.map(a -> Tuple.t(a, context.value()));
    }

    /**
     * Returns context wrapped with monad.
     * @return monad
     */
    public IMonad<T, MA> exec() {
        return (IMonad<T, MA>) this.body.map(a -> context.value());
    }

    /**
     * Appends new value to context.
     * @param t context value
     * @return writer transformer
     */
    public WriterT<T, MT, A, MA> tell(final T t) {
        return writerT(this.body, (Monoid<T, MT>) this.context.mappend(this.context.ops().wrap(t)));
    }

    /**
     * Maps function over underlying value in monad.
     * @param function mapper
     * @param <U> new value type
     * @return writer transformer
     */
    public <U> WriterT<T, MT, U, MA> map(final Function<A, U> function) {
        return writerT((IMonad<U, MA>) this.body.map(function), this.context);
    }

    /**
     * Flat maps function over underlying value in monad.
     * @param function mapper
     * @param <U> new value type
     * @return writer transformer
     */
    public <U> WriterT<T, MT, U, MA> flatMap(final Function<A, IMonad<U, MA>> function) {
        return writerT((IMonad<U, MA>) this.body.flatMap(function), this.context);
    }

    @Override
    public String toString() {
        return "WriterT{" + this.body + '}';
    }
}
