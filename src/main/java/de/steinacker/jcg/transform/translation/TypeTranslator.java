/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.SimpleName;
import de.steinacker.jcg.transform.type.AbstractTypeTranslator;
import de.steinacker.jcg.util.NameUtil;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.List;

/**
 * Translates types from one language into another language, using a Glossary.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTranslator extends AbstractTypeTranslator {

    /** A list of possible prefixes of method names, where the remaining part of the
     * method name may be translatable.
     * TODO: make list configurable!
     */
    private static final List<String> translatableNamePrefixes = Arrays.asList(
            "is", "has", "set", "get", "add", "remove", "put"
    );

    private String name;
    private Glossary glossary;

    /**
     * Injects the Glossary used to translate the names.
     * @param glossary Glossary
     */
    @Required
    public void setGlossary(final Glossary glossary) {
        this.glossary = glossary;
    }

    /**
     * Injects the name of the transformer.
     * @param name name of the transformer
     */
    @Required
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    protected SimpleName translateSimpleName(final CharSequence sourceName) {
        if (glossary.hasTranslation(sourceName, glossary.getSourceLanguage())) {
            final String translation = glossary.getTranslation(sourceName, glossary.getSourceLanguage());
            final boolean firstUpperCase = NameUtil.isFirstUpperCase(sourceName);
            return SimpleName.valueOf(NameUtil.toCamelHumpName(translation, firstUpperCase));
        } else {
            return SimpleName.valueOf(sourceName);
        }
    }

    @Override
    protected QualifiedName translateQualifiedName(final QualifiedName sourceName) {
        final SimpleName simpleName = translateSimpleName(sourceName.getSimpleName());
        return QualifiedName.valueOf(sourceName.getPackage(), simpleName);
    }

    @Override
    protected SimpleName translateCamelHumpName(final CharSequence name) {
        String[] parts = null;
        final String s = name.toString();
        String methodPrefix = "";
        for (final String prefix : translatableNamePrefixes) {
            if (s.startsWith(prefix) && prefix.length() < s.length()) {
                methodPrefix = prefix;
                parts = NameUtil.splitCamelHumpName(s.substring(prefix.length()));
            }
        }
        if (parts != null) {
            final StringBuilder camelHumpName = new StringBuilder();
            for (String part : parts) {
                camelHumpName.append(translateSimpleName(part));
            }
            return SimpleName.valueOf(methodPrefix + NameUtil.toCamelHumpName(camelHumpName.toString(), true));
        } else {
            return translateSimpleName(name);
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
