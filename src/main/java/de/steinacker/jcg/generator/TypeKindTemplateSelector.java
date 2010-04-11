/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.generator;

import de.steinacker.jcg.model.Type;

public class TypeKindTemplateSelector implements TemplateSelector {

    @Override
    public String select(final Type type) {
        return "/templates/type/" + type.getKind().name().toLowerCase() + ".vm";
    }

}
