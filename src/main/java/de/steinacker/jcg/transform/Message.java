/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform;

import de.steinacker.jcg.Context;

/**
 * A Message used to parameterize different transformers and generators.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Message<T> {

    /**
     * Returns the Context of the Message.
     *
     * @return Context
     */
    public Context getContext();

    /**
     * Returns the payload of the Message.
     *
     * @return payload of the message
     */
    public T getPayload();
}
