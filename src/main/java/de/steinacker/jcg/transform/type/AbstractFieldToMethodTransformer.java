/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.Field;
import de.steinacker.jcg.model.FieldModifier;
import de.steinacker.jcg.model.Method;
import de.steinacker.jcg.transform.predicate.Predicate;
import de.steinacker.jcg.transform.predicate.TruePredicate;
import org.apache.log4j.Logger;

/**
 * Abstract implementation of a ModelTransformer used to add Methods for every Field of a Type.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public abstract class AbstractFieldToMethodTransformer extends AbstractTypeTransformer implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(AbstractFieldToMethodTransformer.class);

    private Predicate<Field> fieldPredicate = new TruePredicate<Field>();

    public final void setFieldPredicate(final Predicate<Field> fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

    @Override
    public final void visit(final Field field, final Context context) {
        if (fieldPredicate.eval(field) && !field.is(FieldModifier.STATIC)) {
            getTypeBuilder(context).addMethod(transformFieldToMethod(field, context));
        }
    }

    protected abstract Method transformFieldToMethod(final Field field, final Context context);
}
