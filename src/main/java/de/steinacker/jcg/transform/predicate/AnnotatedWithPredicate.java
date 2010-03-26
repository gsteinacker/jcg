/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.predicate;

import de.steinacker.jcg.model.Annotatable;
import de.steinacker.jcg.model.Annotation;
import de.steinacker.jcg.model.QualifiedName;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * This predicate returns true if the evaluated object is annotated with a given Annotation.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AnnotatedWithPredicate implements Predicate<Annotatable> {

    @NotNull
    @Valid
    private final QualifiedName annotationName;

    public AnnotatedWithPredicate(final QualifiedName annotationName) {
        this.annotationName = annotationName;
    }

    @Override
    public boolean eval(final Annotatable param) {
        for (final Annotation annotation : param.getAnnotations()) {
            if (annotation.getName().equals(annotationName))
                return true;
        }
        return false;
    }

}
