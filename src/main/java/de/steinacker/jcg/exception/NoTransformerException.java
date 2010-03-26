/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.exception;

/**
 * Exception thrown if a RoutingModelTransformer does not find a ModelTransformer.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class NoTransformerException extends JcgRuntimeException {

    public NoTransformerException() {
    }

    public NoTransformerException(final String message) {
        super(message);
    }

    public NoTransformerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoTransformerException(final Throwable cause) {
        super(cause);
    }
}