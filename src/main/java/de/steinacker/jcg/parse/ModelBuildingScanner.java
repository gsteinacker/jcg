/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import de.steinacker.jcg.model.*;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.element.Modifier.*;
import static javax.lang.model.type.TypeKind.LONG;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * Visitor to implement name checks.
 * The visitor could be enhanced to return true/false if
 * there were warnings reported or a count of the number
 * of warnings.  This could be facilitated by using
 * Boolean or Integer instead of Void for the actual type
 * arguments.  In more detail, one way to tally the number
 * of warnings would be for each method to return the sum
 * of the warnings it and the methods it called issued, a
 * bottom-up computation.  In that case, the first type
 * argument would be Integer and the second type argument
 * would still be Void.  Alternatively, the current count
 * could be passed along in Integer parameter p and each
 * method could return the Integer sum of p and the
 * warnings the method issued.  Some computations are more
 * naturally expressed in one form instead of the other.
 * If greater control is needed over traversal order, a
 * SimpleElementVisitor can be extended instead of an
 * ElementScanner.
 */
final class ModelBuildingScanner extends ElementScanner6<TypeBuilder, TypeBuilder> {

    private final Types typeUtils;
    private final Messager messager;
    private final Trees trees;

    public ModelBuildingScanner(final ProcessingEnvironment processingEnv) {
        this.trees = Trees.instance(processingEnv);
        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();

    }

    /**
     * Check the name of a type and its enclosed elements and
     * type parameters.
     */
    @Override
    public TypeBuilder visitType(TypeElement e, TypeBuilder typeBuilder) {
        // Get the source code of the type:
        final TreePath treePath = trees.getPath(e);
        final SourcePositions sourcePosition = trees.getSourcePositions();
        final CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
        final JavaFileObject file = compilationUnit.getSourceFile();
        final String sourceCode;
        try {
            sourceCode = file.getCharContent(false).toString();
            //System.out.println("Source:\n" + sourceCode);

        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new IllegalStateException(e1);
        }

        final Tree tree = treePath.getLeaf();
        final int startPos = (int) sourcePosition.getStartPosition(compilationUnit, tree);
        //final long endPos = sourcePosition.getEndPosition(compilationUnit, tree);
        final int endPos = sourceCode.indexOf("{", startPos);
        //System.out.println(sourceCode.subSequence(startPos, endPos));


        // handle generic types:
        if (e.getTypeParameters().size() > 0)
            throw new UnsupportedOperationException("Generic types are not yet implemented.");
        scan(e.getTypeParameters(), typeBuilder);

        // class name:
        typeBuilder.setName(QualifiedName.valueOf(e.getQualifiedName()));

        // class or interface?
        typeBuilder.setIsInterface(e.getKind().isInterface());

        // modifiers:
        typeBuilder.setModifiers(mapToTypeModifiers(e.getModifiers()));

        // annotations:
        List<Annotation> annotations = mapToAnnotations(e.getAnnotationMirrors());
        typeBuilder.setAnnotations(annotations);

        // comment:
        final String comment = "This is my comment.";
        typeBuilder.setComment(comment);
        // parent class:
        typeBuilder.setNameOfSuperClass(QualifiedName.valueOf(e.getSuperclass().toString()));

        // interfaces:
        final List<QualifiedName> interfaces = new ArrayList<QualifiedName>(e.getInterfaces().size());
        for (final TypeMirror typeMirror : e.getInterfaces()) {
            interfaces.add(QualifiedName.valueOf(typeMirror.toString()));
        }
        typeBuilder.setNameOfInterfaces(interfaces);

        super.visitType(e, typeBuilder);          // Check the names of any enclosed elements
        return typeBuilder;
    }

    /**
     * Check the name of an executable (method, constructor,
     * etc.) and its type parameters.
     */
    @Override
    public TypeBuilder visitExecutable(ExecutableElement e, TypeBuilder typeBuilder) {
        scan(e.getTypeParameters(), typeBuilder); // Check the names of any type parameters

        // Check the name of the executable
        if (e.getKind() == METHOD) {
            // Make sure that a method does not have the same
            // name as its class or interface.
            Name name = e.getSimpleName();
            if (name.contentEquals(e.getEnclosingElement().getSimpleName()))
                messager.printMessage(ERROR,
                        "A method should not have the same name as its enclosing type, ``" +
                                name + "''.", e);
            final SimpleName methodName = new SimpleName(name.toString());
            final QualifiedName returnType = QualifiedName.valueOf(e.getReturnType().toString());
            final List<Annotation> annotations = mapToAnnotations(e.getAnnotationMirrors());
            final List<QualifiedName> exceptions = new ArrayList<QualifiedName>(e.getThrownTypes().size());
            for (TypeMirror typeMirror : e.getThrownTypes()) {
                exceptions.add(QualifiedName.valueOf(typeMirror.toString()));
            }
            final List<Parameter> parameters = new ArrayList<Parameter>(e.getParameters().size());
            for (VariableElement variableElement : e.getParameters()) {
                // TODO: variableElement.getConstantValue();
                final String comment = "TODO!";
                final Parameter param = new ParameterBuilder()
                        .setTypeName(QualifiedName.valueOf(variableElement.asType().toString()))
                        .setName(new SimpleName(variableElement.getSimpleName()))
                        .setAnnotations(mapToAnnotations(variableElement.getAnnotationMirrors()))
                        .setFinal(variableElement.getModifiers().contains(Modifier.FINAL))
                        .setComment(comment)
                        .toParameter();
                parameters.add(param);
            }
            // TODO: e.getTypeParameters();
            final EnumSet<MethodModifier> modifiers = mapToMethodModifiers(e);
            final Method method = new MethodBuilder()
                    .setName(methodName)
                    .setAnnotations(annotations)
                    .setModifiers(modifiers)
                    .setExceptions(exceptions)
                    .setReturnTypeName(returnType)
                    .setParameters(parameters)
                    .toMethod();
            typeBuilder.addMethod(method);
        } else if (e.getKind() == CONSTRUCTOR) {
            // TODO: Konstruktoren
        }

        // At this point, could use the Tree API,
        // com.sun.source, to examine the names of entities
        // inside a method.

        super.visitExecutable(e, typeBuilder);
        return typeBuilder;
    }


    /**
     * Check the name of a field, parameter, etc.
     */
    @Override
    public TypeBuilder visitVariable(VariableElement e, TypeBuilder typeBuilder) {
        if (!checkForSerial(e)) { // serialVersionUID checks
            // Is the variable a constant?
            if (e.getKind() == ENUM_CONSTANT ||
                    e.getConstantValue() != null ||
                    heuristicallyConstant(e)) {
                checkAllCaps(e); // includes enum constants                
            } else if (e.getKind() == FIELD) {
                final SimpleName name = new SimpleName(e.getSimpleName().toString());
                final QualifiedName typeName = QualifiedName.valueOf(e.asType().toString());
                final List<Annotation> annotations = mapToAnnotations(e.getAnnotationMirrors());
                final EnumSet<FieldModifier> modifiers = mapToFieldModifiers(e);
                final String comment = "TODO!";
                typeBuilder.addField(new FieldBuilder().setName(name).setTypeName(typeName).setAnnotations(annotations).setModifiers(modifiers).setComment(comment).toField());
            }
        }
        // A call to super can be elided with the current language definition.
        // super.visitVariable(e, p);
        return typeBuilder;
    }

    /**
     * Check the name of a type parameter.
     */
    @Override
    public TypeBuilder visitTypeParameter(TypeParameterElement e, TypeBuilder typeBuilder) {
        checkAllCaps(e);
        // A call to super can be elided with the current language definition.
        // super.visitTypeParameter(e, p);
        return typeBuilder;
    }

    /**
     * Check the name of a package.
     */
    @Override
    public TypeBuilder visitPackage(PackageElement e, TypeBuilder typeBuilder) {
        // Whether or not this method should call
        // super.visitPackage, to visit the packages enclosed
        // elements, is a design decision based on what a
        // PackageElemement is used to mean in this context.
        // A PackageElement can represent a whole package, so
        // it can provide a concise way to indicate many
        // user-defined types should be visited.  However, a
        // PackageElement can also represent a
        // package-info.java file, as would be in the case if
        // the PackageElement came from
        // RoundEnvironment.getRootElements.  In that case,
        // the package-info file and other files in that
        // package could be passed in.  Therefore, without
        // further checks, types in a package could be visited
        // more than once if a package's elements were visited
        // too.
        return typeBuilder;
    }


    @Override
    public TypeBuilder visitUnknown(Element e, TypeBuilder typeBuilder) {
        // This method will be called if a kind of element
        // added after JDK 6 is visited.  Since as of this
        // writing the conventions for such constructs aren't
        // known, issue a warning.
        messager.printMessage(WARNING,
                "Unknown kind of element, " + e.getKind() +
                        ", no name checking performed.", e);
        return typeBuilder;
    }

    // All the name checking methods assume the examined names
    // are syntactically well-formed identifiers.

    /**
     * Return {@code true} if this variable is a field named
     * "serialVersionUID"; false otherwise.  A true
     * serialVersionUID of a class has type {@code long} and
     * is static and final.
     * <p/>
     * <p>To check that a Serializable class defines a proper
     * serialVersionUID, run javac with -Xlint:serial.
     *
     * @return true if this variable is a serialVersionUID field and false otherwise
     */
    private boolean checkForSerial(VariableElement e) {
        // If a field is named "serialVersionUID" ...
        if (e.getKind() == FIELD &&
                e.getSimpleName().contentEquals("serialVersionUID")) {
            // ... issue a warning if it does not act as a serialVersionUID
            if (!(e.getModifiers().containsAll(EnumSet.of(STATIC, FINAL)) &&
                    typeUtils.isSameType(e.asType(), typeUtils.getPrimitiveType(LONG)) &&
                    e.getEnclosingElement().getKind() == CLASS)) // could check that class implements Serializable
                messager.printMessage(WARNING,
                        "Field named ``serialVersionUID'' is not acting as such.", e);
            return true;
        }
        return false;
    }

    /**
     * Using heuristics, return {@code true} is the variable
     * should follow the naming conventions for constants and
     * {@code false} otherwise.  For example, the public
     * static final fields ZERO, ONE, and TEN in
     * java.math.BigDecimal are logically constants (and named
     * as constants) even though BigDecimal values are not
     * regarded as constants by the language specification.
     * However, some final fields may not act as constants
     * since the field may be a reference to a mutable object.
     * <p/>
     * <p> These heuristics could be tweaked to provide better
     * fidelity.
     *
     * @return true if the current heuristics regard the
     *         variable as a constant and false otherwise.
     */
    private boolean heuristicallyConstant(VariableElement e) {
        // Fields declared in interfaces are logically
        // constants, JLSv3 section 9.3.
        if (e.getEnclosingElement().getKind() == INTERFACE)
            return true;
        else if (e.getKind() == FIELD &&
                e.getModifiers().containsAll(EnumSet.of(PUBLIC, STATIC, FINAL)))
            return true;
        else {
            // A parameter declared final should not be named like
            // a constant, neither should exception parameters.
            return false;
        }
    }

    /**
     * Print a warning if the element's name is not a sequence
     * of uppercase letters separated by underscores ("_").
     *
     * @param e the element whose name will be checked
     */
    private void checkAllCaps(Element e) {
        String name = e.getSimpleName().toString();
        if (e.getKind() == TYPE_PARAMETER) { // Should be one character
            if (name.codePointCount(0, name.length()) > 1 ||
                    // Assume names are non-empty
                    !Character.isUpperCase(name.codePointAt(0)))
                warning("A type variable's name,``" + name + "'', should be a single uppercace character.", e);
        } else {
            boolean conventional = true;
            int firstCodePoint = name.codePointAt(0);

            // Starting with an underscore is not conventional
            if (!Character.isUpperCase(firstCodePoint))
                conventional = false;
            else {
                // Was the previous character an underscore?
                boolean previousUnderscore = false;
                int cp = firstCodePoint;
                for (int i = Character.charCount(cp);
                     i < name.length();
                     i += Character.charCount(cp)) {
                    cp = name.codePointAt(i);
                    if (cp == (int) '_') {
                        if (previousUnderscore) {
                            conventional = false;
                            break;
                        }
                        previousUnderscore = true;
                    } else {
                        previousUnderscore = false;
                        if (!Character.isUpperCase(cp) && !Character.isDigit(cp)) {
                            conventional = false;
                            break;
                        }
                    }
                }
            }

            if (!conventional)
                error("A constant's name, ``" + name + "'', should be ALL_CAPS.", e);
        }
    }

    private void warning(final CharSequence message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message);
    }

    private void warning(final CharSequence message, final Element positionHint) {
        messager.printMessage(Diagnostic.Kind.WARNING, message, positionHint);
    }

    private void error(final CharSequence message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void error(final CharSequence message, final Element positionHint) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, positionHint);
    }

    private static List<Annotation> mapToAnnotations(List<? extends AnnotationMirror> annotationMirrors) {
        List<Annotation> annotations = new ArrayList<Annotation>(annotationMirrors.size());
        for (final AnnotationMirror annotationMirror : annotationMirrors) {
            annotations.add(new Annotation(QualifiedName.valueOf(annotationMirror.getAnnotationType().toString())));
            // TODO: annotation values
            for (final ExecutableElement ee : annotationMirror.getElementValues().keySet()) {
                //System.out.println("    simpleName: " + ee.getSimpleName());
                //System.out.println("    value: " + annotationMirror.getElementValues().get(ee).getValue());
            }
        }
        return annotations;
    }

    private static EnumSet<MethodModifier> mapToMethodModifiers(ExecutableElement e) {
        final EnumSet<MethodModifier> modifiers = EnumSet.noneOf(MethodModifier.class);
        for (final Modifier modifier : e.getModifiers()) {
            modifiers.add(MethodModifier.valueOf(modifier.name()));
        }
        return modifiers;
    }

    private static EnumSet<FieldModifier> mapToFieldModifiers(VariableElement e) {
        final EnumSet<FieldModifier> modifiers = EnumSet.noneOf(FieldModifier.class);
        for (final Modifier modifier : e.getModifiers()) {
            modifiers.add(FieldModifier.valueOf(modifier.name()));
        }
        return modifiers;
    }

    private EnumSet<TypeModifier> mapToTypeModifiers(final Set<Modifier> modifiers) {
        final EnumSet<TypeModifier> result = EnumSet.noneOf(TypeModifier.class);
        for (final Modifier modifier : modifiers) {
            result.add(TypeModifier.valueOf(modifier.name()));
        }
        return result;
    }

}