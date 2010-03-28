/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg;

import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.generator.Generator;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.parse.JavaFileParser;
import de.steinacker.jcg.transform.model.ModelMessage;
import de.steinacker.jcg.transform.model.ModelTransformer;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guido Steinacker
 */
public final class JcgController {
    private final static Logger LOG = Logger.getLogger(JcgController.class);

    private JavaFileParser parser;
    private ModelTransformer transformer;
    private Generator generator;

    public void setParser(final JavaFileParser parser) {
        this.parser = parser;
    }

    public void setModelTransformer(final ModelTransformer transformer) {
        this.transformer = transformer;
    }

    public void setGenerator(final Generator generator) {
        this.generator = generator;
    }

    public void invoke(final String selector,
                       final String sourceFile,
                       final String targetDir,
                       final String binDir) throws JcgException {
        LOG.info("Parsing...");
        final Model model = parser.parse(sourceFile, binDir);
        LOG.info("Transforming...");
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ctx-selector-param", selector);
        final ModelMessage message = new ModelMessage(model, new Context(parameters));
        final ModelMessage transformedMessage = transformer.transform(message);
        LOG.info("Generating...");
        generator.generate(transformedMessage.getPayload(), targetDir);
        LOG.info("Done.");
    }

    public void invoke(final String selector,
                       final String sourceDir,
                       final boolean recursive,
                       final String targetDir,
                       final String binDir) throws JcgException {
        LOG.info("Parsing...");
        final Model model = parser.parse(sourceDir, recursive, binDir);
        LOG.info("Transforming...");
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ctx-selector-param", selector);
        final ModelMessage message = new ModelMessage(model, new Context(parameters));
        final ModelMessage transformedMessage = transformer.transform(message);
        LOG.info("Generating...");
        generator.generate(transformedMessage.getPayload(), targetDir);
        LOG.info("Done.");
    }

}