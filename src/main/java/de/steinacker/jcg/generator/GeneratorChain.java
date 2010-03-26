/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class GeneratorChain implements Generator {
    private final List<Generator> chain;

    public GeneratorChain(final List<? extends Generator> chain) {
        this.chain = new ArrayList<Generator>(chain);
    }

    @Override
    public void generate(final Model model, final String targetDir) throws JcgException {
        for (final Generator generator : chain) {
            generator.generate(model, targetDir);
        }
    }
}
