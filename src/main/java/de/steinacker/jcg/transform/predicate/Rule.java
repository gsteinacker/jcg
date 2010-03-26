/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.predicate;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Rule<T, R> {

    /**
     * Applies a rule on obj and returns a result.
     *
     * @param obj the object the rule is applied to.
     * @return the resulting value.
     */
    public R apply(T obj);
}
