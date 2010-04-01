/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.rule;

import de.steinacker.jcg.transform.type.TypeMessage;

import java.util.Collections;
import java.util.List;

/**
 * Executes a sequence of rules, until a rule is returning a value.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class TransformerSelectors implements TypeTransformerSelector {

    private List<Rule<TypeMessage, List<String>>> rules;

    public void setRules(final List<Rule<TypeMessage, List<String>>> rules) {
        this.rules = rules;
    }

    @Override
    public List<String> apply(final TypeMessage message) {
        for (final Rule<TypeMessage, List<String>> rule : rules) {
            final List<String> result = rule.apply(message);
            if (result != null && !result.isEmpty())
                return result;
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TransformerSelectors");
        sb.append("{rules='").append(rules).append('\'');
        sb.append('}');
        return sb.toString();
    }
}