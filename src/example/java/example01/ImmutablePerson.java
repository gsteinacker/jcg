/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example01;

import de.steinacker.jcg.annotation.Immutable;

import static de.steinacker.jcg.annotation.Immutable.Style.CONSTRUCTOR;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Immutable(style = CONSTRUCTOR)
public class ImmutablePerson {

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

    //@Method
    public int getAge() {
        return 42;
    }

}