package com.github.xdcrafts.swarm.javaz.future;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.javaz.common.tuple.Tuple;
import com.github.xdcrafts.swarm.javaz.option.IOption;
import com.github.xdcrafts.swarm.javaz.trym.ITryM;
import com.github.xdcrafts.swarm.util.function.IFunction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.future.FutureOps.future;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.none;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.some;
import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.fail;
import static com.github.xdcrafts.swarm.javaz.trym.TryMOps.success;

/**
 * Implementation of IFuture.
 * @param <T> value type
 */
@SuppressWarnings("unchecked")
public class Future<T> implements IFuture<T> {

    private final Executor executor;
    private final CompletableFuture<T> sourceFuture;
    private final CompletableFuture<T> body;
    private final AtomicReference<IOption<ITryM<T>>> result = new AtomicReference<>(none());

    public Future(final Executor executor, final CompletableFuture<T> future) {
        this.executor = executor;
        this.sourceFuture = future;
        this.body = future.whenComplete((r, t) -> {
                if (r != null) {
                    result.set(some(success(r)));
                } else if (t != null) {
                    Throwable cause = t;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    result.set(some(fail(cause)));
                }
            });
    }

    @Override
    public boolean complete(T value) {
        return complete(success(value));
    }

    @Override
    public boolean complete(Throwable t) {
        return complete(fail(t));
    }

    @Override
    public boolean complete(ITryM<T> t) {
        return t.isSuccess()
            ? this.sourceFuture.complete(t.value()) : this.sourceFuture.completeExceptionally(t.throwable());
    }

    @Override
    public void onSuccess(Consumer<T> onSuccess) {
        this.body.whenCompleteAsync((r, t) ->
            this.result.get().foreach((tryM) -> tryM.foreach(onSuccess))
        );
    }

    @Override
    public void onFailure(Consumer<Throwable> onFailure) {
        this.body.whenCompleteAsync((r, t) ->
            this.result.get().foreach((tryM) -> tryM.foreachFailure(onFailure))
        );
    }

    @Override
    public void onComplete(Consumer<ITryM<T>> onComplete) {
        this.body.whenCompleteAsync((r, t) -> onComplete.accept(result.get().get()));
    }

    @Override
    public boolean isCompleted() {
        return this.body.isDone();
    }

    @Override
    public IFuture<T> recover(Function<Throwable, T> recover) {
        return future(this.executor, this.body.exceptionally(recover::apply));
    }

    @Override
    public IFuture<T> recoverWith(Function<Throwable, IFuture<T>> recover) {
        final Future<T> future = future(this.executor);
        onFailure((t) -> recover.apply(t).onComplete(future::complete));
        onSuccess(future::complete);
        return future;
    }

    @Override
    public IOption<ITryM<T>> value() {
        return this.result.get();
    }

    @Override
    public ITryM<T> get() {
        try {
            this.body.get();
            return this.result.get().get();
        } catch (Throwable e) {
            if (this.body.isCompletedExceptionally()) {
                return this.result.get().get();
            } else {
                return fail(e);
            }
        }
    }

    @Override
    public ITryM<T> get(long timeout, TimeUnit timeUnit) {
        try {
            this.body.get(timeout, timeUnit);
            return this.result.get().get();
        } catch (Throwable e) {
            if (this.body.isCompletedExceptionally()) {
                return this.result.get().get();
            } else {
                return fail(e);
            }
        }
    }

    @Override
    public <U> IFuture<Tuple<T, U>> zip(IFuture<U> another) {
        return flatMap((t) -> another.map((u) -> Tuple.t(t, u)));
    }

    @Override
    public <U, MM extends IMonad<U, IFuture<?>>> Future<U> flatMap(Function<T, MM> function) {
        final Future<U> future = future(this.executor);
        final IFunction<T, Future<U>> toApply = (IFunction<T, Future<U>>) function;
        onComplete((tryM) -> {
                if (tryM.isSuccess()) {
                    toApply.apply(tryM.value()).onComplete(future::complete);
                } else {
                    future.complete(tryM.throwable());
                }
            });
        return future;
    }

    @Override
    public <U, MM extends IApplicative<Function<T, U>, IFuture<?>>> IFuture<U> applicativeMap(MM applicativeFunction) {
        final IFuture<Function<T, U>> applicative = (Future<Function<T, U>>) applicativeFunction;
        final IFuture<U> future = future(this.executor);
        applicative.onComplete((tryM) -> {
                if (tryM.isSuccess()) {
                    onComplete((iTryM) -> {
                            if (iTryM.isSuccess()) {
                                future.complete(tryM.value().apply(iTryM.value()));
                            } else {
                                future.complete(iTryM.throwable());
                            }
                        });
                } else {
                    future.complete(tryM.throwable());
                }
            });
        return future;
    }

    @Override
    public <U> IFuture<U> map(Function<T, U> function) {
        return future(this.executor, this.body.handleAsync((r, t) -> {
                if (r != null) {
                    return function.apply(r);
                } else if (t.getClass().equals(CompletionException.class)) {
                    throw (CompletionException) t;
                } else {
                    throw new RuntimeException(t);
                }
            }));
    }

    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return this.body;
    }
}
