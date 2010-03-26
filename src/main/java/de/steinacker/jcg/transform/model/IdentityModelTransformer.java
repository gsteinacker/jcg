/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.model;

import de.steinacker.jcg.visitor.ModelVisitor;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class IdentityModelTransformer implements ModelTransformer {

    private final ModelVisitor visitor;

    public IdentityModelTransformer() {
        visitor = null;
    }

    public IdentityModelTransformer(final ModelVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public String getName() {
        return "Identity";
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation does not transform anything, but is only returning the incoming message.
     * If a visitor is provided, visitor.visit(message.getPayload()) is called, but no modifications
     * are contained in the returned ModelMessage.
     */
    @Override
    public final ModelMessage transform(final ModelMessage message) {
        visitor.visit(message.getPayload(), message.getContext());
        return message;
    }

}
