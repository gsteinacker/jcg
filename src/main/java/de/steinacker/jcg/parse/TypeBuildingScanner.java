/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import de.steinacker.jcg.model.*;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

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
final class TypeBuildingScanner extends ElementScanner6<TypeBuilder, TypeBuilder> {

    private final Types typeUtils;
    private final Messager messager;
    private final Trees trees;
    private final Elements elementUtils;

    public TypeBuildingScanner(final ProcessingEnvironment processingEnv) {
        this.trees = Trees.instance(processingEnv);
        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
    }

    /**
     * Check the name of a type and its enclosed elements and
     * type parameters.
     */
    @Override
    public TypeBuilder visitType(TypeElement e, TypeBuilder typeBuilder) {
        /*
        // Get the source code of the type:
        final TreePath treePath = trees.getPath(e);
        final SourcePositions sourcePosition = trees.getSourcePositions();
        final CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
        final Tree tree = treePath.getLeaf();
        final JavaFileObject file = compilationUnit.getSourceFile();
        final String sourceCode;
        try {
            sourceCode = file.getCharContent(false).toString();
            //System.out.println("Source:\n" + sourceCode);

        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new IllegalStateException(e1);
        }
        final int startPos = (int) sourcePosition.getStartPosition(compilationUnit, tree);
        //final long endPos = sourcePosition.getEndPosition(compilationUnit, tree);
        //final int endPos = sourceCode.indexOf("{", startPos);
        //System.out.println(sourceCode.subSequence(startPos, endPos));
        */

        final TreePath treePath = trees.getPath(e);
        final CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
        final List<? extends ImportTree> importTrees = compilationUnit.getImports();
        for (ImportTree importTree : importTrees) {
            typeBuilder.addImport(QualifiedName.valueOf(importTree.getQualifiedIdentifier().toString()));
            if (importTree.isStatic())
                throw new IllegalStateException("static imports are not yet supported by the scanner.");
        }
        
        // handle generic types:
        scan(e.getTypeParameters(), typeBuilder);

        // class name:
        typeBuilder.setName(QualifiedName.valueOf(e.getQualifiedName()));

        // type parameters
        typeBuilder.setTypeParameters(mapToTypeParameters(e.getTypeParameters()));

        // modifiers:
        final EnumSet<TypeModifier> typeModifiers = mapToTypeModifiers(e.getModifiers());

        // class or interface?
        switch (e.getKind()) {
            case ANNOTATION_TYPE:
                throw new UnsupportedOperationException("Annotations are not yet implemented.");
            case CLASS:
                typeBuilder.setKind(Type.Kind.CLASS);
                break;
            case ENUM:
                throw new UnsupportedOperationException("Enumerations are not yet implemented.");
                /*typeBuilder.setKind(Type.Kind.ENUM);
                break;*/
            case INTERFACE:
                typeBuilder.setKind(Type.Kind.INTERFACE);
                typeModifiers.remove(TypeModifier.ABSTRACT);
                typeModifiers.remove(TypeModifier.FINAL);
                break;
        }

        // set modifiers:
        typeBuilder.setModifiers(typeModifiers);

        // annotations:
        final List<Annotation> annotations = mapToAnnotations(e.getAnnotationMirrors());
        typeBuilder.setAnnotations(annotations);

        // TODO comment:
        //final String comment = "This is my comment.";
        typeBuilder.setComment("");

        // parent class:
        final Tree tree = trees.getPath(e).getLeaf();
        final JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl)tree;

        // super class:
        if (e.getSuperclass() instanceof NoType) {
            typeBuilder.setSuperClass(null);
        } else {
            // final QualifiedName superClassQN = QualifiedName.valueOf(e.getSuperclass().toString());
            if (jcClassDecl.extending != null) {
                final JCTree.JCExpression expression = (JCTree.JCExpression) jcClassDecl.extending;
                final TypeSymbol superClass = mapToTypeSymbol(expression.type, expression.getKind());
                typeBuilder.setSuperClass(superClass);
            }
        }

        // interfaces:
        /*final List<QualifiedName> interfaces = new ArrayList<QualifiedName>(e.getInterfaces().size());
        for (final TypeMirror typeMirror : e.getInterfaces()) {
            interfaces.add(QualifiedName.valueOf(typeMirror.toString()));
        }*/
        //typeBuilder.setImplementedInterfaces(interfaces);
        for (JCTree.JCExpression jcExpression : jcClassDecl.implementing) {
            final TypeSymbol implementedInterface = mapToTypeSymbol(jcExpression.type, jcExpression.getKind());
            typeBuilder.addImplementedInterface(implementedInterface);
        }

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
        switch(e.getKind()) {
            case METHOD:
            case CONSTRUCTOR:
                final Method method = mapToMethod(e);
                typeBuilder.addMethod(method);
                break;
            default:
                throw new UnsupportedOperationException("Don't know how to parse a " + e.getKind().name());
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
            final ElementKind kind = e.getKind();
            if (kind == ENUM_CONSTANT || e.getConstantValue() != null || heuristicallyConstant(e)) {
                checkAllCaps(e); // includes enum constants
            }
            if (e.getKind() == FIELD) {
                final SimpleName name = SimpleName.valueOf(e.getSimpleName().toString());
                final List<Annotation> annotations = mapToAnnotations(e.getAnnotationMirrors());
                final EnumSet<FieldModifier> modifiers = mapToFieldModifiers(e);
                final String comment = "TODO!";
                // TODO final Object constantValue = e.getConstantValue();
                final TreePath treePath = trees.getPath(e);
                final Tree tree = treePath.getLeaf();
                final JCTree.JCVariableDecl jcTree = (JCTree.JCVariableDecl)tree;
                final TypeSymbol fieldType = mapToTypeSymbol(jcTree.vartype.type, jcTree.vartype.getKind());
                final JCTree.JCExpression initializerExpression = jcTree.getInitializer();
                final String initString = (initializerExpression != null) ? initializerExpression.toString() : null;
                typeBuilder.addField(new FieldBuilder()
                        .setName(name)
                        .setType(fieldType)
                        .setInitString(initString)
                        .setAnnotations(annotations)
                        .setModifiers(modifiers)
                        .setComment(comment)
                        .toField());
            }
        }
        // A call to super can be elided with the current language definition.
        // super.visitVariable(e, typeBuilder);
        return typeBuilder;
    }

    /**
     * Check the name of a type parameter.
     */
    @Override
    public TypeBuilder visitTypeParameter(TypeParameterElement e, TypeBuilder typeBuilder) {
        checkAllCaps(e);

        final QualifiedName paramName = QualifiedName.valueOf(e.getSimpleName().toString());
        final List<QualifiedName> boundedTypes = new ArrayList<QualifiedName>();
        for (TypeMirror bound : e.getBounds()) {
            boundedTypes.add(QualifiedName.valueOf(bound.toString()));
        }
        // A call to super can be elided with the current language definition.
        // super.visitTypeParameter(e, p);
        return typeBuilder.addTypeParameter(new TypeParameter(paramName, boundedTypes));
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
        throw new UnsupportedOperationException("package-info.java is not yet supported.");
        //return typeBuilder; //TODO: annotierte packages / package-info.java
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
     * @param e the VariableElement
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

    private List<Annotation> mapToAnnotations(List<? extends AnnotationMirror> javaxAnnotationMirrors) {
        final List<Annotation> annotations = new ArrayList<Annotation>(javaxAnnotationMirrors.size());
        for (final AnnotationMirror javaxAnnotationMirror : javaxAnnotationMirrors) {
            annotations.add(mapToAnnotation(javaxAnnotationMirror));
        }
        return annotations;
    }

    private Annotation mapToAnnotation(final AnnotationMirror javaxAnnotationMirror) {
        // 1. Name of the Annotation:
        final QualifiedName annotationName = QualifiedName.valueOf(javaxAnnotationMirror.getAnnotationType().toString());
        // 2. All the parameters (excluding default parameters) of the Annotation:
        final List<AnnotationParameter> parameters = mapToAnnotationParameters(javaxAnnotationMirror, false);
        // 3. All the parameters (including default parameters) of the Annotation:
        final List<AnnotationParameter> defaults = mapToAnnotationParameters(javaxAnnotationMirror, true);

        return new Annotation(annotationName, parameters, defaults);
    }

    /**
     * Maps the parameters of an AnnotationMirror to a list of AnnotationParameter instances.
     * Depending on <code>withDefaults</code>, this list contains default parameters, or not.
     * @param javaxAnnotationMirror the javax.lang.model.AnnotationMirror.
     * @param withDefaults specifies if default parameters should be included or not.
     * @return List<AnnotationParameter>
     */
    private List<AnnotationParameter> mapToAnnotationParameters(final AnnotationMirror javaxAnnotationMirror,
                                                                final boolean withDefaults) {
        final List<AnnotationParameter> parameters = new ArrayList<AnnotationParameter>();
        // The Map containing all javax parameters of the annotation:
        final Map<? extends ExecutableElement, ? extends AnnotationValue> javaxAnnotationParameters;
        if (withDefaults)
            javaxAnnotationParameters = elementUtils.getElementValuesWithDefaults(javaxAnnotationMirror);
        else
            javaxAnnotationParameters = javaxAnnotationMirror.getElementValues();

        // Now we have to map all the parameters + values:
        for (final ExecutableElement javaxAnnotationParameter : javaxAnnotationParameters.keySet()) {
            // The name of the parameter:
            final String paramName = javaxAnnotationParameter.getSimpleName().toString();
            final AnnotationParameter parameter;

            // the javaxAnnotationValue has two important informations: the Object javaxParameterValue, and a String
            // representation of the javaxParameterValue, that we can use to generate source code:
            final AnnotationValue javaxAnnotationValue = javaxAnnotationParameters.get(javaxAnnotationParameter);
            // recursively map the javax AnnotationValue to an AnnotationValue. The recursion is
            // necessary, so parameter values can be a list of values (groups={String.class, Integer.class})
            final List<de.steinacker.jcg.model.AnnotationValue> annotationValues = mapToAnnotationValues(javaxAnnotationValue);
            parameter = new AnnotationParameter(paramName, false, annotationValues);
            parameters.add(parameter);
        }
        return parameters;
    }

    /**
     * Maps a javax.lang.model.AnnotationValue to an AnnotationValue.
     *
     * @param javaxAnnotationValue the javaxAnnotationValue
     * @return AnnotationValue
     */
    private List<de.steinacker.jcg.model.AnnotationValue> mapToAnnotationValues(final AnnotationValue javaxAnnotationValue) {
        final Object javaxParameterValue = javaxAnnotationValue.getValue();
        final List<de.steinacker.jcg.model.AnnotationValue> annotationValues
                = new ArrayList<de.steinacker.jcg.model.AnnotationValue>();
        if (javaxParameterValue instanceof List) {
            // javaxParameterValue is a List<? extends AnnotationValue>
            final List<AnnotationValue> l = (List<AnnotationValue>) javaxParameterValue;
            for (AnnotationValue o : l) {
                // recursively map the single value to an AnnotationValue:
                annotationValues.addAll(mapToAnnotationValues(o));
            }
        } else if (javaxParameterValue instanceof AnnotationMirror) {
            // javaxParameterValue is an AnnotationMirror - recursively create an Annotation:
            final Annotation annotation = mapToAnnotation((AnnotationMirror) javaxParameterValue);
            annotationValues.add(new de.steinacker.jcg.model.AnnotationValue(annotation, javaxAnnotationValue.toString()));
        } else if (javaxParameterValue instanceof VariableElement) {
            // javaxParameterValue is a VariableElement (representing an enum constant):
            final VariableElement javaxEnumConstant = (VariableElement)javaxParameterValue;
            final Object constantValue = javaxEnumConstant.getConstantValue();
            annotationValues.add(new de.steinacker.jcg.model.AnnotationValue(constantValue, javaxAnnotationValue.toString()));
        } else if (javaxParameterValue instanceof TypeMirror) {
            // javaxParameterValue is a TypeMirror:
            final TypeMirror javaxTypeMirror = (TypeMirror)javaxParameterValue;
            // TODO: TypeMirror!
            final QualifiedName typeName = QualifiedName.valueOf(javaxTypeMirror.toString());
            annotationValues.add(new de.steinacker.jcg.model.AnnotationValue(typeName, javaxAnnotationValue.toString()));
        } else {
            // javaxParameterValue is a wrapper class (such as Integer) for a primitive type:
            annotationValues.add(new de.steinacker.jcg.model.AnnotationValue(javaxParameterValue, javaxAnnotationValue.toString()));

            //System.out.println("    simpleName: " + javaxAnnotationParameter.getSimpleName());
            //System.out.println("    javaxParameterValue: " + javaxAnnotationMirror.getElementValues().get(javaxAnnotationParameter).getValue());
        }
        return annotationValues;
    }

    private EnumSet<FieldModifier> mapToFieldModifiers(VariableElement e) {
        final EnumSet<FieldModifier> modifiers = EnumSet.noneOf(FieldModifier.class);
        for (final Modifier modifier : e.getModifiers()) {
            modifiers.add(FieldModifier.valueOf(modifier.name()));
        }
        return modifiers;
    }

    private Method mapToMethod(final ExecutableElement e) {
        if (e.getKind() != ElementKind.METHOD && e.getKind() != ElementKind.CONSTRUCTOR) {
            throw new IllegalArgumentException("ExecutableElement is neither a METHOD nor a CONSTRUCTOR");
        }

        final MethodBuilder methodBuilder = new MethodBuilder();

        final Tree tree = trees.getPath(e).getLeaf();
        final JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl)tree;
        //final SourcePositions sourcePosition = trees.getSourcePositions();
        //final CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
        //System.out.println("SourcePosition" + sourcePosition.getStartPosition(compilationUnit, tree));

        // METHOD or CONSTRUCTOR?
        if (e.getKind() == METHOD) {
            // method name:
            final String methodName = e.getSimpleName().toString();
            methodBuilder.setName(SimpleName.valueOf(methodName));
            // kind of method:
            methodBuilder.setKind(Method.Kind.METHOD);
            // return type:;
            final com.sun.tools.javac.code.Type type = jcMethodDecl.restype.type;
            final Symbol.TypeSymbol typeSymbol = type.tsym;
            final QualifiedName returnType = QualifiedName.valueOf(typeSymbol.getQualifiedName().toString());
            final List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
            for (com.sun.tools.javac.code.Type typeParam : type.getTypeArguments()) {
                typeParameters.add(new TypeParameter(QualifiedName.valueOf(typeParam.toString())));
            }
            methodBuilder.setReturnType(new TypeSymbol(returnType, typeParameters));
            // method body:
            final JCTree.JCBlock block = jcMethodDecl.getBody();
            if (block != null) {
                final String b = block.toString();
                methodBuilder.setMethodBody(b.substring(b.indexOf('{')+1, b.lastIndexOf('}')).trim());
            }
        } else {
            // method name:
            final String methodName = e.getEnclosingElement().getSimpleName().toString();
            methodBuilder.setName(SimpleName.valueOf(methodName));
            // kind of method:
            methodBuilder.setKind(Method.Kind.CONSTRUCTOR);
            // return type:
            methodBuilder.setReturnType(null);
        }

        // TypeParameter der Methode: <T> void printHello(T t) => "<T>"
        methodBuilder.setTypeParameters(mapToTypeParameters(e.getTypeParameters()));

        // annotations:
        methodBuilder.setAnnotations(mapToAnnotations(e.getAnnotationMirrors()));

        // exceptions:
        final ArrayList<QualifiedName> exceptions = new ArrayList<QualifiedName>(e.getThrownTypes().size());
        for (TypeMirror typeMirror : e.getThrownTypes()) {
            exceptions.add(QualifiedName.valueOf(typeMirror.toString()));
        }
        methodBuilder.setExceptions(exceptions);

        // parameters:
        final List<Parameter> parameters = new ArrayList<Parameter>(e.getParameters().size());
        for (VariableElement variableElement : e.getParameters()) {
            // TODO: variableElement.getConstantValue();
            // TODO: final String comment = "TODO!";
            final Tree paramTree = trees.getPath(variableElement).getLeaf();
            final JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) paramTree;
            final TypeSymbol paramTypeSymbol = mapToTypeSymbol(variableDecl.vartype.type, variableDecl.vartype.getKind());
            //final JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl)tree;
            final Parameter param = new ParameterBuilder()
                    //.setType(QualifiedName.valueOf(variableElement.asType().toString()))
                    .setType(paramTypeSymbol)
                    .setName(SimpleName.valueOf(variableElement.getSimpleName()))
                    .setAnnotations(mapToAnnotations(variableElement.getAnnotationMirrors()))
                    .setFinal(variableElement.getModifiers().contains(Modifier.FINAL))
                    .setComment("")
                    .toParameter();
            parameters.add(param);
        }
        methodBuilder.setParameters(parameters);

        // modifiers:
        methodBuilder.setModifiers(mapToMethodModifiers(e));

        return methodBuilder.toMethod();
    }

    private EnumSet<MethodModifier> mapToMethodModifiers(final ExecutableElement e) {
        final EnumSet<MethodModifier> modifiers = EnumSet.noneOf(MethodModifier.class);
        for (final Modifier modifier : e.getModifiers()) {
            modifiers.add(MethodModifier.valueOf(modifier.name()));
        }
        if (e.getEnclosingElement().getKind() == ElementKind.INTERFACE)
            modifiers.remove(MethodModifier.ABSTRACT);
        return modifiers;
    }

    private EnumSet<TypeModifier> mapToTypeModifiers(final Set<Modifier> modifiers) {
        final EnumSet<TypeModifier> result = EnumSet.noneOf(TypeModifier.class);
        for (final Modifier modifier : modifiers) {
            result.add(TypeModifier.valueOf(modifier.name()));
        }
        return result;
    }

    private TypeSymbol mapToTypeSymbol(final com.sun.tools.javac.code.Type type, final Tree.Kind kind) {
        final TypeSymbol typeSymbol;
        switch (kind) {
            case PRIMITIVE_TYPE:
            case MEMBER_SELECT:
                final QualifiedName primitiveQN = QualifiedName.valueOf(type.tsym.getQualifiedName().toString());
                typeSymbol = new TypeSymbol(primitiveQN);
                break;
            case PARAMETERIZED_TYPE:
            case IDENTIFIER:
            case CLASS: {
                final QualifiedName interfaceQN = QualifiedName.valueOf(type.tsym.getQualifiedName().toString());
                final List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();
                for (com.sun.tools.javac.code.Type typeParam : type.getTypeArguments()) {
                    final TypeKind typeKind = typeParam.getKind();
                    switch(typeKind) {
                        case TYPEVAR: //List<T>
                        case DECLARED: // List<String>
                        case WILDCARD: // List<? extends Number>
                            typeParameters.add(new TypeParameter(QualifiedName.valueOf(typeParam.toString())));
                            break;
                        default:
                            throw new IllegalStateException("Unexpected TypeKind " + kind + " while determining type parameters.");
                    }
                }
                typeSymbol = new TypeSymbol(interfaceQN, typeParameters);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected Tree.Kind " + kind + ".");
        }
        return typeSymbol;
    }

    private List<TypeParameter> mapToTypeParameters(final List<? extends TypeParameterElement> typeParameters) {
        final List<TypeParameter> result = new ArrayList<TypeParameter>();
        // generic name
        if (typeParameters.size() > 0) {
            for (final TypeParameterElement param : typeParameters) {
                final QualifiedName paramName = QualifiedName.valueOf(param.toString());
                final List<QualifiedName> boundedTypes = new ArrayList<QualifiedName>();
                for (TypeMirror typeMirror : param.getBounds()) {
                    boundedTypes.add(QualifiedName.valueOf(typeMirror.toString()));
                }
                result.add(new TypeParameter(paramName, boundedTypes));
            }
        }
        return result;
    }

}