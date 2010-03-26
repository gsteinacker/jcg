/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import java.io.Writer;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Target {

    public void writeToTarget(final Writer writer);

}
