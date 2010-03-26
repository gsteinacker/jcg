/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.transform.predicate.Predicate;
import de.steinacker.jcg.transform.predicate.TruePredicate;

/**
 * A simple TypeTransformer that adds support for Predicate<Type>.
 * If the predicate evaluates to true, doTransform(message) is called,
 * otherwise the incoming message is returned as it is.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public abstract class SimpleTypeTransformer implements TypeTransformer {
    private Predicate<Type> typePredicate = new TruePredicate<Type>();

    public final void setTypePredicate(final Predicate<Type> typePredicate) {
        this.typePredicate = typePredicate;
    }

    @Override
    public TypeMessage transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (typePredicate.eval(type)) {
            return doTransform(message);
        } else
            return message;
    }

    protected abstract TypeMessage doTransform(final TypeMessage message);
}
