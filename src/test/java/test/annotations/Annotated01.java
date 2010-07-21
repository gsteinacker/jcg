/**
 * Copyright (c) 2010 by Guido Steinacker
 */

package test.annotations;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/* This is only an example for different kinds of annotations. The
 * annotations do not make sense at all!
 */
@SuppressWarnings("foo")
public class Annotated01 {

    @NotNull
    String mostSimpleAnnotedField;

    @NotNull (groups=String.class)
    String fFieldWithClassTypeAnnotationParam;

    @Size(min = 5, max=8)
    String fieldWithAnnotationParams;

    @NotNull (groups={String.class, Integer.class})
    String fieldAnnotatedWithMultipleValues;

    @Annotation03(bar=@Annotation02(fooBar="42"))
    String fieldAnnotatedWithAnnotationParam;

    @SuppressWarnings("foo")
    void annotatedMethod() {
    }

}