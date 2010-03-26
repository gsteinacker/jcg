/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.visitor;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Field;
import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.model.Type;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface TypeVisitor {

    public void visit(final Type type, final Context context);

    public void visit(final Method method, final Context context);

    public void visit(final Field field, final Context context);

}
