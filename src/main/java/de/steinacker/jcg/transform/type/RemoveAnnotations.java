/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Annotation;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A TypeTransformer which removes annotations from a Type.
 * The annotations are specified as a list of fully qualified class names ({@link #setClassNames})
 * and/or a list of package names ({@link #setPackageNames}).
 * <p/>
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class RemoveAnnotations implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(RemoveAnnotations.class);
    private String name;
    private List<String> classNames;
    private List<String> packageNames;

    @Required
    public void setName(String name) {
        this.name = name;
    }

    @Required
    public void setClassNames(List<String> classNames) {
        this.classNames = classNames;
    }

    @Required
    public void setPackageNames(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns true if the specified annotation should be removed from the type.
     *
     * @param annotation the Annotation
     * @return boolean
     */
    private boolean removeAnnotation(final Annotation annotation) {
        final String annotationName = annotation.getName().toString();
        for (final String className : this.classNames) {
            if (className.equals(annotationName))
                return true;
        }
        for (final String packageName : packageNames) {
            if (annotationName.startsWith(packageName))
                return true;
        }
        return false;
    }

    @Override
    public List<TypeMessage> transform(final TypeMessage message) {
        final Type type = message.getPayload();
        final List<Annotation> annotations = new ArrayList<Annotation>();
        boolean removedAnnotation = false;
        for (final Annotation annotation : type.getAnnotations()) {
            if (!removeAnnotation(annotation))
                annotations.add(annotation);
            else
                removedAnnotation = true;
        }
        if (removedAnnotation) {
            final Type annotatedType = new TypeBuilder(type)
                    .setAnnotations(annotations)
                    .toType();
            return Collections.singletonList(new TypeMessage(annotatedType, message.getContext()));
        } else {
            return Collections.singletonList(message);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}