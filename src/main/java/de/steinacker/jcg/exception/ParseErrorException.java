/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.exception;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class ParseErrorException extends JcgException {

    public ParseErrorException() {
    }

    public ParseErrorException(final String message) {
        super(message);
    }

    public ParseErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParseErrorException(final Throwable cause) {
        super(cause);
    }
}
