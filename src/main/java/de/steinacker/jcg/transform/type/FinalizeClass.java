/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeBuilder;
import org.apache.log4j.Logger;

import static de.steinacker.jcg.model.TypeModifier.ABSTRACT;
import static de.steinacker.jcg.model.TypeModifier.FINAL;

/**
 * A ModelTransformer which makes a type 'final'.
 * <p/>
 * This Transformer optionally supports predicates to decide, whether a type
 * will be transformed or not.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class FinalizeClass extends SimpleTypeTransformer {

    private final static Logger LOG = Logger.getLogger(FinalizeClass.class);

    @Override
    public String getName() {
        return "FinalizeClass";
    }

    @Override
    protected TypeMessage doTransform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (type.is(ABSTRACT) || type.is(FINAL)) {
            return message;
        } else {
            final Type finalizedType = new TypeBuilder(type)
                    .addModifier(FINAL)
                    .toType();
            return new TypeMessage(finalizedType, message.getContext());
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}