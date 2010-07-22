/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.codegen;

import de.steinacker.jcg.model.Import;
import de.steinacker.jcg.model.QualifiedName;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The ProcessingContext is used by the TemplateProcessor and the Templates to gather
 * state information like types used in the generated code.
 * These types might be used to generate import statements needed by a Type.
 *
 * @author Guido Steinacker
 * @since 21.07.2010
 */
public final class ProcessingContext {
    private Set<Import> addedImports = new HashSet<Import>();

    public void addImport(final String qn) {
        addedImports.add(new Import(QualifiedName.valueOf(qn)));
    }

    public void addImport(final String qn, final boolean isStatic) {
        addedImports.add(new Import(QualifiedName.valueOf(qn), isStatic));
    }

    public Set<Import> getAddedImports() {
        return Collections.unmodifiableSet(addedImports);
    }
}
