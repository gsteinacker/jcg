/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.model;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.transform.Message;

/**
 * An immutable Message implementation with <code>Model</code> payloads.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class ModelMessage implements Message<Model> {

    private final Context context;
    private final Model payload;

    /**
     * Creates a Message with payload and context.
     *
     * @param payload the Model payload.
     * @param context the Context.
     */
    public ModelMessage(final Model payload, final Context context) {
        this.context = context;
        this.payload = payload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model getPayload() {
        return payload;
    }
}