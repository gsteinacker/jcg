/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg;

import java.util.HashMap;
import java.util.Map;

/**
 * A Builder used to create Context instances.
 */
public final class ContextBuilder {
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public ContextBuilder() {
    }

    public ContextBuilder(final String key, final String value) {
        this.parameters.put(key, value);
    }

    public ContextBuilder(final Context context) {
        this.parameters.putAll(context.getParameters());
    }

    public ContextBuilder setParameters(final Map<String, Object> parameters) {
        this.parameters = new HashMap<String, Object>(parameters);
        return this;
    }

    public ContextBuilder mergeWith(final Context context) {
        this.parameters.putAll(context.getParameters());
        return this;
    }

    public ContextBuilder addParameter(final String key, final Object object) {
        this.parameters.put(key, object);
        return this;
    }

    public Context toContext() {
        return new Context(parameters);
    }
}