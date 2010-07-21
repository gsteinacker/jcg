/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.Type;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ConsoleGenerator implements Generator {

    private TypeSerializer serializer;

    public void setSerializer(final TypeSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void generate(final Model model, final String targetDir) throws JcgException {
        for (final Type type : model.getAllTypes()) {
            serializer.serializeType(type, System.out);
        }
    }
}
