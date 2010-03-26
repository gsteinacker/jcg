package de.steinacker.jcg;

import org.apache.commons.cli.CommandLine;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Parameters used by Jcg.
 */
public final class JcgParameters {
    private String sourceFile = null;
    private String sourceDir = null;
    private String targetDir = null;
    private boolean recursive = false;
    private String springConfig = "jcg.xml";
    private String selector = null;

    public JcgParameters (final CommandLine cli) throws IOException {
        if (cli.hasOption("p")) {
            mergeWithProperties(cli.getOptionValue('p'));
        }
        mergeWithCliOptions(cli);
    }

    private void mergeWithProperties(final String propertyFile) throws IOException {
        final Properties p = new Properties();
        p.load(new FileReader(propertyFile));
        selector = p.getProperty("selector");
        sourceFile = p.getProperty("sourceFile");
        sourceDir = p.getProperty("sourceDir");
        targetDir = p.getProperty("targetDir");
        selector = p.getProperty("selector");
        if (p.containsKey("recursive")) {
            recursive = Boolean.parseBoolean(p.getProperty("recursive"));
        }
        springConfig = p.getProperty("config", springConfig);
    }

    private void mergeWithCliOptions(final CommandLine cli) {
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
            selector = cli.getOptionValue('s');
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(final String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(final String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(final String targetDir) {
        this.targetDir = targetDir;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(final boolean recursive) {
        this.recursive = recursive;
    }

    public String getSpringConfig() {
        return springConfig;
    }

    public void setSpringConfig(final String springConfig) {
        this.springConfig = springConfig;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(final String selector) {
        this.selector = selector;
    }
}
