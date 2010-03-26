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
public class JcgRuntimeException extends RuntimeException {

    public JcgRuntimeException() {
    }

    public JcgRuntimeException(final String message) {
        super(message);
    }

    public JcgRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JcgRuntimeException(final Throwable cause) {
        super(cause);
    }

}