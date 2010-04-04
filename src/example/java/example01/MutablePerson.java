/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example01;

import de.steinacker.jcg.annotation.Method;
import de.steinacker.jcg.annotation.Mutable;
import de.steinacker.jcg.annotation.Property;

import javax.validation.constraints.Pattern;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Mutable
public class MutablePerson {

    //@Property
    public String firstName;

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
