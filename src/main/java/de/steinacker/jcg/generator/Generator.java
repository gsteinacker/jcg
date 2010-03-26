/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Model;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Generator {

    public void generate(final Model model, final String targetDir) throws JcgException;

}
