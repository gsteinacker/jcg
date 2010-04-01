/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.model;

import de.steinacker.jcg.transform.rule.Rule;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class RoutingModelTransformer implements ModelTransformer {
    private final static Logger LOG = Logger.getLogger(RoutingModelTransformer.class);

    private final Rule<ModelMessage, String> selector;
    private final Map<String, ModelTransformer> transformers;
    private final String name;

    /**
     * @param selector     the rule implementation used to select a transform
     * @param transformers the possible transformers.
     */
    public RoutingModelTransformer(final String name,
                                   final Rule<ModelMessage, String> selector,
                                   final List<? extends ModelTransformer> transformers) {
        this.selector = selector;
        this.name = name;
        this.transformers = new HashMap<String, ModelTransformer>(transformers.size());
        for (final ModelTransformer transformer : transformers) {
            if (this.transformers.containsKey(transformer.getName())) {
                LOG.warn("Transformer " + transformer.getName() + " is already defined.");
            }
            this.transformers.put(transformer.getName(), transformer);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Transforms the ModelMessage into another ModelMessage,
     *
     * @param message the source ModelMessage
     * @return the target ModelMessage
     * @throws de.steinacker.jcg.exception.NoTransformerException
     *          If no defaultTransformer is setParameters or no
     *          transform matches to the rule.
     */
    @Override
    public ModelMessage transform(final ModelMessage message) {
        ModelMessage tempMessage = message;
        final List<String> keys = this.selector.apply(message);
        if (keys.isEmpty()) {
            LOG.warn("No transformers returned from selector " + selector);
        }
        for (final String key : keys) {
            final ModelTransformer transformer = transformers.get(key);
            LOG.info("Selecting " + transformer);
            tempMessage = transformer.transform(tempMessage);
        }
        return tempMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RoutingModelTransformer");
        sb.append("{name='").append(name).append('\'');
        sb.append(", selector=").append(selector);
        sb.append('}');
        return sb.toString();
    }
}
