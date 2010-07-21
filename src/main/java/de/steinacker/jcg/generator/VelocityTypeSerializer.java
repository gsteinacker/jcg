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
import org.springframework.beans.factory.annotation.Required;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;


/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
final class VelocityTypeSerializer implements TypeSerializer {

    private static final Logger logger = Logger.getLogger(VelocityTypeSerializer.class);

    public final VelocityEngine ve = new VelocityEngine();
    public Set<String> templates;
    public TemplateSelector templateSelector;

    VelocityTypeSerializer() {
        try {
            ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            ve.init();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Exception while initializing Velocity: " + e.getMessage(), e);
        }
    }

    @Required
    public void setTemplates(final Set<String> templates) {
        this.templates = templates;
    }

    @Required
    public void setTemplateSelector(final TemplateSelector templateSelector) {
        this.templateSelector = templateSelector;
    }


    @Override
    public void serializeType(final Type type, final Appendable appendable) throws JcgException {
        final VelocityContext vc = new VelocityContext();
        vc.put("type", type);
        vc.put("codeUtil", new CodeUtil());
        vc.put("dateUtil", new DateUtil());
        try {
            final Template velocityTemplate = ve.getTemplate(templateSelector.select(type));
            final Writer writer = new StringWriter();
            velocityTemplate.merge(vc, writer);
            appendable.append(writer.toString());
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
