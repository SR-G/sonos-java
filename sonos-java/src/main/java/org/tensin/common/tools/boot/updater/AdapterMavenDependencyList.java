package org.tensin.common.tools.boot.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterLTPXml.
 * 
 * @author u390348
 * @version $Revision: 1.3 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterMavenDependencyList implements IAdapterInput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterMavenDependencyList.class);

    /**
     * Méthode.
     *
     * @param dependencyFileName the dependency file name
     * @return the adapter maven dependency list
     * @throws DependencyException the dependency exception
     */
    public static AdapterMavenDependencyList buildAdapter(final String dependencyFileName) throws DependencyException {
        final AdapterMavenDependencyList adapter = new AdapterMavenDependencyList();
        adapter.setDependencyFileName(dependencyFileName);

        if (!new File(dependencyFileName).isFile()) {
            throw new DependencyException("File [" + dependencyFileName + "] not found (should have been created previously with 'mvn dependency:list')");
        }

        return adapter;
    }

    /** The current jar. */
    private JarContainer currentJar = null;

    /** filePath. */
    private String filePath = "maven_dependencies.txt";

    /** FilePathXml. */
    private String filePathXml = "maven_dependencies.xml";

    /** delimiter. */
    private String delimiter = ":";

    /** destFileName. */
    private String dependencyFileName;

    /**
     * Getter for the attribute filePathXml.
     * 
     * @return Returns the attribute De.
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Gets the dependency file name.
     * 
     * @return the dependency file name
     */
    public String getDependencyFileName() {
        return dependencyFileName;
    }

    /**
     * Getter for the attribute getFilePath.
     * 
     * @return Returns the attribute filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Getter for the attribute filePathXml.
     * 
     * @return Returns the attribute filePathXml.
     */
    public String getFilePathXml() {
        return filePathXml;
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#getName()
     */
    /** {@inheritDoc}
     * @see org.tensin.common.tools.boot.updater.IAdapterInput#getName()
     */
    @Override
    public String getName() {
        return "Load maven dependencies list (from 'mvn dependency:list' output for file [" + getDependencyFileName() + "])";
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#load()
     */
    /** {@inheritDoc}
     * @see org.tensin.common.tools.boot.updater.IAdapterInput#load()
     */
    @Override
    public Collection<JarContainer> load() throws DependencyException {
        StringBuilder sb = new StringBuilder("Results :\n");
        Collection<JarContainer> jars = new ArrayList<JarContainer>();
        try {
            Scanner scanner = new Scanner(new File(dependencyFileName));
            String line;
            String[] temp;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();
                if (StringUtils.isNotEmpty(line) && !line.contains("project: MavenProject:")) {
                    line = line.trim();
                    line = line.replaceAll("artifact =", "");
                    if (StringUtils.isNotEmpty(line)) {
                        temp = line.split(delimiter);
                        if (temp.length >= 4) {
                            sb.append("    Adding [").append(line).append("]").append("\n");
                            currentJar = new JarContainer(AdapterMavenDependencyList.class);
                            currentJar.setGroupId(temp[0].trim());
                            currentJar.setArtifactId(temp[1].trim());
                            currentJar.setVersion(temp[3].trim());
                            jars.add(currentJar);
                        } else {
                            sb.append("    Weird line not added [").append(line).append("]").append("\n");
                        }
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new DependencyException(e);
        } finally {
            LOGGER.info(sb.toString());
        }

        return jars;
    }

    /**
     * Setter for the attribute delimiter.
     * 
     * @param delimiter
     *            The attribute delimiter.
     */
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Sets the dependency file name.
     * 
     * @param dependencyFileName
     *            the new dependency file name
     */
    public void setDependencyFileName(final String dependencyFileName) {
        this.dependencyFileName = dependencyFileName;
    }

    /**
     * Setter for the attribute getFilePath.
     * 
     * @param filePath
     *            the new file path
     * @return Returns the attribute filePath.
     */
    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * Setter for the attribute filePathXml.
     * 
     * @param filePathXml
     *            the new file path xml
     */
    public void setfilePathXml(final String filePathXml) {
        this.filePathXml = filePathXml;
    }

    /**
     * Update destination with start constants.
     * 
     * @param jar
     *            the jar
     * @return the string
     */
    @SuppressWarnings("unused")
    private String updateDestinationWithStartConstants(final JarContainer jar) {
        String key = jar.getKey();
        if (StringUtils.isNotEmpty(key)) {
            key = jar.getJarName();
            /*
             * if (ArrayUtils.contains(StartConstants.ltpLib, key)) {
             * jar.addDestination("ltp");
             * } else {
             * if (ArrayUtils.contains(StartConstants.routeurLib, key)) {
             * jar.addDestination("routeur");
             * }
             * if (ArrayUtils.contains(StartConstants.coeurLib, key)) {
             * jar.addDestination("coeur");
             * }
             * if (ArrayUtils.contains(StartConstants.executeurLib, key)) {
             * jar.addDestination("executeur");
             * }
             * if (ArrayUtils.contains(StartConstants.toolsLib, key)) {
             * jar.addDestination("tools");
             * }
             * if (ArrayUtils.contains(StartConstants.mqutilsLib, key)) {
             * jar.addDestination("mqutils");
             * }
             * if (ArrayUtils.contains(StartConstants.securityutilsLib, key)) {
             * jar.addDestination("securityutils");
             * }
             * if (ArrayUtils.contains(StartConstants.toolsLib, key)) {
             * jar.addDestination("tools");
             * }
             * if (ArrayUtils.contains(StartConstants.vedLib, key)) {
             * jar.addDestination("ved");
             * }
             * }
             * if (ArrayUtils.contains(StartConstants.clientCoeurLib, key)) {
             * jar.addDestination("clientCoeur");
             * }
             * if (ArrayUtils.contains(StartConstants.updateLib, key)) {
             * jar.addDestination("update");
             * }
             * if (ArrayUtils.contains(StartConstants.serviceLib, key)) {
             * jar.addDestination("service");
             * }
             * if (ArrayUtils.contains(StartConstants.tbfLib, key)) {
             * jar.addDestination("tbf");
             * }
             * if (ArrayUtils.contains(StartConstants.surveillantLib, key)) {
             * jar.addDestination("surveillant");
             * }
             * if (ArrayUtils.contains(StartConstants.traceLib, key)) {
             * jar.addDestination("trace");
             * }
             * if (ArrayUtils.contains(StartConstants.servicemixLib, key)) {
             * jar.addDestination("servicemix");
             * }
             * if (ArrayUtils.contains(StartConstants.jbiLib, key)) {
             * jar.addDestination("jbiLib");
             * }
             * if (ArrayUtils.contains(StartConstants.axisLib, key)) {
             * jar.addDestination("axis");
             * }
             * if (ArrayUtils.contains(StartConstants.printServerLib, key)) {
             * jar.addDestination("printServer");
             * }
             */
        }

        return jar.getDestinations();
    }
}