/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.visitor;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.*;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ConsoleModelVisitor extends AbstractModelVisitor implements ModelVisitor {

    @Override
    public void visit(final Type type, final Context context) {
        System.out.println("Type: " + type.getName());
        System.out.println("\textends: " + type.getNameOfSuperClass());
        System.out.print("\timplements: ");
        for (final QualifiedName qualifiedName : type.getNameOfInterfaces()) {
            System.out.print(qualifiedName.toString() + " ");
        }
        System.out.println("");
        System.out.println("\tannotations: ");
        for (final Annotation annotation : type.getAnnotations()) {
            System.out.print(annotation.getName().toString() + " ");
        }
        System.out.println("");
        super.visit(type, context);
    }

    @Override
    public void visit(final Method method, final Context context) {
        System.out.println("\tMethod: " + method.toString());
        super.visit(method, context);
    }

    @Override
    public void visit(final Field field, final Context context) {
        System.out.println("\tField: " + field.toString());
        super.visit(field, context);
    }
}
