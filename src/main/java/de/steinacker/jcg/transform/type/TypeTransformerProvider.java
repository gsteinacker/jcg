package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.exception.NoTransformerException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to all registered Transformers.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTransformerProvider {
    private final static Logger LOG = Logger.getLogger(TypeTransformerProvider.class);
    private final Map<String, TypeTransformer> transformers = new HashMap<String, TypeTransformer>();

    public void setTransformers(final List<TypeTransformer> transformers) {
        for (final TypeTransformer transformer : transformers) {
            final String name = transformer.getName();
            if (this.transformers.containsKey(name))
                LOG.warn("Redefining Transformer " + name);
            this.transformers.put(name, transformer);
        }
    }

    public TypeTransformer getTransformer(final String key) {
        if (transformers.containsKey(key))
            return transformers.get(key);
        else
            throw new NoTransformerException("Transformer " + key + " does not exist or is not registered properly.");
    }
}
