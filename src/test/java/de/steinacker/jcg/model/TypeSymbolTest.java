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
public class TypeSymbolTest {

    @Test
    public void testTypeSymbolToString() {
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"));
        assertEquals(typeSymbol.toString(), "List");
    }

    @Test
    public void testTypeSymbolWithTypeParamToString() {
        final List<TypeParameter> typeParams = Collections.singletonList(new TypeParameter(QualifiedName.valueOf("T")));
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        assertEquals(typeSymbol.toString(), "List<T>");
    }

    @Test
    public void testTypeSymbolWithBoundedTypeParamToString() {
        final TypeParameter typeParameter = new TypeParameter(QualifiedName.valueOf("?"), Collections.singletonList(QualifiedName.valueOf("java.lang.Number")));
        final List<TypeParameter> typeParams = Collections.singletonList(typeParameter);
        final TypeSymbol typeSymbol = new TypeSymbol(QualifiedName.valueOf("java.util.List"), typeParams);
        assertEquals(typeSymbol.toString(), "List<? extends Number>");
    }
}
