/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Annotation;
import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A ModelTransformer which adds a @Generated annotations to the type.
 * <p/>
 * This Transformer optionally supports a predicate to decide, whether a type or a field
 * shall become a @Generated annotation.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddGeneratedAnnotation extends SimpleTypeTransformer implements TypeTransformer {

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

    @Override
    protected TypeMessage doTransform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (!hasAnnotation(type, GENERATED_ANNOTATION_NAME)) {
            final Map<String, String> parameters = new LinkedHashMap<String, String>();
            parameters.put("value", "de.steinacker.jcg");
            parameters.put("date", new SimpleDateFormat("dd.MM.yyyy HH:MM").format(new Date()));
            final Type annotatedType = new TypeBuilder(type)
                    .addAnnotation(new Annotation(GENERATED_ANNOTATION_NAME, parameters))
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