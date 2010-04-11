/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.*;
import de.steinacker.jcg.test.AbstractJcgTest;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static de.steinacker.jcg.test.TypeAssert.assertHasMethod;
import static org.testng.Assert.*;


public class ParseInterfacesTest extends AbstractJcgTest {

    @Test
    public void testModel() {
        assertNotNull(getParsedModel().getType(QualifiedName.valueOf("test.interfaces.Kunde")));
        assertNotNull(getParsedModel().getType(QualifiedName.valueOf("test.interfaces.Person")));
        assertNotNull(getParsedModel().getType(QualifiedName.valueOf("test.interfaces.MutablePerson")));
    }

    @Test
    public void testKunde() {
        final Type customer = getParsedModel().getType(QualifiedName.valueOf("test.interfaces.Kunde"));
        assertTrue(customer.getKind().equals(Type.Kind.INTERFACE));
        assertHasMethod(customer, "public long getKundennummer()");
        assertEquals(customer.getMethods().size(), 1);
        assertTrue(customer.getNameOfInterfaces().contains(QualifiedName.valueOf("test.interfaces.Person")));
        assertEquals(customer.getNameOfSuperClass(), null);
        final Method method = customer.getMethods().get(0);
        assertNull(method.getMethodBody());
        assertEquals(method.getModifiers().size(), 1);
        assertTrue(method.getModifiers().contains(MethodModifier.PUBLIC));
    }
}