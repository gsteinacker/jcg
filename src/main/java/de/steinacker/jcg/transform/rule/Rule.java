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
     * @return the resulting values, or an empty list if the rule was not able to find one.
     */
    public List<R> apply(T obj);
}
