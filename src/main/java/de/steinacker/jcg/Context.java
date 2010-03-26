/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Context {
    @NotNull
    private final Map<String, Object> parameters;

    /**
     * Creates a new Context instance
     *
     * @param parameters a Map containing all context parameters.
     */
    public Context(final Map<String, Object> parameters) {
        this.parameters = new HashMap<String, Object>(parameters);
    }

    public <T> T getParameter(final String key, Class<T> valueType) {
        return valueType.cast(parameters.get(key));
    }

    /**
     * Returns an immutable Map, containing all context parameters.
     *
     * @return parameters
     */
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Context that = (Context) o;

        if (!parameters.equals(that.parameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return parameters.hashCode();
    }
}
