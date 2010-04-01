package de.steinacker.jcg.util;

import de.steinacker.jcg.model.QualifiedName;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface FormatStringProvider {
    String getFormatForGetter(QualifiedName type);

    String getFormatForSetter(QualifiedName type);
}
