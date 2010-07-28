/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.*;
import de.steinacker.jcg.test.AbstractJcgTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;


public final class ParseGenericsTest extends AbstractJcgTest {
    
    private static final QualifiedName QN_GENERICINTERFACE01 = QualifiedName.valueOf("test.generics.GenericInterface01");
    private static final QualifiedName QN_GENERICINTERFACE02 = QualifiedName.valueOf("test.generics.GenericInterface02");
    private static final QualifiedName QN_GENERICINTERFACE03 = QualifiedName.valueOf("test.generics.GenericInterface03");
    private static final QualifiedName QN_GENERICTYPE01 = QualifiedName.valueOf("test.generics.GenericType01");
    private static final QualifiedName QN_GENERICTYPE02 = QualifiedName.valueOf("test.generics.GenericType02");

    @Test
    public void testModel() {
        final Model model = getParsedModel();
        assertNotNull(model.getType(QN_GENERICTYPE01));
    }

    /**
     * Imports must not contain the generic types like T, Comparable<T>, and so on.
     */
    @Test
    public void noGenericTypeSymbolsInImports() {
        final Model model = getParsedModel();
        for (final Type type : model.getAllTypes()) {
            for (final Import imp : type.getImports()) {
                final QualifiedName qn = imp.getQualifiedName();
                assertFalse(qn.toString().equals("T"));
                assertFalse(qn.toString().contains("<T>"));
                assertFalse(qn.toString().equals("S"));
                assertFalse(qn.toString().contains("<S>"));
            }
        }
    }

    /**
     * The QN of a Type must not contain the generic parameters like <T>
     */
    @Test
    public void typeNamesAreValid() {
        final Model model = getParsedModel();
        for (final Type type : model.getAllTypes()) {
            assertTrue(type.getName().toString().matches("[[a-z0-9_]+\\.]*[A-Z][a-zA-Z0-9_]*"));
        }
    }

    /**
     * The QN of a Type must not contain the generic parameters like <T>
     */
    @Test
    public void methodNamesAreValid() {
        final Model model = getParsedModel();
        for (final Type type : model.getAllTypes()) {
            for (final Method method : type.getMethods()) {
                if (method.isConstructor())
                    assertTrue(method.getName().toString().matches("[A-Z][a-zA-Z0-9_]*"));
                else
                    assertTrue(method.getName().toString().matches("[a-z][a-zA-Z0-9_]*"));    
            }

        }
    }

    @Test
    public void testGenericInterface01() {
        final Type type = getParsedModel().getType(QN_GENERICINTERFACE01);
        assertNotNull(type);
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
        final List<Import> imports = type.getImports();
        assertEquals(imports.size(), 2);
        assertTrue(imports.contains(new Import(QualifiedName.valueOf("java.util.List"))));
        assertTrue(imports.contains(new Import(QualifiedName.valueOf("java.io.Serializable"))));
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

    @Test
    public void testGenericInterface02() {
        final Type type = getParsedModel().getType(QN_GENERICINTERFACE02);
        assertNotNull(type);
        // test for the one and only PUBLIC modifier:
        assertEquals(type.getModifiers().size(), 1);
        assertTrue(type.is(TypeModifier.PUBLIC));
        // test annotations:
        assertEquals(type.getAnnotations().size(), 0);
        // test for the kind of type:
        assertEquals(type.getKind(), Type.Kind.INTERFACE);
        // test the extended interfaces and superclass:
        assertEquals(type.getImplementedInterfaces().size(), 1);
        assertEquals(type.getImplementedInterfaces().get(0).getQualifiedName(), QN_GENERICINTERFACE01);
        assertNull(type.getSuperClass());
        // test the name and toString() output:
        assertEquals(type.getName(), QN_GENERICINTERFACE02);
        assertEquals(type.getName().getSimpleName().toString(), "GenericInterface02");
        assertEquals(type.toString(), "public interface GenericInterface02<T>");
        // test the type parameters and bounds:
        assertEquals(type.getTypeParameters().size(), 1);
        assertEquals(type.getTypeParameters().get(0).getParamName().toString(), "T");
        assertTrue(type.getTypeParameters().get(0).getBoundedTypes().isEmpty());
        // test the imports:
        final List<Import> imports = type.getImports();
        assertEquals(imports.size(), 0);
        // test the single asList method:
        assertEquals(type.getMethods().size(), 0);
    }

    @Test
    public void testGenericInterface03() {
        final Type type = getParsedModel().getType(QN_GENERICINTERFACE03);
        assertNotNull(type);
        // test for the one and only PUBLIC modifier:
        assertEquals(type.getModifiers().size(), 1);
        assertTrue(type.is(TypeModifier.PUBLIC));
        // test annotations:
        assertEquals(type.getAnnotations().size(), 0);
        // test for the kind of type:
        assertEquals(type.getKind(), Type.Kind.INTERFACE);
        // test the extended interfaces and superclass:
        assertEquals(type.getImplementedInterfaces().size(), 1);
        final TypeSymbol implementedInterface = type.getImplementedInterfaces().get(0);
        assertEquals(implementedInterface.getQualifiedName(), QN_GENERICINTERFACE01);
        // test the bounds of the implemented interface:
        assertTrue(implementedInterface.isParameterized());
        assertEquals(implementedInterface.getTypeParameters().size(), 1);
        assertEquals(implementedInterface.getTypeParameters().get(0).getParamName().toString(), "java.lang.Double");
        assertEquals(implementedInterface.getTypeParameters().get(0).getBoundedTypes().size(), 0);

        assertNull(type.getSuperClass());
        // test the name and toString() output:
        assertEquals(type.getName(), QN_GENERICINTERFACE03);
        assertEquals(type.getName().getSimpleName().toString(), "GenericInterface03");
        assertEquals(type.toString(), "public interface GenericInterface03");
        // test the type parameters and bounds:
        assertEquals(type.getTypeParameters().size(), 0);
        // test the imports:
        final List<Import> imports = type.getImports();
        assertEquals(imports.size(), 0);
        // test the number of methods:
        assertEquals(type.getMethods().size(), 0);
    }

    @Test
    public void  testGenericType01() {
        final Type type = getParsedModel().getType(QN_GENERICTYPE01);
        assertNotNull(type);
        // test for the one and only PUBLIC modifier:
        assertEquals(type.getModifiers().size(), 1);
        assertTrue(type.is(TypeModifier.PUBLIC));
        // test annotations:
        assertEquals(type.getAnnotations().size(), 0);
        // test for the kind of type:
        assertEquals(type.getKind(), Type.Kind.CLASS);
        // test the extended interfaces and superclass:
        assertEquals(type.getImplementedInterfaces().size(), 0);
        assertNotNull(type.getSuperClass());
        assertEquals(type.getSuperClass().getQualifiedName().toString(), "java.lang.Object");
        // test the name and toString() output:
        assertEquals(type.getName(), QN_GENERICTYPE01);
        assertEquals(type.getName().getSimpleName().toString(), "GenericType01");
        assertEquals(type.toString(), "public class GenericType01<T>");
        // test the type parameters and bounds:
        assertEquals(type.getTypeParameters().size(), 1);
        final TypeParameter typeParameter = type.getTypeParameters().get(0);
        final QualifiedName paramName = typeParameter.getParamName();
        assertEquals(paramName.toString(), "T");
        assertEquals(paramName.isPrimitive(), false);
        assertEquals(paramName.isWildcard(), false);
        assertEquals(paramName.isTypeVariable(), true);
        assertEquals(typeParameter.getBoundedTypes().size(), 0);
        // test the imports:
        final List<Import> imports = type.getImports();
        assertEquals(imports.size(), 2);
        assertTrue(imports.contains(new Import(QualifiedName.valueOf("java.util.List"))));
        assertTrue(imports.contains(new Import(QualifiedName.valueOf("java.util.Collections"))));
        // test the number of methods is 3 because the compiler-generated empty default constructor is not yet removed:
        assertEquals(type.getMethods().size(), 3);
    }

    @Test
    public void  testGenericType02() {
        final Type type = getParsedModel().getType(QN_GENERICTYPE02);
        assertNotNull(type);
        // test for the one and only PUBLIC modifier:
        assertEquals(type.getModifiers().size(), 2);
        assertTrue(type.is(TypeModifier.PUBLIC));
        assertTrue(type.is(TypeModifier.FINAL));
        // test annotations:
        assertEquals(type.getAnnotations().size(), 0);
        // test for the kind of type:
        assertEquals(type.getKind(), Type.Kind.CLASS);
        // test the extended interfaces and superclass:
        assertEquals(type.getImplementedInterfaces().size(), 0);
        assertNotNull(type.getSuperClass());
        assertEquals(type.getSuperClass().getQualifiedName().toString(), "java.lang.Object");
        // test the name and toString() output:
        assertEquals(type.getName(), QN_GENERICTYPE02);
        // test the type parameters and bounds:
        // Das S kommt aus dem <S> des Konstruktors!
        assertEquals(type.toString(), "public final class GenericType02<T extends Number, S>");
        assertEquals(type.getTypeParameters().size(), 2);
        final TypeParameter typeParameterT = type.getTypeParameters().get(0);
        assertEquals(typeParameterT.toString(), "T extends Number");
        final QualifiedName paramNameT = typeParameterT.getParamName();
        assertEquals(paramNameT.toString(), "T");
        assertEquals(paramNameT.isPrimitive(), false);
        assertEquals(paramNameT.isWildcard(), false);
        assertEquals(paramNameT.isTypeVariable(), true);
        assertEquals(typeParameterT.getBoundedTypes().size(), 1);
        assertEquals(typeParameterT.getBoundedTypes().get(0).toString(), "java.lang.Number");
        final TypeParameter typeParameterS = type.getTypeParameters().get(1);
        assertEquals(typeParameterS.toString(), "S");
        final QualifiedName paramNameS = typeParameterS.getParamName();
        assertEquals(paramNameS.toString(), "S");
        assertEquals(paramNameS.isPrimitive(), false);
        assertEquals(paramNameS.isWildcard(), false);
        assertEquals(paramNameS.isTypeVariable(), true);
        assertEquals(typeParameterS.getBoundedTypes().size(), 0);
        // test the imports:
        final List<Import> imports = type.getImports();
        assertEquals(imports.size(), 0);
        // test the number of methods:
        assertEquals(type.getMethods().size(), 2);
        Method method = type.getMethods().get(0);
        assertEquals(method.getName().toString(), "GenericType02");
        assertEquals(method.toString(), "<S> GenericType02(final S s)");
        assertTrue(method.getAnnotations().isEmpty());
        assertTrue(method.getModifiers().isEmpty());
        assertEquals(method.getKind(), Method.Kind.CONSTRUCTOR);
        assertEquals(method.getTypeParameters().size(), 1);
        assertEquals(method.getParameters().size(), 1);
        final Parameter p = method.getParameters().get(0);
        assertEquals(p.getName().toString(), "s");
        assertEquals(p.getType().getQualifiedName().toString(), "S");
        assertFalse(p.getType().getQualifiedName().isPrimitive());
        assertFalse(p.getType().getQualifiedName().isWildcard());
        assertTrue(p.getType().getQualifiedName().isTypeVariable());
        assertTrue(p.isFinal());
        // ZODO: method = type.getMethods().get(1);
    }

    public void  testGenericType03() {
        fail("not yet implemented");
    }

    public void  testGenericType04() {
        fail("not yet implemented");
    }

    public void  testGenericType05() {
        fail("not yet implemented");
    }

    public void  testGenericType06() {
        fail("not yet implemented");
    }

    @Override
    protected String selectTransformer() {
        return "RemoveSingleEmptyDefaultConstructor";
    }

    @Override
    protected String getTestSources() {
        return "src/test/java/test/generics";
    }

}