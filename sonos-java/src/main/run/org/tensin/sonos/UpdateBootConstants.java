package org.tensin.sonos;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.common.tools.boot.updater.AdapterBootConstants;
import org.tensin.common.tools.boot.updater.AdapterDump;
import org.tensin.common.tools.boot.updater.AdapterMavenDependencyList;
import org.tensin.common.tools.boot.updater.DependencyConverter;
import org.tensin.common.tools.boot.updater.IAdapterInput;
import org.tensin.common.tools.boot.updater.IAdapterOutput;
import org.tensin.common.tools.boot.updater.JarContainer;

/**
 * The Class UpdateBootConstants.
 */
public class UpdateBootConstants {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBootConstants.class);

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws CoreException
     *             the core exception
     */
    public static void main(final String[] args) throws SonosException {
        BasicConfigurator.configure();

        final DependencyConverter converter = new DependencyConverter();

        final Collection<IAdapterInput> inputs = new ArrayList<IAdapterInput>();
        inputs.add(AdapterMavenDependencyList.buildAdapter("target/maven-dependencies.txt")); // has to be created through mvn dependency:list -PdependenciesList
        inputs.add(AdapterBootConstants.buildAdapter("src/main/java/org/tensin/sonos/boot/CLIBootConstants.java", "LIBS"));

        final Collection<IAdapterOutput> outputs = new ArrayList<IAdapterOutput>();
        outputs.add(AdapterDump.buildAdapter());
        outputs.add(AdapterBootConstants.buildAdapter("src/main/java/org/tensin/sonos/boot/CLIBootConstants.java", "LIBS", true));

        final Collection<JarContainer> jars = converter.process(inputs, outputs);
        LOGGER.info("Summary :\n" + converter.dumpDifferences(jars));
    }
}