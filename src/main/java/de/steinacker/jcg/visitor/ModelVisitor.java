/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.visitor;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Model;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface ModelVisitor extends TypeVisitor {

    public void visit(final Model model, final Context context);

}
