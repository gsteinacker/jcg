/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A Transformer that removes default constructors possibly added by the compiler.
 * The default constructor is removed, if it is the only one, and if the methodBody
 * only contains a call to super();
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class RemoveSingleEmptyDefaultConstructor implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(RemoveSingleEmptyDefaultConstructor.class);

    @Override
    public String getName() {
        return "RemoveSingleEmptyDefaultConstructor";
    }

    @Override
    public TypeMessage transform(final TypeMessage message) {
        final Type type = message.getPayload();
        boolean singleEmptyDefaultConstructor = false;
        int count = 0;
        for (final Method method : type.getMethods()) {
            if (method.isConstructor()) {
                ++count;
                // more than one constructor
                if (count > 1)
                    return message;
                else {
                    if (method.getParameters().size() == 0 && method.getMethodBody().trim().equals("super();"))
                        singleEmptyDefaultConstructor = true;
                }
            }
        }
        if (singleEmptyDefaultConstructor) {
            final List<Method> methods = new ArrayList<Method>();
            for (final Method method : type.getMethods()) {
                if (!method.isConstructor())
                    methods.add(method);
            }
            final Type transformedType = new TypeBuilder(type).setMethods(methods).toType();
            return new TypeMessage(transformedType, message.getContext());
        } else {
            return message;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}