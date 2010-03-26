/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This TypeTransformer transforms TypeMessages by delegating the incoming message to
 * a chain of other TypeTransformers.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TypeTransformerChain implements TypeTransformer {
    private static final Logger LOG = Logger.getLogger(TypeTransformerChain.class);

    private final List<TypeTransformer> chain;

    public TypeTransformerChain(final TypeTransformer... chain) {
        this.chain = Arrays.asList(chain);
    }

    public TypeTransformerChain(final List<? extends TypeTransformer> chain) {
        this.chain = new ArrayList<TypeTransformer>(chain);
    }

    public String getName() {
        return "TypeTransformerChain";
    }

    @Override
    public TypeMessage transform(final TypeMessage inputMessage) {
        TypeMessage msg = inputMessage;
        for (final TypeTransformer typeTransformer : chain) {
            LOG.info("Applying " + typeTransformer.getName());
            msg = typeTransformer.transform(msg);
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
}