/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.types;

import java.io.Serializable;
import java.util.List;

public interface GenericInterface01<T> extends Serializable {

    public List<T> asList(final T t);

}