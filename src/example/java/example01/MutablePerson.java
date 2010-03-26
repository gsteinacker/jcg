/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example01;

import de.steinacker.jcg.annotation.Method;
import de.steinacker.jcg.annotation.MutableBean;
import de.steinacker.jcg.annotation.Property;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@MutableBean
public class MutablePerson {

    public static final int INT_42 = 42;
    public static final int INT_4711;

    static {
        INT_4711 = 4711;
    }

    @Property
    public String firstName;

    @Property
    public String lastName;

    // TODO: Warnung oder Fehler ausgeben? Kopieren? 
    private String ignoredAttribute;

    @Method
    public String getName() {
        return new StringBuilder(firstName)
                .append(" ")
                .append(lastName)
                .toString();
    }

}
