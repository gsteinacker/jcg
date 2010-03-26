/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.predicate;

/**
 * Always evaluates to true.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TruePredicate<T> implements Predicate<T> {

    @Override
    public boolean eval(final T param) {
        return true;
    }
}
