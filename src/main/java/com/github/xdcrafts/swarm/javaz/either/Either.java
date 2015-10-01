package com.github.xdcrafts.swarm.javaz.either;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;
import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.javaz.option.IOption;

import java.io.Serializable;
import java.util.function.Function;

import static com.github.xdcrafts.swarm.javaz.option.OptionOps.none;
import static com.github.xdcrafts.swarm.javaz.option.OptionOps.some;

/**
 * Implementation of either monad.
 * @param <L> left value type
 * @param <R> right value type
 */
public abstract class Either<L, R> implements Serializable, IEither<L, R> {

    private static final long serialVersionUID = 0;

    @Override
    public abstract <U> IEither<L, U> map(Function<R, U> function);

    @Override
    public <U, MM extends IApplicative<Function<R, U>, IEither<?, ?>>> IEither<L, U> applicativeMap(
        MM applicativeFunction) {
        //noinspection unchecked
        final IEither<L, IEither<L, U>> mapped = (IEither<L, IEither<L, U>>) applicativeFunction
            .map((Function<Function<R, U>, IEither<L, U>>) Either.this::map);
        final IEither<L, U> result;
        if (mapped.isLeft()) {
            result = EitherOps.left(mapped.left());
        } else {
            result = mapped.right();
        }
        return result;
    }

    @Override
    public <U, MM extends IMonad<U, IEither<?, ?>>> IEither<L, U> flatMap(Function<R, MM> function) {
        //noinspection unchecked
        final IEither<L, IEither<L, U>> mapped = (IEither<L, IEither<L, U>>) map(function);
        final IEither<L, U> result;
        if (mapped.isLeft()) {
            result = EitherOps.left(mapped.left());
        } else {
            result = mapped.right();
        }
        return result;
    }

    @Override
    public abstract <U> IEither<U, R> mapLeft(Function<L, U> function);

    @Override
    public <U> IEither<U, R> applicativeMapLeft(IEither<?, Function<L, U>> applicativeFunction) {
        //noinspection unchecked
        final IEither<IEither<U, R>, R> mapped = (IEither<IEither<U, R>, R>) applicativeFunction
            .map((Function<Function<L, U>, IEither<U, R>>) Either.this::mapLeft);
        final IEither<U, R> result;
        if (mapped.isLeft()) {
            result = mapped.left();
        } else {
            result = EitherOps.right(mapped.right());
        }
        return result;
    }

    @Override
    public <U> IEither<U, R> flatMapLeft(Function<L, IEither<U, R>> function) {
        //noinspection unchecked
        return mapLeft(function).left();
    }

    @Override
    public IOption<R> asOption() {
        return isRight() ? some(right()) : none();
    }
}
