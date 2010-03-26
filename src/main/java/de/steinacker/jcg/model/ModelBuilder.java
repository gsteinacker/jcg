/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ModelBuilder {
    private final Map<QualifiedName, Type> typeMap;

    public ModelBuilder() {
        typeMap = new HashMap<QualifiedName, Type>();
    }

    public ModelBuilder(final Model prototype) {
        typeMap = new HashMap<QualifiedName, Type>();
        for (final Type type : prototype.getAllTypes()) {
            typeMap.put(type.getName(), type);
        }
    }

    public ModelBuilder addType(final Type type) {
        typeMap.put(type.getName(), type);
        return this;
    }

    public Model toModel() {
        return new Model(typeMap.values());
    }
}
