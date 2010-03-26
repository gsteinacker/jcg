/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.model;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This ModelTransformer transforms ModelMessages by delegating the incoming message to
 * a chain of ModelTransformers.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ModelTransformerChain implements ModelTransformer {
    private static final Logger LOG = Logger.getLogger(ModelTransformerChain.class);

    private final List<ModelTransformer> chain;

    public ModelTransformerChain(final ModelTransformer... chain) {
        this.chain = Arrays.asList(chain);
    }

    public ModelTransformerChain(final List<? extends ModelTransformer> chain) {
        this.chain = new ArrayList<ModelTransformer>(chain);
    }

    public String getName() {
        return "ModelTransformerChain";
    }

    @Override
    public ModelMessage transform(final ModelMessage inputMessage) {
        ModelMessage msg = inputMessage;
        for (final ModelTransformer modelTransformer : chain) {
            LOG.info("Applying " + modelTransformer.getName());
            msg = modelTransformer.transform(msg);
        }
        return msg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ModelTransformerChain");
        sb.append("{chain=").append(chain);
        sb.append('}');
        return sb.toString();
    }
}
