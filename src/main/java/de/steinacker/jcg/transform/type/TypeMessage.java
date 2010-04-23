/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.transform.Message;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An immutable Message implementation with <code>Type</code> payloads.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class TypeMessage implements Message<Type> {

    private final Context context;
    private final Type payload;

    /**
     * Creates a Message with payload and type.
     *
     * @param payload the Type payload.
     * @param context the Context.
     */
    public TypeMessage(final Type payload, final Context context) {
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
    public Type getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TypeMessage");
        sb.append("{payload=").append(payload);
        sb.append(", context=").append(context);
        sb.append('}');
        return sb.toString();
    }
}
