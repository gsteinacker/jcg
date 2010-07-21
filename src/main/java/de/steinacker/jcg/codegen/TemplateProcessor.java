/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.codegen;

import java.util.Map;

/**
 * A TemplateProcessor is used to process templates.
 *
 * @author Guido Steinacker
 * @since 21.07.2010
 */
public interface TemplateProcessor {

    /**
     * Processes the template with the specified argument and appends the results to the Appendable.
     *
     * @param ctx the ProcessingContext
     * @param template the name of the template to process.
     * @param appendable the Appendable the generated code is appended to.
     * @param arguments arguments needed by the template. The keys are used by the templates to lookup the argument.
     */
    public void process(ProcessingContext ctx, String template, Appendable appendable, Map<String, ?> arguments);

}
