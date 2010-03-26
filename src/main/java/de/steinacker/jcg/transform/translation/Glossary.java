/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

import de.steinacker.jcg.util.NameUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A glossary contains terms, optional translations and descriptive texts in at least one language.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Glossary {

    /**
     * A glossary entry consisting of a term, a translation (both singular + plural) of this term,
     * a short description, and a long description.
     * <p>Descriptions are used for documentation purposes. For example, a short description might be
     * used to generate JavaDoc comments for parameters, while a long description is used to
     * comment types or packages.
     * <p>The plural form is used to generate fields + methods with cardinality > 1.
     */
    public static final class Entry {
        @NotNull
        private final String singularTerm;
        @NotNull
        private final String pluralTerm;
        @NotNull
        private final String singularTranslation;
        @NotNull
        private final String pluralTranslation;
        @NotNull
        private final String shortDescription;
        @NotNull
        private final String longDescription;

        /**
         * Creates a new glossary entry.
         *
         * @param singularTerm
         * @param pluralTerm
         * @param singularTranslation
         * @param pluralTranslation
         * @param shortDescription
         * @param longDescription
         */
        public Entry(final String singularTerm,
                     final String pluralTerm,
                     final String singularTranslation,
                     final String pluralTranslation,
                     final String shortDescription,
                     final String longDescription) {
            this.singularTerm = singularTerm;
            this.pluralTerm = pluralTerm;
            this.singularTranslation = singularTranslation;
            this.pluralTranslation = pluralTranslation;
            this.shortDescription = shortDescription;
            this.longDescription = longDescription;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Entry entry = (Entry) o;

            if (!longDescription.equals(entry.longDescription)) return false;
            if (!pluralTerm.equals(entry.pluralTerm)) return false;
            if (!pluralTranslation.equals(entry.pluralTranslation)) return false;
            if (!shortDescription.equals(entry.shortDescription)) return false;
            if (!singularTerm.equals(entry.singularTerm)) return false;
            if (!singularTranslation.equals(entry.singularTranslation)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = singularTerm.hashCode();
            result = 31 * result + pluralTerm.hashCode();
            result = 31 * result + singularTranslation.hashCode();
            result = 31 * result + pluralTranslation.hashCode();
            result = 31 * result + shortDescription.hashCode();
            result = 31 * result + longDescription.hashCode();
            return result;
        }
    }

    @NotNull
    @Valid
    private final Map<String, Entry> glossary;
    @NotNull
    private final Locale sourceLanguage;
    @NotNull
    private final Locale targetLanguage;

    /**
     * Creates a new Glossary.
     *
     * @param sourceLanguage  the source language of the glossary.
     * @param targetLanguage  the target language of the glossary.
     * @param glossaryEntries the glossary entries.
     */
    public Glossary(final Locale sourceLanguage,
                    final Locale targetLanguage,
                    final Collection<Entry> glossaryEntries) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.glossary = new HashMap<String, Entry>(glossaryEntries.size());
        for (final Entry entry : glossaryEntries) {
            glossary.put(buildKey(entry.singularTerm, sourceLanguage), entry);
            glossary.put(buildKey(entry.pluralTerm, sourceLanguage), entry);
            if (!entry.singularTranslation.isEmpty()) {
                glossary.put(buildKey(entry.singularTranslation, targetLanguage), entry);
                glossary.put(buildKey(entry.pluralTranslation, targetLanguage), entry);
            }
        }
    }

    public Locale getSourceLanguage() {
        return sourceLanguage;
    }

    public Locale getTargetLanguage() {
        return targetLanguage;
    }

    /**
     * Returns true, if the glossary has an entry for the specified term and language.
     *
     * @param term     a text in the specified language
     * @param language the language of the specified text.
     * @return true if there is a translation for the term.
     */
    public boolean hasEntry(final CharSequence term, final Locale language) {
        return glossary.containsKey(buildKey(term, language));
    }

    /**
     * Returns true, if the glossary has an entry for the specified term and language
     * and an translation of this term.
     *
     * @param term     a text in the specified language
     * @param language the language of the specified text.
     * @return true if there is a translation for the term.
     */
    public boolean hasTranslation(final CharSequence term, final Locale language) {
        final Entry entry = glossary.get(buildKey(term, language));
        return entry != null && !entry.singularTranslation.isEmpty();
    }

    /**
     * Returns the translation of the specified term.
     *
     * @param term     a text in the specified language
     * @param language the language of the specified text.
     * @return the translation
     * @throws IllegalArgumentException if there is no entry for the specified term.
     */
    public String getTranslation(final CharSequence term, final Locale language) {
        final String key = buildKey(term, language);
        final Entry entry = glossary.get(key);
        if (entry == null || entry.singularTranslation.isEmpty())
            throw new IllegalArgumentException("No translation found for '" + term + "'.");
        final boolean isSingular = key.equals(buildKey(entry.singularTerm, language));
        if (isSingular) {
            return entry.singularTranslation;
        } else {
            return entry.pluralTranslation;
        }
    }

    /**
     * Returns the short description of the specified term.
     *
     * @param term     a text in the specified language
     * @param language the language of the specified text.
     * @return the short description
     * @throws IllegalArgumentException if there is no entry for the specified term.
     */
    public String getShortDescription(final CharSequence term, final Locale language) {
        final Entry entry = glossary.get(buildKey(term, language));
        if (entry == null)
            throw new IllegalArgumentException("No entry found for '" + term + "'.");
        return entry.shortDescription;
    }

    /**
     * Returns the short description of the specified term.
     *
     * @param term     a text in the specified language
     * @param language the language of the specified text.
     * @return the short description
     * @throws IllegalArgumentException if there is no entry for the specified term.
     */
    public String getLongDescription(final CharSequence term, final Locale language) {
        final Entry entry = glossary.get(buildKey(term, language));
        if (entry == null)
            throw new IllegalArgumentException("No entry found for '" + term + "'.");
        return entry.shortDescription;
    }

    private static String buildKey(final CharSequence text, final Locale language) {
        return new StringBuilder()
                .append(language.getLanguage())
                .append('#')
                .append(NameUtil.toCamelHumpName(text.toString(), false).toLowerCase())
                .toString();
    }

}
