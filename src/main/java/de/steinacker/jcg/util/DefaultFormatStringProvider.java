package de.steinacker.jcg.util;

import de.steinacker.jcg.model.QualifiedName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This implementation of FormatStringProvider reads the formats from the classpath resource
 * /templates/formats/*.properties.
 *  
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class DefaultFormatStringProvider implements FormatStringProvider {

    private static final Map<QualifiedName, String> accessorFormats = new HashMap<QualifiedName, String>();
    private static final Map<QualifiedName, String> modifierFormats = new HashMap<QualifiedName, String>();
    static {
        final Properties accessorProperties = new Properties();
        final ClassLoader cl = DefaultFormatStringProvider.class.getClassLoader();
        try {
            accessorProperties.load(cl.getResourceAsStream("templates/formats/accessor.properties"));
            for (final Object key : accessorProperties.keySet()) {
                accessorFormats.put(QualifiedName.valueOf(key.toString()), accessorProperties.getProperty(key.toString()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("File not found: " + e.getMessage(), e);
        }
        final Properties modifierProperties = new Properties();
        try {
            modifierProperties.load(cl.getResourceAsStream("templates/formats/modifier.properties"));
            for (final Object key : modifierProperties.keySet()) {
                modifierFormats.put(QualifiedName.valueOf(key.toString()), modifierProperties.getProperty(key.toString()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("File not found: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the format string used to write code for a getter.
     *
     * @param type the type that is returned by the getter;
     * @return a format string
     */
    @Override
    public String getFormatForGetter(final QualifiedName type) {
        return (accessorFormats.containsKey(type) ? accessorFormats.get(type) : accessorFormats.get(QualifiedName.valueOf("default")));
    }

    /**
     * Returns the format string used to write code for a setter or constructor.
     *
     * @param type the type of the field that is modified.
     * @return a format string
     */
    @Override
    public String getFormatForSetter(final QualifiedName type) {
        return (modifierFormats.containsKey(type) ? modifierFormats.get(type) : modifierFormats.get(QualifiedName.valueOf("default")));
    }

}
