/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Guido Steinacker
 * @since 28.07.2010
 */
public class ImportTest {

    @Test
    public void testSingleImportToString() {
        final Import staticImport = new Import(QualifiedName.valueOf("foo.bar.FooBar"));
        assertEquals(staticImport.toString(), "import foo.bar.FooBar;");
    }

    @Test
    public void testImportToString() {
        final Import staticImport = new Import(QualifiedName.valueOf("foo.bar.*"));
        assertEquals(staticImport.toString(), "import foo.bar.*;");
    }

    @Test
    public void testSingleStaticImportToString() {
        final Import staticImport = new Import(QualifiedName.valueOf("foo.bar.FooBar"), true);
        assertEquals(staticImport.toString(), "import static foo.bar.FooBar;");
    }

    @Test
    public void testStaticImportToString() {
        final Import staticImport = new Import(QualifiedName.valueOf("foo.bar.FooBar.*"), true);
        assertEquals(staticImport.toString(), "import static foo.bar.FooBar.*;");
    }

    @Test (expectedExceptions = java.lang.IllegalArgumentException.class)
    public void testPrimitiveImport() {
        new Import(QualifiedName.valueOf("int"));
    }

    @Test (expectedExceptions = java.lang.IllegalArgumentException.class)
    public void testTypeVarImport() {
        new Import(QualifiedName.valueOf("T"));
    }

    @Test (expectedExceptions = java.lang.IllegalArgumentException.class)
    public void testWildcardImport1() {
        new Import(QualifiedName.valueOf("?"));
    }

}
