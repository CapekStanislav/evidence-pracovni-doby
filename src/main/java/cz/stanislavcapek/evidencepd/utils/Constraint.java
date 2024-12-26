package cz.stanislavcapek.evidencepd.utils;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An instance of class {@code Constraint}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class Constraint<T> {

    protected final Predicate<T> predicate;
    protected final Function<T, ? extends RuntimeException> exceptionGenerator;

    public Constraint(Predicate<T> predicate, Function<T, ? extends RuntimeException> exceptionGenerator) {
        this.predicate = predicate;
        this.exceptionGenerator = exceptionGenerator;
    }

    public boolean check(T type) {
        return predicate.test(type);
    }

    public boolean orThrow(T type) throws RuntimeException {
        final boolean result = predicate.test(type);
        if (!result) {
            throw exceptionGenerator.apply(type);
        }
        return true;
    }


}
