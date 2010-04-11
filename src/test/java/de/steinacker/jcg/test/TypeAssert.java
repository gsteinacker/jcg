/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.test;

import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.model.Type;

import static org.testng.Assert.fail;

public class TypeAssert {

    private TypeAssert() {
    }

    public static void assertHasMethod(final Type type, final Method method) {
        for (final Method m : type.getMethods()) {
            if (m.toString().equals(method.toString()))
                return;
        }
        fail();
    }

    public static void assertHasMethod(final Type type, final String methodSignature) {
        for (final Method m : type.getMethods()) {
            if (m.toString().equals(methodSignature))
                return;
        }
        fail();
    }
}
