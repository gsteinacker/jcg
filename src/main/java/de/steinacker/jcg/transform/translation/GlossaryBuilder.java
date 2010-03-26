/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

/**
 * A glossary contains terms, optional translations and descriptive texts in at least one language.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class GlossaryBuilder {

    private final Locale sourceLanguage;
    private final Locale targetLanguage;
    private final Collection<Glossary.Entry> glossary;

    /**
     * Creates a new Glossary.
     *
     * @param sourceLanguage the source language of the glossary.
     * @param targetLanguage the target language of the glossary.
     */
    public GlossaryBuilder(final Locale sourceLanguage, final Locale targetLanguage) {
        this.glossary = new HashSet<Glossary.Entry>();
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    /**
     * Returns true, if the glossary has an entry for the specified term and language.
     *
     * @param term             a text in the source language
     * @param translation      a translation of the term
     * @param shortDescription a short description of the term
     * @return true if there is a translation for the term.
     */
    public GlossaryBuilder addEntry(final String term,
                                    final String pluralTerm,
                                    final String translation,
                                    final String pluralTranslation,
                                    final String shortDescription,
                                    final String longDescription) {
        glossary.add(new Glossary.Entry(
                term, pluralTerm, translation, pluralTranslation, shortDescription, longDescription));
        return this;
    }

    public Glossary toGlossary() {
        return new Glossary(sourceLanguage, targetLanguage, glossary);
    }

}