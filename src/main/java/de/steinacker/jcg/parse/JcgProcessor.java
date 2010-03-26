/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.ModelBuilder;
import de.steinacker.jcg.model.TypeBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * An Annotation Processor used by the JavaFileParser in order to parse Java code and build a Model.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("*")
final class JcgProcessor extends AbstractProcessor {
    //private NameCheckScanner nameCheckScanner;
    private final ModelBuilder modelBuilder;
    private ModelBuildingScanner modelBuildingScanner;

    public JcgProcessor() {
        this.modelBuilder = new ModelBuilder();
    }

    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //this.nameCheckScanner = new NameCheckScanner(messager, typeUtils);
        modelBuildingScanner = new ModelBuildingScanner(processingEnv);
    }

    /**
     * Check that the names of the root elements (and their enclosed
     * elements) follow the appropriate naming conventions.  This
     * processor examines all files regardless of whether or not
     * annotations are present; no new source or class files are
     * generated.
     * <p/>
     * <p>Processors that actually process specific annotations should
     * <em>not</em> report supporting {@code *}; this could cause
     * performance degradations and other undesirable outcomes.
     */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getRootElements()) {
                final TypeBuilder typeBuilder = modelBuildingScanner.scan(element, new TypeBuilder());
                modelBuilder.addType(typeBuilder.toType());
            }
        }
        return false;
    }

    public Model getModel() {
        return modelBuilder.toModel();
    }
}