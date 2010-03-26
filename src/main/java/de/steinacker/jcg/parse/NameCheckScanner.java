/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.EnumSet;
import java.util.List;

import static javax.lang.model.element.ElementKind.*;
import static javax.lang.model.element.Modifier.*;
import static javax.lang.model.type.TypeKind.LONG;
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
class NameCheckScanner extends ElementScanner6<Void, Void> {
    private final Types typeUtils;
    private final Messager messager;

    public NameCheckScanner(final Messager messager, final Types typeUtils) {
        this.messager = messager;
        this.typeUtils = typeUtils;
    }

    /**
     * Check the name of a type and its enclosed elements and
     * type parameters.
     */
    @Override
    public Void visitType(TypeElement e, Void p) {
        scan(e.getTypeParameters(), p); // Check the names of any type parameters
        checkCamelCase(e, true);        // Check the name of the class or interface
        List<? extends AnnotationMirror> annotations = e.getAnnotationMirrors();
        for (final AnnotationMirror annotation : annotations) {
            //System.out.println("Annotation: " + annotation.getAnnotationType().toString());
            for (final ExecutableElement ee : annotation.getElementValues().keySet()) {
                //System.out.println("    simpleName: " + ee.getSimpleName());
                //System.out.println("    value: " + annotation.getElementValues().get(ee).getValue());
            }
        }
        /*
        final Generator generator = new TypeSerializerFactory().getTypeSerializer();
        // TODO: Annotation-Name => Template-Name! Dafür einen TemplateResolver schreiben (.vm anhängen, Config, ...)
        try {
            System.out.println(generator.generate(new GeneratorContext("/templates/type/example.vm", null)));
        } catch (JcgException ex) {
            error(ex.getMessage());
        }
        */
        super.visitType(e, p);          // Check the names of any enclosed elements
        return null;
    }

    /**
     * Check the name of an executable (method, constructor,
     * etc.) and its type parameters.
     */
    @Override
    public Void visitExecutable(ExecutableElement e, Void p) {
        scan(e.getTypeParameters(), p); // Check the names of any type parameters

        // Check the name of the executable
        if (e.getKind() == METHOD) {
            // Make sure that a method does not have the same
            // name as its class or interface.
            Name name = e.getSimpleName();
            if (name.contentEquals(e.getEnclosingElement().getSimpleName()))
                messager.printMessage(WARNING,
                        "A method should not have the same name as its enclosing type, ``" +
                                name + "''.", e);
            checkCamelCase(e, false);
        }
        // else constructors and initializers don't have user-defined names

        // At this point, could use the Tree API,
        // com.sun.source, to examine the names of entities
        // inside a method.


        super.visitExecutable(e, p);
        return null;
    }

    /**
     * Check the name of a field, parameter, etc.
     */
    @Override
    public Void visitVariable(VariableElement e, Void p) {
        if (!checkForSerial(e)) { // serialVersionUID checks
            // Is the variable a constant?
            if (e.getKind() == ENUM_CONSTANT ||
                    e.getConstantValue() != null ||
                    heuristicallyConstant(e))
                checkAllCaps(e); // includes enum constants
            else
                checkCamelCase(e, false);
        }
        // A call to super can be elided with the current language definition.
        // super.visitVariable(e, p);
        return null;
    }

    /**
     * Check the name of a type parameter.
     */
    @Override
    public Void visitTypeParameter(TypeParameterElement e, Void p) {
        checkAllCaps(e);
        // A call to super can be elided with the current language definition.
        // super.visitTypeParameter(e, p);
        return null;
    }

    /**
     * Check the name of a package.
     */
    @Override
    public Void visitPackage(PackageElement e, Void p) {
        /*
        * Implementing the checks of package names is left
        * as an exercise for the reader, see JLSv3 section
        * 7.7 for conventions.
        */

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
        return null;
    }

    @Override
    public Void visitUnknown(Element e, Void p) {
        // This method will be called if a kind of element
        // added after JDK 6 is visited.  Since as of this
        // writing the conventions for such constructs aren't
        // known, issue a warning.
        messager.printMessage(WARNING,
                "Unknown kind of element, " + e.getKind() +
                        ", no name checking performed.", e);
        return null;
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
     * Print a warning if an element's simple name is not in
     * camel case.  If there are two adjacent uppercase
     * characters, the name is considered to violate the
     * camel case naming convention.
     *
     * @param e           the element whose name will be checked
     * @param initialCaps whether or not the first character should be uppercase
     */
    private void checkCamelCase(Element e, boolean initialCaps) {
        String name = e.getSimpleName().toString();
        boolean previousUpper = false;
        boolean conventional = true;

        int firstCodePoint = name.codePointAt(0);

        if (Character.isUpperCase(firstCodePoint)) {
            previousUpper = true;
            if (!initialCaps) {
                warning("Name, ``" + name + "'', should start in lowercase.", e);
                return;
            }
        } else if (Character.isLowerCase(firstCodePoint)) {
            if (initialCaps) {
                warning("Name, ``" + name + "'', should start in uppercase.", e);
                return;
            }
        } else // underscore, etc.
            conventional = false;

        if (conventional) {
            int cp = firstCodePoint;
            for (int i = Character.charCount(cp);
                 i < name.length();
                 i += Character.charCount(cp)) {
                cp = name.codePointAt(i);
                if (Character.isUpperCase(cp)) {
                    if (previousUpper) {
                        conventional = false;
                        break;
                    }
                    previousUpper = true;
                } else
                    previousUpper = false;
            }
        }

        if (!conventional)
            warning("Name, ``" + name + "'', should be in camel case.", e);
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

}
