/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.*;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A TypeTransformer which adds a @Generated annotation to the type.
 * <p/>
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddGeneratedAnnotation implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(AddGeneratedAnnotation.class);
    private final static QualifiedName GENERATED_ANNOTATION_NAME = QualifiedName.valueOf("javax.annotation.Generated");

    private String comments = "";
    private String dateFormat = "dd.MM.yyyy HH:MM";
    private String name = "AddGeneratedAnnotation";

    /** Optionally injects the name of this transformer.
     * The default name is 'AddGeneratedAnnotation'.
     * @param name the name of this transformer instance.
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /** Optionally provide some comments that are generated into the 'comments' parameter
     * of the @Generated anntotation. By default, this parameter is empty, so no
     * comments parameter is generated.
     * @param comments String
     */
    public void setComments(final String comments) {
        this.comments = comments;
    }

    /** Optionally provide a format string that is used to generate the 'date'  parameter.
     * The default dateFormat is <code>dd.MM.yyyy HH:MM</code>
     * @see <a href="http://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html">java.text.SimpleDateFormat</a>
     * @param dateFormat format string
     */
    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String getName() {
        return name;
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
    public List<TypeMessage> transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (!hasAnnotation(type, GENERATED_ANNOTATION_NAME)) {
            final List<AnnotationParameter> params = new ArrayList<AnnotationParameter>(2);
            // value is a String[]:
            final List<AnnotationValue> valueList
                    = Collections.singletonList(new AnnotationValue("de.steinacker.jcg", "\"de.steinacker.jcg\""));
            params.add(new AnnotationParameter("value", false, valueList));
            // date is a String
            final String dateString = new SimpleDateFormat(dateFormat).format(new Date());
            final AnnotationValue dateValue = new AnnotationValue(dateString, "\"" + dateString + "\"");
            params.add(new AnnotationParameter("date", false, dateValue));
            // currently, no comments are filled, so we are adding it as an empty String parameter:
            final List<AnnotationParameter> defaults = new ArrayList<AnnotationParameter>(3);
            defaults.addAll(params);
            defaults.add(new AnnotationParameter("comments", true, new AnnotationValue(comments, "\"" + comments + "\"")));
            // create the new Type from the old one and add the @Generated annotation:
            final Type annotatedType = new TypeBuilder(type)
                    .addAnnotation(new Annotation(GENERATED_ANNOTATION_NAME, params, defaults))
                    .toType();
            // return a new TypeMessage:
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