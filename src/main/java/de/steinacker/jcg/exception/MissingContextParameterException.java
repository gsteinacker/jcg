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
public class MissingContextParameterException extends JcgRuntimeException {

    public MissingContextParameterException() {
    }

    public MissingContextParameterException(final String message) {
        super(message);
    }

    public MissingContextParameterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MissingContextParameterException(final Throwable cause) {
        super(cause);
    }
}