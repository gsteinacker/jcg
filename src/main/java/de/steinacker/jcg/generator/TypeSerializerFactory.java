/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.model.Type;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class TypeSerializerFactory {

    public TypeSerializer getTypeSerializer(final Type type) {
        // TODO: Annotation-Name => Template-Name! Dafür einen TemplateResolver schreiben (.vm anhängen, Config, ...)
        return new VelocityTypeSerializer("/templates/type/class.vm");
    }

}
