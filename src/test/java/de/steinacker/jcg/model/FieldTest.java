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
public class FieldTest {

    @Test
    public void testFieldToString() {
        final Field field = new FieldBuilder()
                .setType(new TypeSymbol(QualifiedName.valueOf("int")))
                .setName(SimpleName.valueOf("foo"))
                .toField();
        assertEquals(field.toString(), "int foo");
    }

    @Test
    public void testFieldWithGenericReturnTypeToString() {
        final List<TypeParameter> typeParams = Collections.singletonList(new TypeParameter(QualifiedName.valueOf("java.lang.Number")));
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Field field = new FieldBuilder()
                .setType(typeSymbol)
                .setName(SimpleName.valueOf("foo"))
                .toField();
        assertEquals(field.toString(), "List<Number> foo");
    }


    @Test
    public void testFieldWithGenericTypeVarReturnTypeToString() {
        final List<TypeParameter> typeParams = Collections.singletonList(new TypeParameter(QualifiedName.valueOf("T")));
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Field field = new FieldBuilder()
                .setType(typeSymbol)
                .setName(SimpleName.valueOf("foo"))
                .toField();
        assertEquals(field.toString(), "List<T> foo");
    }

    @Test
    public void testFieldWithGenericBoundedReturnTypeToString() {
        final TypeParameter typeParameter = new TypeParameter(QualifiedName.valueOf("?"), Collections.singletonList(QualifiedName.valueOf("java.lang.Number")));
        final List<TypeParameter> typeParams = Collections.singletonList(typeParameter);
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        final Field field = new FieldBuilder()
                .setType(typeSymbol)
                .setName(SimpleName.valueOf("foo"))
                .toField();
        assertEquals(field.toString(), "List<? extends Number> foo");
    }
}