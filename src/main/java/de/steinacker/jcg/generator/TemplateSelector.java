/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.generator;

import de.steinacker.jcg.model.Type;

/**
 * Selects templates for a Type.
 *
 */
public interface TemplateSelector {

    /**
     * Selects a template for a type and returns the template's name.
     *
     * @param type the Type
     * @return name of the template
     */
    public String select(Type type);

}
