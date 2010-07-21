/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.test.AbstractJcgTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public final class ParseClassesTest extends AbstractJcgTest {

    private static final QualifiedName QN_CLASS01 = QualifiedName.valueOf("test.types.Class01");
    private static final QualifiedName QN_CLASS02 = QualifiedName.valueOf("test.types.Class02");
    private static final QualifiedName QN_CLASS03 = QualifiedName.valueOf("test.types.Class03");
    private static final QualifiedName QN_ENUMTYPE01 = QualifiedName.valueOf("test.types.EnumType01");
    private static final QualifiedName QN_ENUMTYPE02 = QualifiedName.valueOf("test.types.EnumType02");

    @Test
    public void testExistenceOfClasses() {
        final Model model = getParsedModel();
        assertNotNull(model.getType(QN_CLASS01));
        assertNotNull(model.getType(QN_CLASS02));
    }

    public void testExistenceOfEnums() {
        final Model model = getParsedModel();
        assertNotNull(model.getType(QN_ENUMTYPE01));
        assertNotNull(model.getType(QN_ENUMTYPE02));
    }

    @Test
    public void theMostSimpleClass() {
        final Type type = getParsedModel().getType(QN_CLASS01);
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
    }

    @Test
    public void classWithInheritance() {
        final Model model = getParsedModel();
    }

    @Test
    public void nestedTypes() {
        final Model model = getParsedModel();
    }

    @Test
    public void simpleEnum() {
        final Type type = getParsedModel().getType(QN_ENUMTYPE01);
    }

    @Test
    public void enumWithMethodsAndFields() {
        final Type type = getParsedModel().getType(QN_ENUMTYPE02);
    }

    /*
    public void testGenericInterface01() {
        final Type type = getParsedModel().getType(QN_GENERICINTERFACE01);
        // test for the one and only PUBLIC modifier:
        assertEquals(type.getModifiers().size(), 1);
        assertTrue(type.is(TypeModifier.PUBLIC));
        // test annotations:
        assertEquals(type.getAnnotations().size(), 0);
        // test for the kind of type:
        assertEquals(type.getKind(), Type.Kind.INTERFACE);
        // test the extended interfaces and superclass:
        assertEquals(type.getImplementedInterfaces().size(), 1);
        assertEquals(type.getImplementedInterfaces().get(0).getQualifiedName(), QualifiedName.valueOf("java.io.Serializable"));
        assertNull(type.getSuperClass());
        // test the name and toString() output:
        assertEquals(type.getName(), QN_GENERICINTERFACE01);
        assertEquals(type.getName().getSimpleName().toString(), "GenericInterface01");
        assertEquals(type.toString(), "public interface GenericInterface01<T>");
        // test the type parameters and bounds:
        assertEquals(type.getTypeParameters().size(), 1);
        assertEquals(type.getTypeParameters().get(0).getParamName().toString(), "T");
        assertTrue(type.getTypeParameters().get(0).getBoundedTypes().isEmpty());
        // test the imports:
        final List<QualifiedName> imports = type.getImports();
        assertEquals(imports.size(), 2);
        assertTrue(imports.contains(QualifiedName.valueOf("java.util.List")));
        assertTrue(imports.contains(QualifiedName.valueOf("java.io.Serializable")));
        // test the single asList method:
        assertEquals(type.getMethods().size(), 1);
        final Method asList = type.getMethods().get(0);
        final TypeParameter tp = asList.getReturnType().getTypeParameters().get(0);
        assertEquals(tp.getParamName().toString(), "T");
        assertEquals(asList.getName().toString(), "asList");
        assertEquals(asList.toString(), "public List<T> asList(final T t)");
        assertEquals(asList.getReturnType().getQualifiedName().toString(), "java.util.List");
        assertEquals(asList.getReturnType().getTypeParameters().size(), 1);
        assertEquals(asList.getParameters().size(), 1);
        Parameter p = asList.getParameters().get(0);
        assertEquals(p.getName().toString(), "t");
        assertEquals(p.getType().toString(), "T");
    }
    */
    
    @Override
    protected String selectTransformer() {
        return "RemoveSingleEmptyDefaultConstructor";
    }

    @Override
    protected String getTestSources() {
        return "src/test/java/test/types";
    }

}