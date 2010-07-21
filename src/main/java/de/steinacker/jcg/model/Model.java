/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Model implements Iterable<Type> {

    @NotNull
    @Valid
    private final Map<QualifiedName, Type> model;

    public Model(final Collection<Type> types) {
        final Map<QualifiedName, Type> tempModel = new LinkedHashMap<QualifiedName, Type>(types.size());
        for (final Type type : types) {
            tempModel.put(type.getName(), type);
        }
        model = Collections.unmodifiableMap(tempModel);
    }

    public Type getType(final QualifiedName name) {
        return model.get(name);
    }

    public Collection<Type> getAllTypes() {
        return model.values();
    }

    @Override
    public Iterator<Type> iterator() {
        return model.values().iterator();
    }
}
