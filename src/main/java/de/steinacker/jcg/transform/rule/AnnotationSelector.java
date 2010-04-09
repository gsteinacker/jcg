/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.rule;

import de.steinacker.jcg.model.Annotation;
import de.steinacker.jcg.model.AnnotationParameter;
import de.steinacker.jcg.model.AnnotationValue;
import de.steinacker.jcg.transform.type.TypeMessage;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This rule returns true if the evaluated object is annotated with a given Annotation.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AnnotationSelector implements TypeTransformerSelector {

    private List<CharSequence> annotationNames;
    private List<String> defaultValue = Collections.emptyList();

    @Required
    public void setAnnotationNames(final List<CharSequence> annotationNames) {
        this.annotationNames = new ArrayList<CharSequence>(annotationNames.size());
        for (final CharSequence key : annotationNames) {
            this.annotationNames.add(key.toString());
        }
    }

    public void setDefaultValue(final List<String> defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<String> apply(final TypeMessage param) {
        for (final Annotation annotation : param.getPayload().getAnnotations()) {
            if (annotationNames.contains(annotation.getName().toString())) {
                final AnnotationParameter result;
                if (annotation.getParameter("transformWith", true) != null)
                    result = annotation.getParameter("transformWith", true);
                else if (annotation.getName().toString().equals("de.steinacker.jcg.annotation.TransformWith"))
                    result = annotation.getParameter("value", true);
                else
                    result = null;
                if (result != null) {
                    final List<AnnotationValue> values = result.getValues();
                    final List<String> selection = new ArrayList<String>(values.size());
                    for (AnnotationValue annotationValue : values) {
                        selection.add(annotationValue.getValue().toString());
                    }
                    return selection;
                }
            }
        }
        return defaultValue;
    }

}
