/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.exception.NoTransformerException;
import de.steinacker.jcg.transform.predicate.Rule;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Routes TypeMessages to one of several possible TypeTransformers using a Rule implementation to
 * select a transformer.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTransformerRouter implements TypeTransformer {
    private final static Logger LOG = Logger.getLogger(TypeTransformerRouter.class);

    private final Rule<TypeMessage, String> selector;
    private final Map<String, ? extends TypeTransformer> transformers;

    /**
     * @param selector     the rule implementation used to select a transformer
     * @param transformers the possible transformers.
     */
    public TypeTransformerRouter(final Rule<TypeMessage, String> selector,
                                 final Map<String, ? extends TypeTransformer> transformers) {
        this.selector = selector;
        this.transformers = new HashMap<String, TypeTransformer>(transformers);
    }

    @Override
    public String getName() {
        return "TypeTransformerRouter";
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
    public TypeMessage transform(final TypeMessage message) {
        final String key = this.selector.apply(message);
        if (transformers.containsKey(key)) {
            final TypeTransformer typeTransformer = transformers.get(key);
            LOG.info("Selecting " + typeTransformer);
            return typeTransformer.transform(message);
        }
        final String msg = "No TypeTransformer found for selector=" + key;
        LOG.error(msg);
        throw new NoTransformerException(msg);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TypeTransformerRouter");
        sb.append("{name='").append(getName()).append('\'');
        sb.append(", selector=").append(selector);
        sb.append('}');
        return sb.toString();
    }
}