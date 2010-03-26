/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.translation;

/**
 * A Factory to load and/or create Glossary instances.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface GlossaryFactory {

    /**
     * Creates a new Glossary instance.
     *
     * @return Glossary
     */
    public Glossary glossary();
}
