/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.rule;

import de.steinacker.jcg.transform.type.TypeMessage;

import java.util.Collections;
import java.util.List;

/**
 * Reads a String-parameter from the context and returns the value.
 * This rule is used by the RoutingModelTransformer to route the
 * transformation based on Context parameters.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class Rules implements Rule<TypeMessage, String> {

    private List<Rule<TypeMessage, String>> rules;

    public void setRules(final List<Rule<TypeMessage, String>> rules) {
        this.rules = rules;
    }

    @Override
    public List<String> apply(final TypeMessage message) {
        for (final Rule<TypeMessage, String> rule : rules) {
            final List<String> result = rule.apply(message);
            if (result != null && !result.isEmpty())
                return result;
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Rules");
        sb.append("{rules='").append(rules).append('\'');
        sb.append('}');
        return sb.toString();
    }
}