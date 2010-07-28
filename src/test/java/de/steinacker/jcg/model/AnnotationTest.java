/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

/**
 * @author Guido Steinacker
 * @since 28.07.2010
 */
public class AnnotationTest {

    @Test
    public void testAnnotationToString() {
        final Annotation annotation = new Annotation(QualifiedName.valueOf("test.Foo"));
        assertEquals(annotation.toString(), "@Foo");
    }

    @Test
    public void testAnnotationWithValueParamToString() {
        final Annotation annotation = new Annotation(
                QualifiedName.valueOf("test.Foo"),
                Collections.singletonList(new AnnotationParameter(
                        "value", false, new AnnotationValue(42L, "42"))),
                Collections.<AnnotationParameter>emptyList());
        assertEquals(annotation.toString(), "@Foo(42)");
    }

    @Test
    public void testAnnotationWithValueParamsToString() {
        final Annotation annotation = new Annotation(
                QualifiedName.valueOf("test.Foo"),
                Collections.singletonList(new AnnotationParameter("value", false,
                        new AnnotationValue(42L, "42"),
                        new AnnotationValue(43L, "43"))),
                Collections.<AnnotationParameter>emptyList());
        assertEquals(annotation.toString(), "@Foo(value={42, 43})");
    }

    @Test
    public void testAnnotationWithNamedParamToString() {
        final Annotation annotation = new Annotation(
                QualifiedName.valueOf("test.Foo"),
                Collections.singletonList(new AnnotationParameter(
                        "bar", false, new AnnotationValue(42L, "42"))),
                Collections.<AnnotationParameter>emptyList());
        assertEquals(annotation.toString(), "@Foo(bar=42)");
    }

    @Test
    public void testAnnotationWithNamedParamsToString() {
        final Annotation annotation = new Annotation(
                QualifiedName.valueOf("test.Foo"),
                Arrays.asList(
                        new AnnotationParameter("bar", false, new AnnotationValue(42L, "42")),
                        new AnnotationParameter("foobar", false, new AnnotationValue(42L, "42"))),
                Collections.<AnnotationParameter>emptyList());
        assertEquals(annotation.toString(), "@Foo(bar=42, foobar=42)");
    }
}