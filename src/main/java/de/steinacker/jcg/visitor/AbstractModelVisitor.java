/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.visitor;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.Type;

/**
 * An abstract visitor-implementation which is doing nothing.
 * This class is only used for convenience purposes. It might be
 * extended if only few methods of the ModelVisitor interface
 * must be implemented.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public abstract class AbstractModelVisitor extends AbstractTypeVisitor implements ModelVisitor {

    @Override
    public void visit(final Model model, final Context context) {
        for (final Type type : model.getAllTypes()) {
            visit(type, context);
        }
    }

}
