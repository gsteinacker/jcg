/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import java.util.List;

/**
 * All model objects which might have {@link Annotation}s are implementing this interface.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Annotatable {

    /**
     * Returns the list of annotations of this Type/Method/Field/...
     *
     * @return list of annotations
     */
    public List<Annotation> getAnnotations();
}
