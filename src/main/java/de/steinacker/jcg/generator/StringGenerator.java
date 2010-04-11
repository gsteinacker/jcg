/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.Type;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A generator used to generate a Model into Strings.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class StringGenerator implements Generator {

    private TypeSerializer serializer;
    private final Map<QualifiedName, String> generatedCode = new LinkedHashMap<QualifiedName, String>();

    public void setSerializer(final TypeSerializer serializer) {
        this.serializer = serializer;
    }

    public Map<QualifiedName, String> getGeneratedCode() {
        return Collections.unmodifiableMap(generatedCode);
    }

    @Override
    public void generate(final Model model, final String targetDir) throws JcgException {
        for (final Type type : model.getAllTypes()) {
            final StringWriter writer = new StringWriter(2048);
            try {
                serializer.serializeType(type, writer);
                generatedCode.put(type.getName(), writer.toString());
            } finally {
                if (writer != null)
                    try {
                        writer.close();
                    } catch (IOException e) {
                        //ignore
                    }
            }
        }
    }

}