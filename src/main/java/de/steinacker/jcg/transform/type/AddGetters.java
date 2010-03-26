/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.NameUtil;
import org.apache.log4j.Logger;

/**
 * A ModelTransformer which adds getter methods for all non-static attributes of the different types.
 * <p/>
 * This Transformer optionally supports predicates to decide, whether a type or a field
 * shall become a getter.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddGetters extends AbstractFieldToMethodTransformer implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(AddGetters.class);

    @Override
    public String getName() {
        return "AddGetters";
    }

    @Override
    protected Method transformFieldToMethod(final Field field, final Context context) {
        final MethodBuilder mb = new MethodBuilder();
        // Alle Getter sind public:
        mb.addModifier(MethodModifier.PUBLIC);
        // Wenn die Klasse final ist, m�ssen es die Methoden nicht sein:
        if (!getType(context).getModifiers().contains(TypeModifier.FINAL))
            mb.addModifier(MethodModifier.FINAL);
        // Der Name der Methode:
        final String fieldName = field.getName().toString();
        mb.setName(new SimpleName("get" + NameUtil.toCamelHumpName(fieldName, true)));
        // Der Return-Type der Methode:
        mb.setReturnTypeName(field.getTypeName());
        /*
        // TODO Der Sourcecode:
        mb.setBody(CodeUtil.indent("return " + field.getName() + ";"));
        // TODO Kommentar nicht vergessen:
        final StringBuilder sb = new StringBuilder();
        sb.append(field.getComment());
        sb.append("\n").append("@return ").append(field.getName());
        mb.setComment(CodeUtil.toJavaDocComment(sb.toString()));
        */
        return mb.toMethod();
    }

    @Override
    public String toString() {
        return getName();
    }

}