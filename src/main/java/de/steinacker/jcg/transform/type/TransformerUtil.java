/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.model.Parameter;
import de.steinacker.jcg.model.Type;

import java.util.List;

/**
 * Utility class providing some helper methods used to implement {@link de.steinacker.jcg.transform.Transformer
 * transformers}.
 */
final class TransformerUtil {

    private TransformerUtil() {
    }

    public static boolean hasMethodWithSignature(final Type type, final Method method) {
        for (final Method existingMethod : type.getMethods()) {
            if (!existingMethod.getName().equals(method.getName()))
                continue;
            final List<Parameter> existingMethodParameters = existingMethod.getParameters();
            final List<Parameter> methodParameters = method.getParameters();
            if (existingMethodParameters.size() != methodParameters.size())
                continue;
            boolean differentParams = false;
            for (int i=0, n= existingMethodParameters.size(); i<n; ++i) {
                final Parameter p1 = existingMethodParameters.get(i);
                final Parameter p2 = methodParameters.get(i);
                if (!p1.getType().equals(p2.getType()))
                    differentParams = true;
            }
            if (!differentParams)
                return true;
        }
        return false;
    }

    /**
     * Returns true if the type has one or more constructors.
     *
     * @param type the Type
     * @return true if the type has one or more constructors.
     */
    public static boolean hasConstructors(final Type type) {
        for (final Method method : type.getMethods()) {
            if (method.isConstructor())
                return true;
        }
        return false;
    }

    /**
     * Returns true if the specified <code>type</code> has only a single default-constructor without any code,
     * optionally containing a single call to super().
     * <p>
     * If <code>type</code> has no constructors (that is, the default constructor is only implicitly present,
     * not explicitly contained in {@link Type#getMethods()}), this method returns <code>false</code>.
     * @param type the Type instance.
     * @return true, if <code>type</code> explicitly defines an empty default constructor.
     */
    public static boolean hasSingleEmptyDefaultConstructor(final Type type) {
        boolean singleEmptyDefaultConstructor = false;
        if (type.getKind() != Type.Kind.INTERFACE) {
            int count = 0;
            for (final Method method : type.getMethods()) {
                if (method.isConstructor()) {
                    ++count;
                    // more than one constructor
                    if (count > 1) {
                        singleEmptyDefaultConstructor = false;
                        break;
                    } else {
                        final String body = method.getMethodBody().trim();
                        if (method.getParameters().size() == 0 && (body.equals("super();") || body.isEmpty())) {
                            singleEmptyDefaultConstructor = true;
                        }
                    }
                }
            }
        }
        return singleEmptyDefaultConstructor;
    }
}
