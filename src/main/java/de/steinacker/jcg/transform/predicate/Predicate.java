/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.predicate;

/**
 * A Predicate is used to decide whether a parameter meets a condition.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Predicate<T> {

    public boolean eval(final T param);

}
