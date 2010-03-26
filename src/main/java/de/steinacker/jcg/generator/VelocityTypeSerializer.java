/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.generator;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.exception.ParseErrorException;
import de.steinacker.jcg.exception.TemplateNotFoundException;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.util.CodeUtil;
import de.steinacker.jcg.util.DateUtil;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.StringWriter;
import java.io.Writer;


/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
final class VelocityTypeSerializer implements TypeSerializer {

    private static final Logger logger = Logger.getLogger(VelocityTypeSerializer.class);

    public final VelocityEngine ve = new VelocityEngine();
    public final String template;

    VelocityTypeSerializer(final String template) {
        try {
            ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            ve.init();
            this.template = template;
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Exception while initializing Velocity: " + e.getMessage(), e);
        }
    }

    @Override
    public Writer serializeType(final Type type) throws JcgException {
        final StringWriter writer = new StringWriter(2048);
        serializeType(type, writer);
        return writer;
    }

    @Override
    public void serializeType(final Type type, final Writer writer) throws JcgException {
        final VelocityContext vc = new VelocityContext();
        vc.put("type", type);
        vc.put("codeUtil", new CodeUtil());
        vc.put("dateUtil", new DateUtil());
        try {
            final Template velocityTemplate = ve.getTemplate(template);
            velocityTemplate.merge(vc, writer);
        } catch (final ResourceNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new TemplateNotFoundException(e);
        } catch (final org.apache.velocity.exception.ParseErrorException e) {
            logger.error(e.getMessage(), e);
            throw new ParseErrorException(e);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new JcgException(e);
        }
    }
}
