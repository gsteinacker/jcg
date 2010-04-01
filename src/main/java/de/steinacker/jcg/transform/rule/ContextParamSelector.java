/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.rule;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.exception.MissingContextParameterException;
import de.steinacker.jcg.transform.type.TypeMessage;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads a String-parameter from the context and returns the value.
 * This rule is used by the RoutingModelTransformer to route the
 * transformation based on Context parameters.
 * Comma-separates values are allowed as parameter values.
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ContextParamSelector implements TypeTransformerSelector {

    private String ctxParam;

    /** The context-parameter that is evaluated to get the result of the rule.
     * @param ctxParam name of the context parameter.
     */
    @Required
    public void setCtxParam(final String ctxParam) {
        this.ctxParam = ctxParam;
    }

    @Override
    public List<String> apply(final TypeMessage message) {
        final Context context = message.getContext();
        if (ctxParam == null || context.getParameter(ctxParam, String.class) == null)
            throw new MissingContextParameterException("Missing Context-parameter " + ctxParam);

        final String[] value = context.getParameter(ctxParam, String.class).split(",");
        final List<String> result = new ArrayList<String>(value.length);
        for (final String s : value) {
            result.add(s.trim());
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContextParamSelector");
        sb.append("{ctxParam='").append(ctxParam).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
