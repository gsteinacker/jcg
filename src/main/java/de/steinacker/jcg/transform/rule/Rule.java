/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.rule;

import java.util.List;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Rule<T, R> {

    /**
     * Applies a rule on obj and returns a result.
     *
     * @param obj the object the rule is applied to.
     * @return the resulting value, or null if the rule was not able to find one.
     */
    public R apply(T obj);
}
