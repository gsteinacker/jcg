/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Annotation;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A ModelTransformer which adds a @Generated annotations to the type.
 * <p/>
 * This Transformer optionally supports a predicate to decide, whether a type or a field
 * shall become a @Generated annotation.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class RemoveJcgAnnotations implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(RemoveJcgAnnotations.class);
    private final static String ANNOTATION_PREFIX = "de.steinacker.jcg.annotation";

    @Override
    public String getName() {
        return "RemoveJcgAnnotations";
    }

    private boolean hasAnnotationWithPrefix(final Type type, final String prefix) {
        for (final Annotation a : type.getAnnotations()) {
            if (a.getName().toString().startsWith(prefix))
                return true;
        }
        return false;
    }

    @Override
    public TypeMessage transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (hasAnnotationWithPrefix(type, ANNOTATION_PREFIX)) {
            final List<Annotation> annotations = new ArrayList<Annotation>();
            for (final Annotation annotation : type.getAnnotations()) {
                if (!annotation.getName().toString().startsWith(ANNOTATION_PREFIX))
                    annotations.add(annotation);
            }
            final Type annotatedType = new TypeBuilder(type)
                    .setAnnotations(annotations)
                    .toType();
            return new TypeMessage(annotatedType, message.getContext());
        } else {
            return message;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}