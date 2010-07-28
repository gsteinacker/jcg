/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.SimpleName;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Guido Steinacker
 * @since 28.07.2010
 */
public final class TypeMapper extends AbstractTypeTranslator implements TypeTransformer {

    private String name;
    private Map<String,String> mappingPatterns;

    /**
     * Injects the name of the TypeMapper transformer instance.
     * @param name the name of the transformer.
     */
    @Required
    public void setName(final String name) {
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }

    @Required
    public void setMappingPatterns(final Properties mappingPatterns) {
        this.mappingPatterns = new HashMap<String,String>();
        for (final Object key : mappingPatterns.keySet()) {
            this.mappingPatterns.put(key.toString(), mappingPatterns.getProperty((String)key));
        }
    }

    @Override
    protected SimpleName translateSimpleName(CharSequence sourceName) {
        return sourceName instanceof SimpleName
                ? (SimpleName) sourceName
                : SimpleName.valueOf(sourceName);
    }

    @Override
    protected QualifiedName translateQualifiedName(final QualifiedName sourceName) {
        final QualifiedName result;
        if (!sourceName.isPrimitive() && !sourceName.isTypeVariable() && !sourceName.isWildcard()) {
            String targetName = null;
            for (final String sourceRegExp : mappingPatterns.keySet()) {
                final String fromName = sourceName.toString();
                if (fromName.matches(sourceRegExp)) {
                    final Pattern pattern = Pattern.compile(sourceRegExp);
                    final Matcher matcher = pattern.matcher(fromName);
                    final int groupCount = matcher.groupCount();
                    final Object[] args = new String[groupCount];
                    if (matcher.find()) {
                        for (int i=0; i<groupCount; ++i) {
                            args[i] = matcher.group(i+1);
                        }
                    }
                    targetName = String.format(mappingPatterns.get(sourceRegExp), args);
                    break;
                }
            }
            result = targetName != null ? QualifiedName.valueOf(targetName) : sourceName;
        } else {
            result = sourceName;
        }
        return result;
    }

    @Override
    protected SimpleName translateCamelHumpName(final CharSequence sourceName) {
        return sourceName instanceof SimpleName
                ? (SimpleName) sourceName
                : SimpleName.valueOf(sourceName);
    }

    @Override
    public String toString() {
        return name;
    }
}
