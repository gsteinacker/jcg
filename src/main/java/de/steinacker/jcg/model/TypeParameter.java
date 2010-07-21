package de.steinacker.jcg.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TypeParameters specify the generic type parameters and the bounded types of a method or type.
 *
 */
public final class TypeParameter {
    @NotNull
    @Valid
    private final QualifiedName paramName;
    @NotNull
    @Valid
    private final List<QualifiedName> boundedTypes;

    public TypeParameter(final QualifiedName paramName) {
        this.paramName = paramName;
        this.boundedTypes = Collections.emptyList();
    }

    public TypeParameter(final QualifiedName paramName, final List<QualifiedName> boundedTypes) {
        this.paramName = paramName;
        final ArrayList<QualifiedName> types = new ArrayList<QualifiedName>(boundedTypes);
        types.remove(QualifiedName.valueOf("java.lang.Object"));
        this.boundedTypes = Collections.unmodifiableList(types);
    }

    /**
     * In case of a typeVar parameter, the getParamName will return a SimpleName, otherwise a QualifiedName is returned.
     * @return the name of the TypeParameter.
     */
    public QualifiedName getParamName() {
        return paramName;
    }

    public List<QualifiedName> getBoundedTypes() {
        return boundedTypes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        // generic name
        sb.append(paramName);
        boolean firstBound = true;
        for (QualifiedName qn : boundedTypes) {
            if (!qn.toString().equals("java.lang.Object")) {
                if (firstBound) {
                    sb.append(" extends ");
                    firstBound = false;
                } else {
                    sb.append(" & ");
                }
                sb.append(qn.getSimpleName());
            }
        }

        return sb.toString();
    }
}
