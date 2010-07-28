/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package example.shop.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author Guido Steinacker
 * @since 26.07.2010
 */
public class Customer {
    @NotNull
    @Pattern (regexp = "[1-9][0-9]{8}")
    String customerNr;
    @NotNull
    @Valid
    Person person;
    @NotNull
    @Valid
    List<Address> deliveryAddresses;
    @NotNull
    @Valid
    Address invoiceAddress;
}
