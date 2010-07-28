/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example.example01;

import de.steinacker.jcg.annotation.Mutable;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Mutable
public class MutablePerson {

    //@Property
    public final String firstName;
    { firstName = null; }
    //@Property    
    public String lastName;

    //@Method
    public String getName() {
        return new StringBuilder(firstName)
                .append(" ")
                .append(lastName)
                .toString();
    }

}
