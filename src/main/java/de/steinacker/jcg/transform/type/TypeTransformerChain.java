/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

/**
 * This TypeTransformer transforms TypeMessages by delegating the incoming message to
 * a chain of other TypeTransformers.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTransformerChain implements TypeTransformer, ApplicationContextAware {
    private static final Logger LOG = Logger.getLogger(TypeTransformerChain.class);

    private String name;
    private List<String> chain;
    private transient volatile ApplicationContext context;


    public void setTransformers(final List<String> transformers) {
        chain = new ArrayList<String>(transformers);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public TypeMessage transform(final TypeMessage inputMessage) {
        TypeMessage msg = inputMessage;
        TypeTransformerProvider provider = context.getBean(TypeTransformerProvider.class);
        for (final String key : chain) {
            final TypeTransformer transformer = provider.getTransformer(key);
            LOG.info("Applying " + transformer.getName());
            msg = transformer.transform(msg);
        }
        return msg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TypeTransformerChain");
        sb.append("{chain=").append(chain);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}