/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.model.*;
import de.steinacker.jcg.util.DefaultFormatStringProvider;
import de.steinacker.jcg.util.FormatStringProvider;
import de.steinacker.jcg.util.NameUtil;

/**
 * A TypeTransformer which adds getter methods for all non-static attributes of the different types.
 * <p/>
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class AddGetters extends AbstractFieldToMethodTransformer implements TypeTransformer {

    private FormatStringProvider formatStringProvider = new DefaultFormatStringProvider();

    /**
     * Inject a FormatStringProvider implementation used to generate method bodies for setters,
     * getters and constructors.
     *
     * @param provider the FormatStringProvider
     */
    public void setFormatStringProvider(final FormatStringProvider provider) {
        this.formatStringProvider = provider;
    }

    @Override
    public String getName() {
        return "AddGetters";
    }

    @Override
    protected Method transformFieldToMethod(final Field field, final Context context) {
        // no getters for static fields:
        if (field.is(FieldModifier.STATIC))
            return null;

        final MethodBuilder mb = new MethodBuilder();
        // Alle Getter sind public:
        mb.addModifier(MethodModifier.PUBLIC);
        // Wenn die Klasse final ist, m�ssen es die Methoden nicht sein:
        if (!getType(context).getModifiers().contains(TypeModifier.FINAL))
            mb.addModifier(MethodModifier.FINAL);
        // Der Name der Methode:
        final String fieldName = field.getName().toString();
        mb.setName(SimpleName.valueOf("get" + NameUtil.toCamelHumpName(fieldName, true)));
        // Der Return-Type der Methode:
        mb.setReturnType(field.getType());
        final String formatString = formatStringProvider.getFormatForGetter(field.getType().getQualifiedName());
        mb.setMethodBody(String.format(formatString, field.getName()));
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