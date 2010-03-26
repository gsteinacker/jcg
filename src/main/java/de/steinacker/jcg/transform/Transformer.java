/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform;

/**
 * Transforms source messages into target messages.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Transformer<S, T> {
    /**
     * The name of the Transformer.
     *
     * @return name
     */
    public String getName();

    /**
     * Transforms a source object into a target object.
     *
     * @param message the source message.
     * @return the target Message
     */
    public T transform(final S message);
}
