/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.test;

import de.steinacker.jcg.model.*;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static de.steinacker.jcg.test.TypeAssert.assertHasMethod;
import static org.testng.Assert.*;


public class TransformInterfacesTest extends AbstractJcgTest {

    @Test
    public void testModel() {
        final Model model = getTransformedModel();
        assertNotNull(model.getType(QualifiedName.valueOf("test.interfaces.Customer")));
        assertNotNull(model.getType(QualifiedName.valueOf("test.interfaces.Person")));
        assertNotNull(model.getType(QualifiedName.valueOf("test.interfaces.MutablePerson")));
    }

    @Test
    public void testKunde() {
        final Model model = getTransformedModel();
        final Type customer = model.getType(QualifiedName.valueOf("test.interfaces.Customer"));
        assertTrue(customer.getKind().equals(Type.Kind.INTERFACE));
        assertHasMethod(customer, "public long getCustomerNumber()");
        assertEquals(customer.getMethods().size(), 1);
        assertTrue(customer.getNameOfInterfaces().contains(QualifiedName.valueOf("test.interfaces.Person")));
        assertEquals(customer.getNameOfSuperClass(), null);
        assertTrue(customer.getModifiers().contains(TypeModifier.PUBLIC));
        assertFalse(customer.getModifiers().contains(TypeModifier.ABSTRACT));
        assertFalse(customer.getModifiers().contains(TypeModifier.FINAL));
        final Method method = customer.getMethods().get(0);
        assertNull(method.getMethodBody());
        assertEquals(method.getModifiers().size(), 1);
        assertTrue(method.getModifiers().contains(MethodModifier.PUBLIC));
        assertFalse(method.getModifiers().contains(MethodModifier.ABSTRACT));
        assertFalse(method.getModifiers().contains(MethodModifier.FINAL));
    }
}