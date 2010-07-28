/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Guido Steinacker
 * @since 28.07.2010
 */
public class MethodTest {

    @Test
    public void testMethodToString() {
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .toMethod();
        assertEquals(method.toString(), "void foo()");
    }

    @Test
    public void testMethodWithSimpleReturnTypeToString() {
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"));
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .setReturnType(typeSymbol)
                .toMethod();
        assertEquals(method.toString(), "List foo()");
    }

    @Test
    public void testMethodWithSimpleParamToString() {
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"));
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .addParameter(new ParameterBuilder()
                        .setType(typeSymbol)
                        .setName(SimpleName.valueOf("bar"))
                        .toParameter())
                .toMethod();
        assertEquals(method.toString(), "void foo(List bar)");       
    }

    @Test
    public void testMethodWithGenericReturnTypeToString() {
        final List<TypeParameter> typeParams = Collections.singletonList(new TypeParameter(QualifiedName.valueOf("T")));
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .setReturnType(typeSymbol)
                .toMethod();
        assertEquals(method.toString(), "List<T> foo()");
    }

    @Test
    public void testMethodWithGenericTypeVarParamToString() {
        final List<TypeParameter> typeParams = Collections.singletonList(new TypeParameter(QualifiedName.valueOf("T")));
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .addParameter(new ParameterBuilder()
                        .setType(typeSymbol)
                        .setName(SimpleName.valueOf("bar"))
                        .toParameter())
                .toMethod();
        assertEquals(method.toString(), "void foo(List<T> bar)");
    }

    @Test
    public void testMethodWithGenericParamToString() {
        final List<TypeParameter> typeParams = Collections.singletonList(new TypeParameter(QualifiedName.valueOf("java.lang.Number")));
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .addParameter(new ParameterBuilder()
                        .setType(typeSymbol)
                        .setName(SimpleName.valueOf("bar"))
                        .toParameter())
                .toMethod();
        assertEquals(method.toString(), "void foo(List<Number> bar)");
    }

    @Test
    public void testMethodWithGenericBoundedReturnTypeToString() {
        final TypeParameter typeParameter = new TypeParameter(
                QualifiedName.valueOf("?"),
                Collections.singletonList(QualifiedName.valueOf("java.lang.Number")));
        final List<TypeParameter> typeParams = Collections.singletonList(typeParameter);
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .setReturnType(typeSymbol)
                .toMethod();
        assertEquals(method.toString(), "List<? extends Number> foo()");
    }

    @Test
    public void testMethodWithGenericBoundedParamToString() {
        final TypeParameter typeParameter = new TypeParameter(QualifiedName.valueOf("?"), Collections.singletonList(QualifiedName.valueOf("java.lang.Number")));
        final List<TypeParameter> typeParams = Collections.singletonList(typeParameter);
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .addParameter(new ParameterBuilder()
                        .setType(typeSymbol)
                        .setName(SimpleName.valueOf("bar"))
                        .toParameter())
                .toMethod();
        assertEquals(method.toString(), "void foo(List<? extends Number> bar)");
    }

        @Test
    public void testParameterizedMethodWithGenericBoundedParamToString() {
        final TypeParameter typeParameter = new TypeParameter(QualifiedName.valueOf("T"), Collections.singletonList(QualifiedName.valueOf("java.lang.Number")));
        final List<TypeParameter> typeParams = Collections.singletonList(typeParameter);
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("T"));
        final Method method = new MethodBuilder()
                .setName(SimpleName.valueOf("foo"))
                .setKind(Method.Kind.METHOD)
                .setTypeParameters(typeParams)
                .addParameter(new ParameterBuilder()
                        .setType(typeSymbol)
                        .setName(SimpleName.valueOf("bar"))
                        .toParameter())
                .toMethod();
        assertEquals(method.toString(), "<T extends Number> void foo(T bar)");
    }
}