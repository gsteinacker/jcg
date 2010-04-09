/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.DefaultFormatStringProvider;
import de.steinacker.jcg.util.FormatStringProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;


public class AddConstructorsTest {

    private final FormatStringProvider formatStringProvider = new DefaultFormatStringProvider();
    private final Context context = new ContextBuilder().toContext();
    private final Type typeWithFinalAndNonFinal = new TypeBuilder()
            .setName(QualifiedName.valueOf("test.TestClass01"))
            .setKind(Type.Kind.CLASS)
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("finalField"))
                    .setTypeName(QualifiedName.valueOf("java.lang.String"))
                    .setModifiers(EnumSet.of(FieldModifier.FINAL))
                    .toField())
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("nonFinalField"))
                    .setTypeName(QualifiedName.valueOf("java.lang.String"))
                    .toField())
            .toType();
    private final Type typeWithTwoFinals = new TypeBuilder()
            .setName(QualifiedName.valueOf("test.TestClass01"))
            .setKind(Type.Kind.CLASS)
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("firstFinalField"))
                    .setTypeName(QualifiedName.valueOf("java.lang.String"))
                    .setModifiers(EnumSet.of(FieldModifier.FINAL))
                    .toField())
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("secondFinalField"))
                    .setTypeName(QualifiedName.valueOf("java.lang.String"))
                    .setModifiers(EnumSet.of(FieldModifier.FINAL))
                    .toField())
            .toType();
    private final Type typeWithTwoNonFinals = new TypeBuilder()
            .setName(QualifiedName.valueOf("test.TestClass01"))
            .setKind(Type.Kind.CLASS)
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("firstNonFinalField"))
                    .setTypeName(QualifiedName.valueOf("java.lang.String"))
                    .toField())
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("secondNonFinalField"))
                    .setTypeName(QualifiedName.valueOf("java.lang.String"))
                    .toField())
            .toType();
    private AddConstructors transformer;

    @Before
    public void setUp() {
        transformer = new AddConstructors();
        transformer.setName("test");
        transformer.setFormatStringProvider(formatStringProvider);
    }

    @Test
    public void transformDefault() {
        final TypeMessage tm = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        final Type t = tm.getPayload();
        assertEquals(1, t.getMethods().size());
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(2, t.getMethods().get(0).getParameters().size());
    }

    @Test
    public void transformFinalAndNonFinalWithDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final Type t = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context)).getPayload();
        assertEquals(1, t.getMethods().size());
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(2, t.getMethods().get(0).getParameters().size());
    }

    @Test
    public void transformTwoFinalsWithDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final Type t = transformer.transform(new TypeMessage(typeWithTwoFinals, context)).getPayload();
        assertEquals(1, t.getMethods().size());
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(2, t.getMethods().get(0).getParameters().size());
    }

    @Test
    public void transformTwoNonFinalsWithDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final Type t = transformer.transform(new TypeMessage(typeWithTwoNonFinals, context)).getPayload();
        assertEquals(2, t.getMethods().size());
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(0, t.getMethods().get(0).getParameters().size());
        assertTrue(t.getMethods().get(1).isConstructor());
        assertEquals(2, t.getMethods().get(1).getParameters().size());
    }

    @Test
    public void transformTwoNonFinalsWithExistingConstructors() {
        transformer.setGenerateDefaultConstructor(true);
        final TypeMessage tm = transformer.transform(new TypeMessage(typeWithTwoNonFinals, context));
        final Type t = transformer.transform(tm).getPayload();
        assertEquals(2, t.getMethods().size());
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(0, t.getMethods().get(0).getParameters().size());
        assertTrue(t.getMethods().get(1).isConstructor());
        assertEquals(2, t.getMethods().get(1).getParameters().size());
    }

    @Test
    public void transformTwoNonFinalsWithExistingDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final Type typeWithConstructor = new TypeBuilder(typeWithTwoNonFinals)
                .addMethod(new MethodBuilder()
                        .setName(typeWithTwoNonFinals.getName().getSimpleName())
                        .setReturnTypeName(null)
                        .addModifier(MethodModifier.PUBLIC)
                        .toMethod())
                .toType();
        final Type t = transformer.transform(new TypeMessage(typeWithConstructor, context)).getPayload();
        assertEquals(2, t.getMethods().size());
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(0, t.getMethods().get(0).getParameters().size());
        assertTrue(t.getMethods().get(1).isConstructor());
        assertEquals(2, t.getMethods().get(1).getParameters().size());
    }

    @Test
    public void transformOnlyFinalFields() {
        transformer.setOnlyFinalFields(true);
        final TypeMessage tm = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        final Type t = tm.getPayload();
        assertEquals(1, t.getMethods().size());
        final Method constructor = t.getMethods().get(0);
        assertTrue(constructor.isConstructor());
        assertEquals(1, constructor.getParameters().size());
        assertEquals("finalField", constructor.getParameters().get(0).getName().toString());
    }

    @Test
    public void transformFinalAndNonFinalFields() {
        transformer.setFinalAndNonFinalFields(true);
        final TypeMessage tm = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        final Type t = tm.getPayload();
        assertEquals(2, t.getMethods().size());
        Method constructor = t.getMethods().get(0);
        assertTrue(constructor.isConstructor());
        assertEquals(1, constructor.getParameters().size());
        assertEquals("finalField", constructor.getParameters().get(0).getName().toString());
        constructor = t.getMethods().get(1);
        assertTrue(constructor.isConstructor());
        assertEquals(2, constructor.getParameters().size());
        assertEquals("finalField", constructor.getParameters().get(0).getName().toString());
        assertEquals("nonFinalField", constructor.getParameters().get(1).getName().toString());
    }
}
