/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Type;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface TypeSerializer {

    public void serializeType(Type type, Appendable writer) throws JcgException;

}
