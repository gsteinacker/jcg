/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.model;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.model.ModelBuilder;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.transform.type.TypeMessage;
import de.steinacker.jcg.transform.type.TypeTransformer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits ModelMessages into several TypeMessages, iterates over these messages and calls
 * the TypeTransformer for every message.
 * After this, all returned TypeMessages are merged into one Model.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ModelSequencer implements ModelTransformer {

    private static final Logger LOG = Logger.getLogger(ModelSequencer.class);
    private final TypeTransformer typeTransformer;

    /**
     * Creates a ModelSequencer.
     *
     * @param typeTransformer The TypeTransformer used to transform the TypeMessages.
     */
    public ModelSequencer(final TypeTransformer typeTransformer) {
        this.typeTransformer = typeTransformer;
    }

    @Override
    public String getName() {
        return "ModelSequencer";
    }

    @Override
    public ModelMessage transform(ModelMessage message) {
        final List<TypeMessage> transformedMessages = new ArrayList<TypeMessage>();
        for (final TypeMessage typeMessage : split(message)) {
            LOG.info("Transforming " + typeMessage.getPayload().getName());
            transformedMessages.add(typeTransformer.transform(typeMessage));
        }
        return aggregate(transformedMessages);
    }

    /**
     * Splits a ModelMessage into several TypeMessages.
     *
     * @param modelMessage the ModelMessage
     * @return TypeMessage
     */
    public List<TypeMessage> split(final ModelMessage modelMessage) {
        final List<TypeMessage> messages = new ArrayList<TypeMessage>();
        final Context context = modelMessage.getContext();
        for (final Type type : modelMessage.getPayload().getAllTypes()) {
            messages.add(new TypeMessage(type, context));
        }
        return messages;
    }

    /**
     * Aggregates several TypeMessages into one ModelMessage.
     *
     * @param typeMessages the list of TypeMessages.
     * @return ModelMessage
     */
    public ModelMessage aggregate(final List<TypeMessage> typeMessages) {
        final ModelBuilder modelBuilder = new ModelBuilder();
        final ContextBuilder ctxBuilder = new ContextBuilder();
        for (final TypeMessage typeMessage : typeMessages) {
            modelBuilder.addType(typeMessage.getPayload());
            ctxBuilder.mergeWith(typeMessage.getContext());
        }
        return new ModelMessage(modelBuilder.toModel(), ctxBuilder.toContext());
    }

}
