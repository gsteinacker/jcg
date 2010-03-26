/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.exception;

/**
 * Root of the jcg exception hierarchy.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class JcgException extends Exception {

    public JcgException() {
    }

    public JcgException(final String message) {
        super(message);
    }

    public JcgException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JcgException(final Throwable cause) {
        super(cause);
    }

}
