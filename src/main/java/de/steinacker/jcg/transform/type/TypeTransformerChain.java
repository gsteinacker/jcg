/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
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

    private String name;
    private List<TypeTransformer> chain;

    @Required
    public void setTransformerChain(final List<TypeTransformer> transformerChain) {
        chain = transformerChain;
    }

    @Required
    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<TypeMessage> transform(final TypeMessage inputMessage) {
        List<TypeMessage> result = Collections.singletonList(inputMessage);
        for (final TypeTransformer transformer : chain) {
            LOG.info("Applying " + transformer.toString());
            final List<TypeMessage> temp = new ArrayList<TypeMessage>();
            for (final TypeMessage typeMessage : result) {
                temp.addAll(transformer.transform(typeMessage));
            }
            result = temp;
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TypeTransformerChain");
        sb.append("{name=").append(name);
        sb.append("{chain=").append(chain);
        sb.append('}');
        return sb.toString();
    }

}