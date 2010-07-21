package de.steinacker.jcg.util;

import de.steinacker.jcg.model.QualifiedName;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface FormatStringProvider {
    public String getFormatForGetter(QualifiedName type);

    public String getFormatForSetter(QualifiedName type);
}
