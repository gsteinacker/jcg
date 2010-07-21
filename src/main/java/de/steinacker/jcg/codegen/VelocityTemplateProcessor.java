/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.codegen;

import de.steinacker.jcg.util.CodeUtil;
import de.steinacker.jcg.util.DateUtil;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;


/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class VelocityTemplateProcessor implements TemplateProcessor {

    private static final Logger logger = Logger.getLogger(VelocityTemplateProcessor.class);

    public final VelocityEngine ve = new VelocityEngine();

    public VelocityTemplateProcessor() {
        try {
            ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            ve.init();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Exception while initializing Velocity: " + e.getMessage(), e);
        }
    }

    @Override
    public void process(final ProcessingContext ctx,
                        final String template,
                        final Appendable appendable,
                        final Map<String, ?> arguments) {
        final VelocityContext vc = new VelocityContext();
        for (final String key : arguments.keySet()) {
            vc.put(key, arguments.get(key));
        }
        vc.put("ctx", ctx);
        vc.put("codeUtil", new CodeUtil());
        vc.put("dateUtil", new DateUtil());
        try {
            final org.apache.velocity.Template velocityTemplate = ve.getTemplate(template);
            final Writer writer = new StringWriter();
            velocityTemplate.merge(vc, writer);
            appendable.append(writer.toString());
        } catch (final ResourceNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalArgumentException("Template not found: " + e);
        } catch (final org.apache.velocity.exception.ParseErrorException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalArgumentException("Error parsing template: " + e);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalArgumentException("Exception caught while processing template: " + e);
        }

    }
}