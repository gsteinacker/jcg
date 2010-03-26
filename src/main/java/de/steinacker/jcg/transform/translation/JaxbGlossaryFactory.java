/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

import de.steinacker.jcg.transform.translation.binding.Entry;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Locale;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class JaxbGlossaryFactory implements GlossaryFactory {

    private static final Logger LOG = Logger.getLogger(JaxbGlossaryFactory.class);
    private static final String GLOSSARY_FILE = "glossary/glossary.xml";
    private final Glossary glossary;

    public JaxbGlossaryFactory() {
        try {
            LOG.info("Loading glossary " + GLOSSARY_FILE);
            final JAXBContext jc = JAXBContext.newInstance(
                    de.steinacker.jcg.transform.translation.binding.Glossary.class.getPackage().getName(),
                    JaxbGlossaryFactory.class.getClassLoader());

            final Unmarshaller unmarshaller = jc.createUnmarshaller();
            // Load glossary.xml file
            final InputStream inStream = JaxbGlossaryFactory.class.getClassLoader().
                    getResourceAsStream(GLOSSARY_FILE);
            // Get the root element
            de.steinacker.jcg.transform.translation.binding.Glossary jaxbGlossary
                    = (de.steinacker.jcg.transform.translation.binding.Glossary) unmarshaller.unmarshal(inStream);

            glossary = mapToGlossary(jaxbGlossary);
            LOG.info("Source language is " + jaxbGlossary.getLanguage().getSource());
            LOG.info("Target language is " + jaxbGlossary.getLanguage().getTarget());
            LOG.info("Found " + jaxbGlossary.getEntry().size() + " entries.");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load glossary: " + e.getMessage(), e);
        }
    }

    @Override
    public Glossary glossary() {
        return glossary;
    }

    private static Glossary mapToGlossary(final de.steinacker.jcg.transform.translation.binding.Glossary jaxbGlossary) {
        final Locale sourceLang = new Locale(jaxbGlossary.getLanguage().getSource());
        final Locale targetLang = new Locale(jaxbGlossary.getLanguage().getTarget());
        final GlossaryBuilder gb = new GlossaryBuilder(sourceLang, targetLang);
        for (final Entry entry : jaxbGlossary.getEntry()) {
            gb.addEntry(
                    entry.getTerm(),
                    entry.getPluralTerm(),
                    entry.getTranslation(),
                    entry.getPluralTranslation(),
                    entry.getShortDescription(),
                    entry.getLongDescription());
        }
        return gb.toGlossary();
    }
}
