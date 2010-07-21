/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.codegen.VelocityTemplateProcessor;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.DefaultFormatStringProvider;
import de.steinacker.jcg.util.FormatStringProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class AddConstructorsTest {

    private final FormatStringProvider formatStringProvider = new DefaultFormatStringProvider();
    private final Context context = new ContextBuilder().toContext();
    private final Type typeWithFinalAndNonFinal = new TypeBuilder()
            .setName(QualifiedName.valueOf("test.TestClass01"))
            .setKind(Type.Kind.CLASS)
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("finalField"))
                    .setType(TYPESYMBOL_JAVA_LANG_STRING)
                    .setModifiers(EnumSet.of(FieldModifier.FINAL))
                    .toField())
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("nonFinalField"))
                    .setType(TYPESYMBOL_JAVA_LANG_STRING)
                    .toField())
            .toType();
    private final Type typeWithTwoFinals = new TypeBuilder()
            .setName(QualifiedName.valueOf("test.TestClass01"))
            .setKind(Type.Kind.CLASS)
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("firstFinalField"))
                    .setType(TYPESYMBOL_JAVA_LANG_STRING)
                    .setModifiers(EnumSet.of(FieldModifier.FINAL))
                    .toField())
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("secondFinalField"))
                    .setType(TYPESYMBOL_JAVA_LANG_STRING)
                    .setModifiers(EnumSet.of(FieldModifier.FINAL))
                    .toField())
            .toType();
    private final Type typeWithTwoNonFinals = new TypeBuilder()
            .setName(QualifiedName.valueOf("test.TestClass01"))
            .setKind(Type.Kind.CLASS)
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("firstNonFinalField"))
                    .setType(TYPESYMBOL_JAVA_LANG_STRING)
                    .toField())
            .addField(new FieldBuilder()
                    .setName(SimpleName.valueOf("secondNonFinalField"))
                    .setType(TYPESYMBOL_JAVA_LANG_STRING)
                    .toField())
            .toType();
    private AddConstructors transformer;
    private static final TypeSymbol TYPESYMBOL_JAVA_LANG_STRING = new TypeSymbol(QualifiedName.valueOf("java.lang.String"));

    @BeforeMethod
    public void setupTransformer() {
        transformer = new AddConstructors();
        transformer.setName("test");
        transformer.setTemplateName("/templates/field/setField.vm");
        transformer.setTemplateProcessor(new VelocityTemplateProcessor());
    }

    @Test
    public void transformDefault() {
        final List<TypeMessage> tm = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        assertEquals(tm.size(), 1);
        final Type t = tm.get(0).getPayload();
        assertEquals(t.getMethods().size(), 1);
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(t.getMethods().get(0).getParameters().size(), 2);
    }

    @Test
    public void transformFinalAndNonFinalWithDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final List<TypeMessage> typeMessages = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        assertEquals(typeMessages.size(), 1);
        final Type t = typeMessages.get(0).getPayload();
        assertEquals(t.getMethods().size(), 1);
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(t.getMethods().get(0).getParameters().size(), 2);
    }

    @Test
    public void transformTwoFinalsWithDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final List<TypeMessage> typeMessages = transformer.transform(new TypeMessage(typeWithTwoFinals, context));
        assertEquals(typeMessages.size(), 1);
        final Type t = typeMessages.get(0).getPayload();
        assertEquals(t.getMethods().size(), 1);
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(t.getMethods().get(0).getParameters().size(), 2);
    }

    @Test
    public void transformTwoNonFinalsWithDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final List<TypeMessage> typeMessages = transformer.transform(new TypeMessage(typeWithTwoNonFinals, context));
        assertEquals(typeMessages.size(), 1);
        final Type t = typeMessages.get(0).getPayload();
        assertEquals(t.getMethods().size(), 2);
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(t.getMethods().get(0).getParameters().size(), 0);
        assertTrue(t.getMethods().get(1).isConstructor());
        assertEquals(t.getMethods().get(1).getParameters().size(), 2);
    }

    @Test
    public void transformTwoNonFinalsWithExistingConstructors() {
        transformer.setGenerateDefaultConstructor(true);
        final List<TypeMessage> tm = transformer.transform(new TypeMessage(typeWithTwoNonFinals, context));
        assertEquals(tm.size(), 1);
        final Type t = transformer.transform(tm.get(0)).get(0).getPayload();
        assertEquals(t.getMethods().size(), 2);
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(t.getMethods().get(0).getParameters().size(), 0);
        assertTrue(t.getMethods().get(1).isConstructor());
        assertEquals(t.getMethods().get(1).getParameters().size(), 2);
    }

    @Test
    public void transformTwoNonFinalsWithExistingDefaultConstructor() {
        transformer.setGenerateDefaultConstructor(true);
        final Type typeWithConstructor = new TypeBuilder(typeWithTwoNonFinals)
                .addMethod(new MethodBuilder()
                        .setName(typeWithTwoNonFinals.getName().getSimpleName())
                        .setReturnType(null)
                        .addModifier(MethodModifier.PUBLIC)
                        .toMethod())
                .toType();
        final List<TypeMessage> typeMessages = transformer.transform(new TypeMessage(typeWithConstructor, context));
        final Type t = typeMessages.get(0).getPayload();
        assertEquals(t.getMethods().size(), 2);
        assertTrue(t.getMethods().get(0).isConstructor());
        assertEquals(t.getMethods().get(0).getParameters().size(), 0);
        assertTrue(t.getMethods().get(1).isConstructor());
        assertEquals(t.getMethods().get(1).getParameters().size(), 2);
    }

    @Test
    public void transformOnlyFinalFields() {
        transformer.setOnlyFinalFields(true);
        final List<TypeMessage> typeMessages = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        final TypeMessage tm = typeMessages.get(0);
        final Type t = tm.getPayload();
        assertEquals(t.getMethods().size(), 1);
        final Method constructor = t.getMethods().get(0);
        assertTrue(constructor.isConstructor());
        assertEquals(constructor.getParameters().size(), 1);
        assertEquals(constructor.getParameters().get(0).getName().toString(), "finalField");
    }

    @Test
    public void transformFinalAndNonFinalFields() {
        transformer.setFinalAndNonFinalFields(true);
        final List<TypeMessage> typeMessages = transformer.transform(new TypeMessage(typeWithFinalAndNonFinal, context));
        final TypeMessage tm = typeMessages.get(0);
        final Type t = tm.getPayload();
        assertEquals(t.getMethods().size(), 2);
        Method constructor = t.getMethods().get(0);
        assertTrue(constructor.isConstructor());
        assertEquals(constructor.getParameters().size(), 1);
        assertEquals(constructor.getParameters().get(0).getName().toString(), "finalField");
        constructor = t.getMethods().get(1);
        assertTrue(constructor.isConstructor());
        assertEquals(constructor.getParameters().size(), 2);
        assertEquals(constructor.getParameters().get(0).getName().toString(), "finalField");
        assertEquals(constructor.getParameters().get(1).getName().toString(), "nonFinalField");
    }
}
