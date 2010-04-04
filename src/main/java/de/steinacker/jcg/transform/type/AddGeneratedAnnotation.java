/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.*;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A ModelTransformer which adds a @Generated annotations to the type.
 * <p/>
 * This Transformer optionally supports a predicate to decide, whether a type or a field
 * shall become a @Generated annotation.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddGeneratedAnnotation implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(AddGeneratedAnnotation.class);
    private final static QualifiedName GENERATED_ANNOTATION_NAME = QualifiedName.valueOf("javax.annotation.Generated");

    @Override
    public String getName() {
        return "AddGeneratedAnnotation";
    }

    private boolean hasAnnotation(final Type type, final QualifiedName name) {
        for (final Annotation a : type.getAnnotations()) {
            if (a.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This transformation adds a @javax.annotation.Generated annotation with value="de.steinacker.jcg" and date in
     * format "dd.MM.yyyy HH:MM" (see SimpleDateFormat). The comments parameter is not yet filled, so it is only
     * added as a default parameter with an empty String: comments="".
     * <p>
     * Future implementations might add another value or fill the comments.
     * @param message the source message containing the type.
     * @return a new TypeMessage, containing the original type with an additional @Generated annotation.
     */
    @Override
    public TypeMessage transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (!hasAnnotation(type, GENERATED_ANNOTATION_NAME)) {
            final List<AnnotationParameter> params = new ArrayList<AnnotationParameter>(2);
            // value is a String[]:
            final List<AnnotationValue> valueList
                    = Collections.singletonList(new AnnotationValue("de.steinacker.jcg", "\"de.steinacker.jcg\""));
            params.add(new AnnotationParameter("value", false, valueList));
            // date is a String
            final String dateString = new SimpleDateFormat("dd.MM.yyyy HH:MM").format(new Date());
            final AnnotationValue dateValue = new AnnotationValue(dateString, "\"" + dateString + "\"");
            params.add(new AnnotationParameter("date", false, dateValue));
            // currently, no comments are filled, so we are adding it as an empty String parameter:
            final List<AnnotationParameter> defaults = new ArrayList<AnnotationParameter>(3);
            defaults.addAll(params);
            defaults.add(new AnnotationParameter("comments", true, new AnnotationValue("", "\"\"")));
            // create the new Type from the old one and add the @Generated annotation:
            final Type annotatedType = new TypeBuilder(type)
                    .addAnnotation(new Annotation(GENERATED_ANNOTATION_NAME, params, defaults))
                    .toType();
            // return a new TypeMessage:
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