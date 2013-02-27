package org.tensin.common.tools.boot.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DependencyConverter.
 * 
 * @author u248663
 * @version $Revision: 1.8 $
 * @since 19 mars 2010 11:26:57
 */
public class DependencyConverter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyConverter.class);

    /** inputAdapters. */
    private final Collection<Class<?>> inputAdapters = new ArrayList<Class<?>>();

    /** outputAdapters. */
    private final Collection<Class<?>> outputAdapters = new ArrayList<Class<?>>();

    /**
     * Dump differences.
     * 
     * @param jars
     *            the jars
     * @return the string
     */
    public String dumpDifferences(final Collection<JarContainer> jars) {
        final Set<String> jarsName = new TreeSet<String>();
        for (final JarContainer jar : jars) {
            jarsName.add(jar.getJarName());
        }
        final StringBuffer sb = new StringBuffer();
        for (final String jarName : jarsName) {
            sb.append("    ").append(StringUtils.rightPad(jarName, 50)).append(" : ");
            boolean eclipse = false, lib = false, pom = false, start = false, xml = false, dependencyList = false;
            String description = "", destination = "";
            for (final JarContainer jar : jars) {
                if (jar.getJarName().equalsIgnoreCase(jarName)) {
                    if (jar.hasOrigin(AdapterWebInfLib.class)) {
                        lib = true;
                    }
                    if (jar.hasOrigin(AdapterEclipseClasspath.class)) {
                        eclipse = true;
                    }
                    if (jar.hasOrigin(AdapterPomXml.class)) {
                        pom = true;
                    }
                    if (jar.hasOrigin(AdapterMavenDependencyList.class)) {
                        dependencyList = true;
                    }
                    if (jar.hasOrigin(AdapterBootConstants.class)) {
                        start = true;
                    }
                    if (jar.hasOrigin(AdapterLTPXml.class)) {
                        xml = true;
                        description = jar.getDescription();
                        destination = jar.getDestinations();
                    }
                }
            }

            if (eclipse) {
                sb.append("ECLIPSE      ");
            } else {
                sb.append("             ");
            }

            if (lib) {
                sb.append("WEB-LIB      ");
            } else {
                sb.append("             ");
            }

            if (pom) {
                sb.append("POM          ");
            } else {
                sb.append("             ");
            }

            if (start) {
                sb.append("START        ");
            } else {
                sb.append("             ");
            }

            if (xml) {
                sb.append("XML          ");
            } else {
                sb.append("             ");
            }

            if (dependencyList) {
                sb.append("DEPENDENCIES       ");
            } else {
                sb.append("                   ");
            }

            sb.append(description).append(destination).append("\n");
        }

        return sb.toString();
    }

    /**
     * Method.
     * 
     * @param jarContainer
     *            the jar container
     * @param results
     *            the results
     * @return the jar container
     */
    private JarContainer getJarContainer(final JarContainer jarContainer, final List<JarContainer> results) {
        JarContainer result = null;
        for (JarContainer currentJarContainer : results) {
            if (currentJarContainer.getJarBaseName().compareTo(jarContainer.getJarBaseName()) == 0) { // TODO à revoir quand on passera en start.xml (pour se baser sur groupId / artifactId et pas nom du jar)
                return currentJarContainer;
            }
            /*
             * if ( currentJarContainer.compareTo(jarContainer) == 0 ) {
             * return currentJarContainer;
             * }
             */
        }
        return result;
    }

    /**
     * Méthode.
     * 
     * @return the string
     */
    public String help() {
        final StringBuffer sb = new StringBuffer("Input adapters : \n");
        final Iterator<Class<?>> itrInput = inputAdapters.iterator();
        while (itrInput.hasNext()) {
            final Class<?> adapter = itrInput.next();
            sb.append(" - ").append(adapter.getName()).append("\n");
        }
        sb.append("Output adapters : \n");
        final Iterator<Class<?>> itrOutput = outputAdapters.iterator();
        while (itrOutput.hasNext()) {
            final Class<?> adapter = itrOutput.next();
            sb.append(" - ").append(adapter.getName()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Méthode.
     * 
     * @param inputs
     *            the inputs
     * @return the collection
     * @throws DependencyException
     *             the dependency exception
     */
    private Collection<JarContainer> loadFromInputs(final Collection<IAdapterInput> inputs) throws DependencyException {
        final Iterator<IAdapterInput> itr = inputs.iterator();
        final List<JarContainer> results = new ArrayList<JarContainer>();
        while (itr.hasNext()) {
            final IAdapterInput adapter = itr.next();
            LOGGER.info("Chargement des jars depuis l'adapter [" + adapter.getName() + "]");
            for (JarContainer jarContainer : adapter.load()) {
                mergeResult(jarContainer, results);
            }
        }
        Collections.sort(results);
        return results;
    }

    /**
     * Ajoute dans la liste 1 tous les éléments de la liste 2 qui n'y étaient
     * pas déjà.
     * 
     * @param liste1
     *            La première liste
     * @param liste2
     *            La deuxième liste
     * @return La fusion des 2 listes sans doublons
     */
    private List<Object> merge(final List<Object> liste1, final List<Object> liste2) {
        final Iterator<Object> itr = liste2.iterator();
        while (itr.hasNext()) {
            final Object o = itr.next();
            if (!liste1.contains(o)) {
                liste1.add(o);
            }
        }
        return liste1;
    }

    /**
     * Si on retrouve le jar, n'ajoute pas l'entrée.
     * 
     * @param jarContainer
     *            the jar container
     * @param results
     *            the results
     */
    private void mergeResult(final JarContainer jarContainer, final List<JarContainer> results) {
        JarContainer otherJarContainer = getJarContainer(jarContainer, results); // TODO à revoir quand on passera en start.xml
        if (otherJarContainer == null) {
            results.add(jarContainer);
        } else {
            LOGGER.info("    Jar déjà présent, non ajouté mais infos complétées [" + otherJarContainer + "]");
            otherJarContainer.addOrigins(jarContainer.getOrigins());
            otherJarContainer.setStartConstantsKey(jarContainer.getStartConstantsKey());
            otherJarContainer.setStartConstantsRegExp(jarContainer.getStartConstantsRegExp());
        }
    }

    /**
     * Méthode.
     * 
     * @param inputs
     *            the inputs
     * @param outputs
     *            the outputs
     * @return the collection
     * @throws DependencyException
     *             the dependency exception
     */
    public Collection<JarContainer> process(final Collection<IAdapterInput> inputs, final Collection<IAdapterOutput> outputs) throws DependencyException {
        final Collection<JarContainer> jars = loadFromInputs(inputs);
        saveToOutputs(jars, outputs);
        return jars;
    }

    /**
     * Méthode.
     * 
     * @param inputs
     *            the inputs
     * @param output
     *            the output
     * @throws DependencyException
     *             the dependency exception
     */
    public void process(final Collection<IAdapterInput> inputs, final IAdapterOutput output) throws DependencyException {
        final Collection<IAdapterOutput> l = new ArrayList<IAdapterOutput>();
        l.add(output);
        process(inputs, l);
    }

    /**
     * Méthode.
     * 
     * @param input
     *            the input
     * @param outputs
     *            the outputs
     * @throws DependencyException
     *             the dependency exception
     */
    public void process(final IAdapterInput input, final Collection<IAdapterOutput> outputs) throws DependencyException {
        final Collection<IAdapterInput> l = new ArrayList<IAdapterInput>();
        l.add(input);
        process(l, outputs);
    }

    /**
     * Méthode.
     * 
     * @param input
     *            the input
     * @param output
     *            the output
     * @throws DependencyException
     *             the dependency exception
     */
    public void process(final IAdapterInput input, final IAdapterOutput output) throws DependencyException {
        final Collection<IAdapterInput> inputs = new ArrayList<IAdapterInput>();
        inputs.add(input);
        final Collection<IAdapterOutput> outputs = new ArrayList<IAdapterOutput>();
        outputs.add(output);
        process(inputs, outputs);
    }

    /**
     * Méthode.
     * 
     * @throws DependencyException
     *             the dependency exception
     */
    public void registerAdapters() throws DependencyException {
        registerInputAdapters();
        registerOutputAdapters();
    }

    /**
     * Méthode.
     * 
     * @throws DependencyException
     *             the dependency exception
     */
    protected void registerInputAdapters() throws DependencyException {
        inputAdapters.add(AdapterEclipseClasspath.class);
        inputAdapters.add(AdapterBootConstants.class);
        inputAdapters.add(AdapterLTPXml.class);
        inputAdapters.add(AdapterPomXml.class);
        inputAdapters.add(AdapterWebInfLib.class);
    }

    /**
     * Méthode.
     * 
     * @throws DependencyException
     *             the dependency exception
     */
    protected void registerOutputAdapters() throws DependencyException {
        outputAdapters.add(AdapterEclipseClasspath.class);
        outputAdapters.add(AdapterBootConstants.class);
        outputAdapters.add(AdapterLTPXml.class);
        // outputAdapters.add(AdapterLTPClasspath.class);
        outputAdapters.add(AdapterPomXml.class);
        outputAdapters.add(AdapterApt.class);
        outputAdapters.add(AdapterDump.class);
    }

    /**
     * Méthode.
     * 
     * @param jars
     *            the jars
     * @param outputs
     *            the outputs
     * @throws DependencyException
     *             the dependency exception
     */
    private void saveToOutputs(final Collection<JarContainer> jars, final Collection<IAdapterOutput> outputs) throws DependencyException {
        final Iterator<IAdapterOutput> itr = outputs.iterator();
        while (itr.hasNext()) {
            final IAdapterOutput adapter = itr.next();
            adapter.generate(jars);
        }
    }

}
