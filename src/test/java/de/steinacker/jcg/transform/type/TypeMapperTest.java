/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.test.AbstractJcgTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;


public final class TypeMapperTest extends AbstractJcgTest {

    private static final QualifiedName QN_CLASS01 = QualifiedName.valueOf("de.steinacker.jcg.test.types.Class01");
    @Test
    public void theMostSimpleClass() {
        final Type type = getTransformedModel().getType(QN_CLASS01);
        assertNotNull(type);
        /*
        // test for the one and only PUBLIC modifier:
        assertEquals(type.getModifiers().size(), 0);
        // test annotations:
        assertEquals(type.getAnnotations().size(), 0);
        // test for the kind of type:
        assertEquals(type.getKind(), Type.Kind.CLASS);
        // test the extended interfaces and superclass:
        assertEquals(type.getImplementedInterfaces().size(), 0);
        assertNotNull(type.getSuperClass());
        assertEquals(type.getSuperClass().getQualifiedName().toString(), "java.lang.Object");
        // test the name and toString() output:
        assertEquals(type.getName(), QN_CLASS01);
        assertEquals(type.getName().getSimpleName().toString(), "Class01");
        assertEquals(type.getName().getPackage(), "test.types");
        assertEquals(type.toString(), "class Class01");
        // test the type parameters and bounds:
        assertEquals(type.getTypeParameters().size(), 0);
        // test the imports:
        assertEquals(type.getImports().size(), 0);
        // test the number of methods:
        // After parsing and before removing the single empty constructor:
        assertEquals(type.getMethods().size(), 1);
        assertEquals(type.getMethods().get(0).getParameters().size(), 0);
        assertTrue(type.getMethods().get(0).getMethodBody().isEmpty());
        */
    }

    @Override
    protected String selectTransformer() {
        return "TypeMapper";
    }

    @Override
    protected String getTestSources() {
        return "src/test/java/test/types";
    }

}