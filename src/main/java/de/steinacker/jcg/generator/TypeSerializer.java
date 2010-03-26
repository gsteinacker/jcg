/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Type;

import java.io.Writer;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface TypeSerializer {

    public Writer serializeType(Type type) throws JcgException;

    public void serializeType(Type type, Writer writer) throws JcgException;

}
