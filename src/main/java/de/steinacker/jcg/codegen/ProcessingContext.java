/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.codegen;

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
    private Set<QualifiedName> addedImports = new HashSet<QualifiedName>();

    public void addImport(final String qn) {
        addedImports.add(QualifiedName.valueOf(qn));
    }

    public Set<QualifiedName> getAddedImports() {
        return Collections.unmodifiableSet(addedImports);
    }
}
