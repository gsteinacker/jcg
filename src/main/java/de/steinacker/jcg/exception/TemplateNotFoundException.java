/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.exception;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class TemplateNotFoundException extends JcgException {

    public TemplateNotFoundException() {
    }

    public TemplateNotFoundException(final String message) {
        super(message);
    }

    public TemplateNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TemplateNotFoundException(final Throwable cause) {
        super(cause);
    }
}