/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg;

import org.apache.commons.cli.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

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
        options.addOption("b", "bin", true, "The directory, where output of the compiler (that is, .class files) is written to. By default, this is './bin'");
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
                final JcgParameters params;
                if (cli.hasOption('p')) {
                    params = new JcgParameters(cli.getOptionValue('p'));
                } else {
                    params = new JcgParameters();
                    if (cli.hasOption('f'))
                        params.setSourceFile(cli.getOptionValue('f'));
                        if (cli.hasOption('d'))
                            params.setSourceDir(cli.getOptionValue('d'));
                        if (cli.hasOption('t'))
                            params.setTargetDir(cli.getOptionValue('t'));
                        if (cli.hasOption('r'))
                            params.setRecursive(true);
                        if (cli.hasOption('c'))
                            params.setSpringConfig(cli.getOptionValue('c'));
                        if (cli.hasOption('s'))
                            params.setSelector(cli.getOptionValue('s'));
                        if (cli.hasOption('b'))
                            params.setBinDir(cli.getOptionValue('b'));
                }
                if (params.getTargetDir() == null || (params.getSourceDir() == null && params.getSourceFile() == null))
                    help();
                final ApplicationContext context = new FileSystemXmlApplicationContext(params.getSpringConfig());
                final JcgController controller = context.getBean(JcgController.class);
                if (params.getSourceFile() != null)
                    controller.invoke(params.getSelector(), params.getSourceFile(), params.getTargetDir(), params.getBinDir());
                else
                    controller.invoke(params.getSelector(), params.getSourceDir(), params.isRecursive(),  params.getTargetDir(), params.getBinDir());
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