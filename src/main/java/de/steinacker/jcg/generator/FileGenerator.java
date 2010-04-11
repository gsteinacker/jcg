/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.Type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class FileGenerator implements Generator {

    private TypeSerializer serializer;

    public void setSerializer(final TypeSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void generate(final Model model, final String targetDir) throws JcgException {
        for (final Type type : model.getAllTypes()) {
            final String packagePath = getPathForType(targetDir, type);
            final File dir = new File(packagePath);
            if (!dir.exists()) {
                if (!dir.mkdirs())
                    throw new IllegalArgumentException("Unable to create targetDir: " + targetDir);
            }
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("Directory " + packagePath + " is not a directory!");
            }
            final String fileName = new StringBuilder()
                    .append(packagePath)
                    .append("/")
                    .append(type.getName().getSimpleName().toString())
                    .append(".java").toString();
            Writer writer = null;
            try {
                writer = new FileWriter(fileName);
                serializer.serializeType(type, writer);
            } catch (IOException e) {
                throw new IllegalStateException("Can not write file " + fileName + ": " + e.getMessage(), e);
            } finally {
                if (writer != null)
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        //ignore
                    }
            }
        }
    }

    private String getPathForType(final String targetDir, final Type type) {
        final String relativePath = type.getName().getPackage().replaceAll("\\.", "/");
        return targetDir + "/" + relativePath;
    }
}