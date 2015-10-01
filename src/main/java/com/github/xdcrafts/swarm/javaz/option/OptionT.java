package com.github.xdcrafts.swarm.javaz.option;

import com.github.xdcrafts.swarm.javaz.common.monad.IMonad;
import com.github.xdcrafts.swarm.util.function.IFunction;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Option monad transformer.
 * @param <T> value type
 * @param <M> another monad type
 */
@SuppressWarnings("unchecked")
public final class OptionT<T, M extends IMonad<?, M>> {

    private final IMonad<IOption<T>, M> body;

    private OptionT(IMonad<IOption<T>, M> body) {
        this.body = body;
    }

    /**
     * Constructs new option transformer instance.
     * @param body internal representation of transformer
     * @param <T> value type
     * @param <M> monad type
     * @return option transformer
     */
    public static <T, M extends IMonad<?, M>> OptionT<T, M> optionT(IMonad<IOption<T>, M> body) {
        return new OptionT<>(body);
    }

    /**
     * Executes transformer and returns option packed in another monad.
     * @param <MT> resulting monad type
     * @return monad
     */
    public <MT extends IMonad<IOption<T>, M>> MT run() {
        return (MT) this.body;
    }

    /**
     * Map on internal option.
     * @param function function to map
     * @param <U> new value type
     * @return option transformer
     */
    public <U> OptionT<U, M> map(final IFunction<T, U> function) {
        return optionT(
            (IMonad<IOption<U>, M>) this.body.map(tOption -> tOption.map(function))
        );
    }

    /**
     * Applicative map on internal option.
     * @param function function to map
     * @param <U> new value type
     * @return option transformer
     */
    public <U> OptionT<U, M> applicativeMap(final IOption<Function<T, U>> function) {
        return optionT(
            (IMonad<IOption<U>, M>) this.body.map(tOption -> tOption.applicativeMap(function))
        );
    }

    /**
     * Flat map on internal option.
     * @param function function to map
     * @param <U> new value type
     * @return option transformer
     */
    public <U> OptionT<U, M> flatMap(final IFunction<T, IOption<U>> function) {
        return optionT(
            (IMonad<IOption<U>, M>) this.body.map(tOption -> tOption.flatMap(function))
        );
    }

    /**
     * If internal optional is defined.
     * @param <MT> resulting monad type
     * @return monad
     */
    public <MT extends IMonad<Boolean, M>> MT isDefined() {
        return (MT) this.body.map(IOption::isDefined);
    }

    /**
     * Filter on internal option monad.
     * @param predicate filtering function
     * @return option transformer
     */
    public OptionT<T, M> filter(final Predicate<T> predicate) {
        return optionT(
            (IMonad<IOption<T>, M>) this.body.map(tOption -> tOption.filter(predicate))
        );
    }

    /**
     * Extracts value from internal option.
     * @param <MT> return monad type
     * @return monad
     */
    public <MT extends IMonad<T, M>> MT get() {
        return (MT) this.body.map(IOption::get);
    }

    /**
     * Extracts value with orNull from internal option.
     * @param <MT> return monad type
     * @return monad
     */
    public <MT extends IMonad<T, M>> MT orNull() {
        return (MT) this.body.map(IOption::orNull);
    }

    /**
     * Extracts value with orElse from internal option.
     * @param <MT> return monad type
     * @return monad
     */
    public <MT extends IMonad<T, M>> MT orElse(final T elseValue) {
        return (MT) this.body.map(tOption -> tOption.orElse(elseValue));
    }

    /**
     * Extracts value with or from internal option.
     * @param <MT> return monad type
     * @return monad
     */
    public <MT extends IMonad<T, M>> MT or(final IOption<T> elseValue) {
        return (MT) this.body.map(tiOption -> tiOption.or(elseValue));
    }

    /**
     * Extracts value with fold on internal option.
     * @param ifNone default value
     * @param function function to apply on option value
     * @param <U> new value type
     * @param <MU> return monad type
     * @return monad
     */
    public <U, MU extends IMonad<U, M>> MU fold(final U ifNone, final Function<T, U> function) {
        return (MU) this.body.map(tOption -> tOption.fold(ifNone, function));
    }

    @Override
    public String toString() {
        return "OptionT{" + this.body + '}';
    }
}
