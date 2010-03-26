/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg;

import org.apache.commons.cli.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.FileReader;
import java.util.Properties;

/**
 * The main class to verify java files using custom annotation processor. The
 * files to be verified can be supplied to this class as comma-separated
 * argument.
 *
 * @author Guido Steinacker
 */
public class Jcg {

    private final static Options options = new Options();

    static {
        options.addOption("h", "help", false, "Print this help.");
        options.addOption("f", "sourceFile", true, "A single source file. Must be provided if no sourcedir is specified.");
        options.addOption("d", "sourceDir", true, "The source directory. Must be provided if no file is specified.");
        options.addOption("t", "targetDir", true, "The target directory where generated files are copied to. Must be provided.");
        options.addOption("s", "selector", true, "Selector used to select one of several possible transformer chains. Must be provided");
        options.addOption("r", "recursive", false, "Recursively traverse the source directory. Default is false");
        options.addOption("c", "config", true, "The configuration file used to initialize jcg.");
        options.addOption("p", "propertyFile", true, "The property-file used to configure jcg. Properties contained in this file are overwritten by other parameters.");
    }


    /**
     * The main method accepts the file(s) for verification. Multiple files can
     * be verified by passing the absolute path of the files as comma-separated
     * argument. This method invokes the custom annotation processor to process
     * the annnotations in the supplied file(s). Verification results will be
     * printed on to the console.
     *
     * @param args The java source files to be verified.
     */
    public static void main(String[] args) {
        try {
            DOMConfigurator.configureAndWatch("log4j.xml", 60 * 1000);

            final CommandLineParser cliParser = new PosixParser();
            final CommandLine cli = cliParser.parse(options, args);

            if (cli.hasOption('h')) {
                help();
            } else {
                String sourceFile = null;
                String sourceDir = null;
                String targetDir = null;
                boolean recursive = false;
                String springConfig = "jcg.xml";
                String transformer = null;
                if (cli.hasOption("p")) {
                    final Properties p = new Properties();
                    p.load(new FileReader(cli.getOptionValue("p")));
                    transformer = p.getProperty("transformer");
                    sourceFile = p.getProperty("sourceFile");
                    sourceDir = p.getProperty("sourceDir");
                    targetDir = p.getProperty("targetDir");
                    if (p.contains("recursive"))
                        recursive = Boolean.parseBoolean(p.getProperty("recursive"));
                    springConfig = p.getProperty("config", springConfig);
                }
                if (cli.hasOption('f'))
                    sourceFile = cli.getOptionValue('f');
                if (cli.hasOption('d'))
                    sourceDir = cli.getOptionValue('d');
                if (cli.hasOption('t'))
                    targetDir = cli.getOptionValue('t');
                if (cli.hasOption('r'))
                    recursive = true;
                if (cli.hasOption('c'))
                    springConfig = cli.getOptionValue('c');
                if (cli.hasOption('s'))
                    transformer = cli.getOptionValue('s');
                if (targetDir == null || (sourceDir == null && sourceFile == null))
                    help();

                // Put all important cli-params in a Properties instance:
                final ApplicationContext context = new FileSystemXmlApplicationContext(springConfig);
                final JcgController controller = context.getBean(JcgController.class);
                if (sourceFile != null)
                    controller.invoke(transformer, sourceFile, targetDir);
                else
                    controller.invoke(transformer, sourceDir, recursive, targetDir);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void help() throws ParseException {
        new HelpFormatter().printHelp("jcg -d ./src/main/java -t ./src/generated/java [-r] -s domain-model", options);
        System.exit(42);
    }
}