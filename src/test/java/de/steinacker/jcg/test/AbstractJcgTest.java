/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package de.steinacker.jcg.test;

import de.steinacker.jcg.Context;
import de.steinacker.jcg.ContextBuilder;
import de.steinacker.jcg.exception.JcgException;
import de.steinacker.jcg.generator.Generator;
import de.steinacker.jcg.generator.StringGenerator;
import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.parse.Parser;
import de.steinacker.jcg.transform.model.ModelMessage;
import de.steinacker.jcg.transform.model.ModelTransformer;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.util.Map;

public abstract class AbstractJcgTest {

    private static final ApplicationContext applicationContext;
    private static final Model parsedModel;
    private static final Model transformedModel;
    private static final Map<QualifiedName, String> generatedCode;

    static {
        DOMConfigurator.configureAndWatch("log4j.xml", 60 * 1000);
        final ApplicationContext parent = new FileSystemXmlApplicationContext("jcg.xml");
        final String[] configLocations = {"jcg-test-generator.xml"};
        applicationContext = new ClassPathXmlApplicationContext(configLocations, parent);
        final Parser parser = applicationContext.getBean("parser", Parser.class);
        parsedModel = parser.parse("src/test/resources/test", true, "./out");
        final ModelTransformer transformer = applicationContext.getBean("modelTransformer", ModelTransformer.class);

        final Context context = new ContextBuilder().addParameter("ctx-selector-param", "Immutable").toContext();
        transformedModel = transformer.transform(new ModelMessage(parsedModel, context)).getPayload();
        final StringGenerator stringGenerator = applicationContext.getBean("stringGenerator", StringGenerator.class);
        final Generator generator = applicationContext.getBean("generator", Generator.class);
        generatedCode = stringGenerator.getGeneratedCode();
        try {
            generator.generate(transformedModel, "");
        } catch (JcgException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public final Model getParsedModel() {
        return parsedModel;
    }

    public final Model getTransformedModel() {
        return transformedModel;
    }

    public final Map<QualifiedName, String> getGeneratedCode() {
        return generatedCode;
    }

    public final String getGeneratedCode(final QualifiedName typeName) {
        return generatedCode.get(typeName);
    }

}
