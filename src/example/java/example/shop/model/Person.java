/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package example.shop.model;

import javax.validation.constraints.NotNull;

/**
 * @author Guido Steinacker
 * @since 26.07.2010
 */
public class Person {
    @NotNull
    String firstName;
    @NotNull
    String lastName;
}
