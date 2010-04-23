/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import de.steinacker.jcg.util.TransformerUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<TypeMessage> transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (TransformerUtil.hasSingleEmptyDefaultConstructor(type)) {
            final List<Method> methods = new ArrayList<Method>();
            for (final Method method : type.getMethods()) {
                if (!method.isConstructor())
                    methods.add(method);
            }
            final Type transformedType = new TypeBuilder(type).setMethods(methods).toType();
            return Collections.singletonList(new TypeMessage(transformedType, message.getContext()));
        } else {
            return Collections.singletonList(message);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}