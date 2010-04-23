/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.exception.JcgRuntimeException;
import de.steinacker.jcg.model.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Extracts an interface and adds the new interface to the type's list of
 * implemented interfaces.
 * <p/>
 * The TypeTransformer only transforms Types with Type.Kind == CLASS.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class ExtractInterface implements TypeTransformer {

    private final static Logger LOG = Logger.getLogger(ExtractInterface.class);

    private String name = "ExtractInterface";
    private String interfacePrefix = "";
    private String classPrefix = "";
    private String classSuffix = "Impl";
    private String interfaceRelativePackage = "";
    private String interfaceAbsolutePackage = "";
    private String classRelativePackage = "";
    private String classAbsolutePackage = "";
    private boolean addOverrideAnnotation = false;

    /**
     * Optionally set the name of the TypeTransformer.
     * The default name is 'ExtractInterface'.
     *
     * @param name the name of the TypeTransformer.
     */
    public void setName(final String name) {
        this.name = name;
    }

    public void setAddOverrideAnnotation(boolean addOverrideAnnotation) {
        this.addOverrideAnnotation = addOverrideAnnotation;
    }

    /**
     * Optionally set the prefix used to name the interface.
     * Default value is "".
     * @param interfacePrefix a prefix used to name the interface.
     */
    public void setInterfacePrefix(final String interfacePrefix) {
        this.interfacePrefix = interfacePrefix;
    }

    /**
     * Optionally set the prefix used to (re)name the class.
     * By default, this value is "".
     * @param classPrefix prefix of the class name.
     */
    public void setClassPrefix(final String classPrefix) {
        this.classPrefix = classPrefix;
    }

    /**
     * Optionally set the suffix used to (re)name the class.
     * By default, this value is "Impl".
     * @param classSuffix the suffix of the class name.
     */
    public void setClassSuffix(final String classSuffix) {
        this.classSuffix = classSuffix;
    }

    /**
     * Optionally set a relative path to the package, where the interface will be
     * generated.
     *
     * @param interfaceRelativePackage relative path like ../api or ./api.
     */
    public void setInterfaceRelativePackage(final String interfaceRelativePackage) {
        if (!this.interfaceRelativePackage.isEmpty())
            throw new IllegalArgumentException("Only one of interfaceRelativePackage and interfaceAbsolutePackage is allowed.");
        this.interfaceRelativePackage = interfaceRelativePackage;
    }

    /**
     * Optionally set a relative path to the package, where the class will be
     * generated.
     *
     * @param classRelativePackage relative path like ../impl or ./impl.
     */
    public void setClassRelativePackage(final String classRelativePackage) {
        if (!this.classAbsolutePackage.isEmpty())
            throw new IllegalArgumentException("Only one of classRelativePackage and classAbsolutePackage is allowed.");
        this.classRelativePackage = classRelativePackage;
    }

    /**
     * Optionally set the absolute path of the package, where the interface will
     * be generated.
     *
     * @param interfaceAbsolutePackage absolute path like /de/gsteinacker/jcg
     */
    public void setInterfaceAbsolutePackage(final String interfaceAbsolutePackage) {
        if (!this.interfaceRelativePackage.isEmpty())
            throw new IllegalArgumentException("Only one of interfaceRelativePackage and interfaceAbsolutePackage is allowed.");
        this.interfaceAbsolutePackage = interfaceAbsolutePackage;
    }

    /**
     * Optionally set the absolute path of the package, where the class will
     * be generated.
     *
     * @param classAbsolutePackage absolute path like /de/gsteinacker/jcg
     */
    public void setClassAbsolutePackage(final String classAbsolutePackage) {
        if (!this.classRelativePackage.isEmpty())
            throw new IllegalArgumentException("Only one of classRelativePackage and classAbsolutePackage is allowed.");
        this.classAbsolutePackage = classAbsolutePackage;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation transformes a single class into an interface and a class implementing this
     * interface.
     * <p>
     * Only types with Kind=CLASS are transformed. For all other kinds of types, the input message is
     * just returned.
     *
     * @param message the source message.
     * @return list containing one or two TypeMessages
     */
    @Override
    public List<TypeMessage> transform(final TypeMessage message) {
        final Type type = message.getPayload();
        if (type.getKind() != Type.Kind.CLASS) {
            return Collections.singletonList(message);
        } else {
            return Arrays.asList(transformToInterface(message), transformToClass(message));
        }
    }

    /**
     * Transforms a class into the new class. The new class implements the new interface
     * and possibly has a different name and package, depending on the configuration of
     * this transformer. The methods are annotated with java.lang.Override, if addOverrideAnnotation
     * is true.
     *
     * @param message the TypeMessage containing the source class.
     * @return TypeMessage containing the target class.
     */
    private TypeMessage transformToClass(final TypeMessage message) {
        try {
            final Type type = message.getPayload();
            final TypeBuilder builder = new TypeBuilder(type);
            final QualifiedName className = buildClassName(type);
            builder.setName(className);
            builder.addNameOfInterface(buildInterfaceName(type));
            final List<Method> methods = new ArrayList<Method>();
            for (final Method method : type.getMethods()) {
                if (method.isConstructor()) {
                    methods.add(new MethodBuilder(method)
                    .setName(className.getSimpleName())
                    .toMethod());
                } else {
                    if (addOverrideAnnotation && isInterfaceMethodCandidate(method)) {
                        methods.add(new MethodBuilder(method)
                                .addAnnotation(new Annotation(QualifiedName.valueOf("java.lang.Override")))
                                .toMethod()
                        );
                    } else {
                        methods.add(method);
                    }
                }
            }
            builder.setMethods(methods);
            return new TypeMessage(builder.toType(), message.getContext());
        } catch (final NoSuchElementException e) {
            throw new JcgRuntimeException("Can not determine a package for type=" + message.getPayload() + " with ExtractInterface=" + toString());
        }
    }


    /**
     * Transforms a class into an interface. The interface defines all methods, that are not
     * a constructor, not static, but PUBLIC.
     * The name and package of the interface depends on the configuration of this transformer.
     *
     * @param message the TypeMessage containing the source class.
     * @return TypeMessage containing the target interface.
     */
    private TypeMessage transformToInterface(final TypeMessage message) {
        try {
            final Type type = message.getPayload();
            final TypeBuilder builder = new TypeBuilder(type);
            builder.setName(buildInterfaceName(type));
            builder.setKind(Type.Kind.INTERFACE);
            builder.setFields(Collections.<Field>emptyList());
            final List<Method> methods = new ArrayList<Method>();
            for (final Method method : type.getMethods()) {
                if (isInterfaceMethodCandidate(method)) {
                    methods.add(new MethodBuilder(method)
                            .setMethodBody(null)
                            .toMethod());
                }
            }
            builder.setMethods(methods);
            builder.setNameOfSuperClass(null);
            Set<TypeModifier> modifiers = EnumSet.of(TypeModifier.PUBLIC);
            builder.setModifiers(modifiers);
            return new TypeMessage(builder.toType(), message.getContext());
        } catch (final NoSuchElementException e) {
            throw new JcgRuntimeException("Can not determine a package for type=" + message.getPayload() + " with ExtractInterface=" + toString());
        }
    }

    /**
     * Returns true, if the method will be defined in the generated interface.
     *
     * @param method the method
     * @return true if the method will be defined in the interface.
     */
    private boolean isInterfaceMethodCandidate(final Method method) {
        return !method.isConstructor() && !method.is(MethodModifier.STATIC) && method.is(MethodModifier.PUBLIC);
    }

    /**
     * Returns a new class name for the specified type.
     *
     * @param type the type.
     * @return the new class name.
     */
    private QualifiedName buildClassName(final Type type) {
        final QualifiedName typeName = type.getName();
        final CharSequence packageName = buildPackageName(typeName.getPackage(), classAbsolutePackage, classRelativePackage);
        final String className = classPrefix + typeName.getSimpleName() + classSuffix;
        return QualifiedName.valueOf(packageName, className);
    }

    /**
     * Returns the name of the generated interface.
     *
     * @param type the type.
     * @return the name of the interface.
     */
    private QualifiedName buildInterfaceName(final Type type) {
        final QualifiedName typeName = type.getName();
        final CharSequence packageName = buildPackageName(typeName.getPackage(), interfaceAbsolutePackage, interfaceRelativePackage);
        final String interfaceName = interfacePrefix + typeName.getSimpleName();
        return QualifiedName.valueOf(packageName, interfaceName);
    }

    /**
     * Builds a package name from a source package name, an optional absolute package, and an optional relative package.
     * @param packageName the name of the package in format "foo.bar"
     * @param absolutePackage the absolute name of the package in format "/foo/bar"
     * @param relativePackage the relative name of the package in format "../foo/bar"
     * @return package name in format "foo.bar"
     */
    private static CharSequence buildPackageName(final String packageName, final String absolutePackage, final String relativePackage) {
        // TODO: der code funktioniert zwar, ist aber hässlich und unverständlich...
        if (!absolutePackage.isEmpty())
            return transformPathToPackage(absolutePackage);
        if (relativePackage.isEmpty())
            return packageName;
        if (relativePackage.startsWith("/"))
            return transformPathToPackage(relativePackage);

        String[] strings = packageName.split("\\.");
        final Deque<String> currentPath = new ArrayDeque<String>(strings.length);
        for (final String string : strings) {
            currentPath.push(string);
        }
        strings = relativePackage.split("/");
        final List<String> relativePath = new ArrayList<String>(strings.length);
        for (final String string : strings) {
            relativePath.add(string);
        }
        final Iterator<String> relIter = relativePath.iterator();
        while (relIter.hasNext()) {
            final String s = relIter.next();
            if (s.equals("."))
                continue;
            if (s.equals(".."))
                currentPath.pop();
            else
                currentPath.push(s);
        }
        StringBuilder sb = new StringBuilder();
        final Iterator<String> iterator = currentPath.descendingIterator();
        while (iterator.hasNext()) {
            if (sb.length() > 0)
                sb.append(".");
            sb.append(iterator.next());
        }
        return sb;
    }

    /**
     * Converts a path in format foo/bar into a package name like foo.bar.
     * @param path the path
     * @return package name
     */
    private static CharSequence transformPathToPackage(String path) {
        if (path.startsWith("/")) {
            path = (path.length() == 1) ? "" : path.substring(1);
        }
        return path.replaceAll("/", ".");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ExtractInterface");
        sb.append("{name='").append(name).append('\'');
        sb.append(", interfacePrefix='").append(interfacePrefix).append('\'');
        sb.append(", classPrefix='").append(classPrefix).append('\'');
        sb.append(", classSuffix='").append(classSuffix).append('\'');
        sb.append(", interfaceRelativePackage='").append(interfaceRelativePackage).append('\'');
        sb.append(", interfaceAbsolutePackage='").append(interfaceAbsolutePackage).append('\'');
        sb.append(", classRelativePackage='").append(classRelativePackage).append('\'');
        sb.append(", classAbsolutPackage='").append(classAbsolutePackage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}