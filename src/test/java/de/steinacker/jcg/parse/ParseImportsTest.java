/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.Import;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.test.AbstractJcgTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;


public final class ParseImportsTest extends AbstractJcgTest {

    private static final QualifiedName QN_CLASS01 = QualifiedName.valueOf("test.imports.Class01");

    @Test
    public void testExistenceOfClasses() {
        final Model model = getParsedModel();
        assertNotNull(model.getType(QN_CLASS01));
    }

    public void isImportOrderCorrect() {
        final Type type = getParsedModel().getType(QN_CLASS01);
        final List<Import> imports = new ArrayList<Import>(type.getImports());
        assertEquals(imports.size(), 5);
        assertEquals(imports.get(0), new Import(QualifiedName.valueOf("java.io.*")));
        assertEquals(imports.get(1), new Import(QualifiedName.valueOf("java.util.Collections")));
        assertEquals(imports.get(2), new Import(QualifiedName.valueOf("java.util.List")));
        assertEquals(imports.get(3), new Import(QualifiedName.valueOf("java.lang.Math.abs"), true));
        assertEquals(imports.get(4), new Import(QualifiedName.valueOf("java.lang.Math, ceil"), true));
    }

    @Test
    public void noJavaLangImports() {
        final Type type = getParsedModel().getType(QN_CLASS01);
        final Set<Import> imports = type.getImports();
        assertFalse(imports.contains(new Import(QualifiedName.valueOf("java.lang.*"))));
    }

    @Override
    protected String selectTransformer() {
        return "RemoveSingleEmptyDefaultConstructor";
    }

    @Override
    protected String getTestSources() {
        return "src/test/java/test/imports";
    }

}