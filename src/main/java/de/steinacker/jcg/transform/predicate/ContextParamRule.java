/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.predicate;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.exception.MissingContextParameterException;
import de.steinacker.jcg.transform.type.TypeMessage;

/**
 * Reads a String-parameter from the context and returns the value.
 * This rule is used by the RoutingModelTransformer to route the
 * transformation based on Context parameters.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ContextParamRule implements Rule<TypeMessage, String> {

    private final String ctxParam;

    public ContextParamRule(final String ctxParam) {
        this.ctxParam = ctxParam;
    }

    @Override
    public String apply(final TypeMessage message) {
        final Context context = message.getContext();
        if (ctxParam == null || context.getParameter(ctxParam, String.class) == null)
            throw new MissingContextParameterException("Missing Context-parameter " + ctxParam);
        return context.getParameter(ctxParam, String.class);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContextParamRule");
        sb.append("{ctxParam='").append(ctxParam).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
