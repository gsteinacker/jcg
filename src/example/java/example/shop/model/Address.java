/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package example.shop.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Guido Steinacker
 * @since 28.07.2010
 */
public class Address {
    @NotEmpty
    String street;
    @NotEmpty
    String nr;
    @NotNull
    @Min(4)
    String zip;
    @NotEmpty
    String city;
    @Pattern(regexp = "[A-Z]{2}")
    String countryCode;
}
