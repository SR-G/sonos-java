package org.tensin.common.tools.boot.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterLTPStartConstants.
 * 
 * @author u248663
 * @version $Revision: 1.5 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterBootConstants implements IAdapterInput, IAdapterOutput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterBootConstants.class);

    /** LIB_COLUMNS. */
    private static final int LIB_COLUMNS = 5;

    /**
     * Guess encoding.
     * 
     * @return the string
     */
    public static String guessEncoding() {
        String foundEncoding = System.getProperty("file.encoding", "UTF-8").toUpperCase().trim();
        return foundEncoding;
    }

    /**
     * Remplacement d'une chaîne par une autre dans un fichier.
     *
     * @param filePathName Le fichier dans lequel faire le remplacement
     * @param oldString L'ancienne chaine
     * @param newString La nouvelle chaîne
     * @throws DependencyException the dependency exception
     */
    public static void replace(final String filePathName, final String oldString, final String newString) throws DependencyException {
        StringBuilder sb = new StringBuilder();
        sb.append("Remplacement dans " + filePathName).append("\n");
        sb.append(" > Ancienne chaine : " + oldString).append("\n");
        sb.append(" > Nouvelle chaine : " + newString).append("\n");
        final File file = new File(filePathName);
        BufferedReader br = null;
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), guessEncoding());

            br = new BufferedReader(isr);
            final StringBuffer buffer = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                buffer.append(line + "\r\n");
                line = br.readLine();
            }
            String fileStr = null;
            try {
                fileStr = buffer.toString().replaceAll(oldString, newString);
                // CHECKSTYLE:OFF exceptions
            } catch (final Exception e) {
                sb.append("Erreur de remplacement dans '" + oldString + "' : " + e.getMessage()).append("\n");
                // CHECKSTYLE:ON
            }
            osw = new OutputStreamWriter(new FileOutputStream(file), guessEncoding());
            osw.write(fileStr);
        } catch (final IOException e) {
            throw new DependencyException("Erreur remplacement chaine", e);
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                    isr = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
                if (osw != null) {
                    osw.close();
                    osw = null;
                }
            } catch (final IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    /** constantName. */
    private String constantName;

    /** excludedJars. */
    private List<String> excludedJars = new ArrayList<String>();

    /** START_JAR_ENTRY_PATTERN. */
    public static final String START_JAR_ENTRY_PATTERN = ".*String (JAR_[^ ]*).*=.*\"(.*\\.jar)\".*";

    /**
     * Builds the adapter.
     * 
     * @param destFileName
     *            the dest file name
     * @return the adapter ltp start constants
     */
    public static AdapterBootConstants buildAdapter(final String destFileName) {
        AdapterBootConstants adapter = new AdapterBootConstants();
        adapter.setDestFileName(destFileName);
        return adapter;
    }

    /**
     * Méthode buildAdapter.
     * 
     * @param destFileName
     *            String
     * @param constantName
     *            String
     * @return AdapterLTPStartConstants
     */
    public static AdapterBootConstants buildAdapter(final String destFileName, final String constantName) {
        AdapterBootConstants adapter = new AdapterBootConstants();
        adapter.setDestFileName(destFileName);
        adapter.setConstantName(constantName);
        return adapter;
    }

    /**
     * Builds the adapter.
     * 
     * @param destFileName
     *            the dest file name
     * @param constantName
     *            the constant name
     * @param useRegExp
     *            the use reg exp
     * @return the i adapter output
     */
    public static IAdapterOutput buildAdapter(final String destFileName, final String constantName, final boolean useRegExp) {
        AdapterBootConstants adapter = new AdapterBootConstants();
        adapter.setDestFileName(destFileName);
        adapter.setConstantName(constantName);
        adapter.setUseRegExp(useRegExp);
        return adapter;
    }

    /**
     * Méthode buildAdapter.
     * 
     * @param destFileName
     *            String
     * @param constantName
     *            String
     * @param excludedJars
     *            List
     * @return AdapterLTPStartConstants
     */
    public static AdapterBootConstants buildAdapter(final String destFileName, final String constantName, final List<String> excludedJars) {
        AdapterBootConstants adapter = new AdapterBootConstants();
        adapter.setDestFileName(destFileName);
        adapter.setConstantName(constantName);
        adapter.setExcludedJars(excludedJars);
        return adapter;
    }

    /** The use reg exp. */
    private boolean useRegExp;

    /** mode. */
    private String mode;

    /** destFileName. */
    private String destFileName;

    /**
     * Adds the excluded jar.
     * 
     * @param jar
     *            the jar
     */
    public void addExcludedJar(final String jar) {
        excludedJars.add(jar);
    }

    /**
     * Method.
     * 
     * @param expectedBaseJars
     *            List
     * @return String
     */
    private String buildAllLibs(final Collection<JarContainer> expectedBaseJars) {
        return buildAllLibs(expectedBaseJars, null);
    }

    /**
     * Construit la liste de toutes les librairies, sauf jars exclus.
     * 
     * @param expectedJars
     *            the expected jars
     * @param excludedJars
     *            List
     * @return String
     */
    private String buildAllLibs(final Collection<JarContainer> expectedJars, final List<String> excludedJars) {
        StringBuffer sb = new StringBuffer();
        Iterator<JarContainer> itr = expectedJars.iterator();
        int count = 0;
        JarContainer item;
        while (itr.hasNext()) {
            item = itr.next();

            if (!isExcludedName(item.getStartConstantsKey(), excludedJars)) {
                // if ( item.hasOrigin(AdapterBootConstants.class)) {
                if (count > 0) {
                    sb.append(", ");
                }
                if ((count % LIB_COLUMNS) == 0) {
                    sb.append("\n\t\t\t");
                }
                String key = item.getStartConstantsKey();
                if (StringUtils.isNotEmpty(key)) {
                    sb.append(key);
                } else {
                    if (useRegExp) {
                        key = item.buildStartConstantsRegExp();
                    } else {
                        key = item.getJarName();
                    }
                    sb.append("\"").append(key).append("\"");
                }
                count++;
                // }
            }
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterOutput#generate()
     */
    /** {@inheritDoc}
     * @see org.tensin.common.tools.boot.updater.IAdapterOutput#generate(java.util.Collection)
     */
    @Override
    public void generate(final Collection<JarContainer> jars) throws DependencyException {
        if (!new File(destFileName).exists()) {
            LOGGER.error("The file [" + destFileName + "] doesn't exist.");
        } else {
            LOGGER.info("Updating content for the file [" + destFileName + "]");
            // Mise à jour des jars indidividuels
            for (JarContainer jar : jars) {
                if (jar.hasOrigin(AdapterBootConstants.class)) {
                    String oldJarDefinition = "public static final String " + jar.getStartConstantsKey() + " = \"[^\"]*\";";
                    String newJarDefinition = "public static final String " + jar.getStartConstantsKey() + " = \"" + jar.getJarName() + "\";";
                    replace(destFileName, oldJarDefinition, newJarDefinition);
                }
            }

            // Construction de la liste de tous les jars
            String allLibs = buildAllLibs(jars, getExcludedJars());
            String allLibsDeclaration = "public static final String[] " + constantName + " = { " + allLibs + " };";
            LOGGER.info(allLibsDeclaration);
            replace(destFileName, "public static final String\\[\\] " + constantName + " = \\{[^\\}]*\\};", allLibsDeclaration);
        }
    }

    /**
     * Récupération d'un reader sur un item éventuellement interne ou sinon fichier.
     * 
     * @param path
     *            Chemin.
     * @return BufferedReader item
     * @throws IOException
     *             Problème.
     */
    private BufferedReader getBufferedReader(final String path) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(path);
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(path);
        }
        if (is == null) {
            is = new FileInputStream(path);
        }
        return new BufferedReader(new InputStreamReader(is, guessEncoding()));
    }

    /**
     * Gets the constant name.
     * 
     * @return the constant name
     */
    public String getConstantName() {
        return constantName;
    }

    /**
     * Getter de l'attribut destFileName.
     * 
     * @return Returns L'attribut destFileName.
     */
    public String getDestFileName() {
        return destFileName;
    }

    /**
     * Méthode getExcludedJars.
     * 
     * @return the excluded jars
     *         List
     */
    public List<String> getExcludedJars() {
        return excludedJars;
    }

    /**
     * Getter de l'attribut mode.
     * 
     * @return Returns L'attribut mode.
     */
    public String getMode() {
        return mode;
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
        return "Load and save from and to LTP java StartConstants file";
    }

    /**
     * Jar exclu ?.
     * 
     * @param item
     *            String
     * @param namesToCheck
     *            Liste des jars exclus
     * @return boolean
     */
    private boolean isExcludedName(final String item, final List<String> namesToCheck) {
        boolean result = false;
        Iterator<String> itr = namesToCheck.iterator();
        String pattern;
        while (itr.hasNext() && !result) {
            pattern = itr.next();
            if (item.startsWith(pattern)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Checks if is use reg exp.
     * 
     * @return true, if is use reg exp
     */
    public boolean isUseRegExp() {
        return useRegExp;
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
        return loadStartJavaContent(getDestFileName());
    }

    /**
     * Chargement des jars contenus dans le start.java
     * 
     * @param javaFileToUpdate
     *            String
     * @return Collection
     * @throws DependencyException
     *             the dependency exception
     */
    private Collection<JarContainer> loadStartJavaContent(final String javaFileToUpdate) throws DependencyException {
        return loadStartJavaContent(javaFileToUpdate, START_JAR_ENTRY_PATTERN);
    }

    /**
     * Chargement des jars contenus dans le start.java
     * 
     * @param javaFileToUpdate
     *            String
     * @param jarEntryConstantPattern
     *            String
     * @return Collection
     * @throws DependencyException
     *             the dependency exception
     */
    private Collection<JarContainer> loadStartJavaContent(final String javaFileToUpdate, final String jarEntryConstantPattern) throws DependencyException {
        Collection<JarContainer> startJavaLibs = new ArrayList<JarContainer>();
        BufferedReader br;
        try {
            br = getBufferedReader(javaFileToUpdate);
            String lineStartJava = null;
            Pattern pattern = Pattern.compile(jarEntryConstantPattern);
            Matcher doubleMatcher;
            String key = null;
            String value = null;
            JarContainer jar;
            while ((lineStartJava = br.readLine()) != null) {
                if (Pattern.matches(jarEntryConstantPattern, lineStartJava)) {
                    doubleMatcher = pattern.matcher(lineStartJava);
                    if (doubleMatcher.find()) {
                        key = doubleMatcher.group(1);
                        value = doubleMatcher.group(2);
                    }
                    jar = new JarContainer(AdapterBootConstants.class);
                    jar.setStartConstantsKey(key);
                    jar.setStartConstantsRegExp(value);
                    jar.setJarName(value);
                    startJavaLibs.add(jar);
                }
            }
        } catch (IOException e) {
            throw new DependencyException(e);
        }
        return startJavaLibs;
    }

    /**
     * Sets the constant name.
     * 
     * @param constantName
     *            the new constant name
     */
    public void setConstantName(final String constantName) {
        this.constantName = constantName;
    }

    /**
     * Setter de l'attribut destFileName.
     * 
     * @param destFileName
     *            L'attribut destFileName.
     */
    public void setDestFileName(final String destFileName) {
        this.destFileName = destFileName;
    }

    /**
     * Méthode setExcludedJars.
     * 
     * @param excludedJars
     *            void
     */
    public void setExcludedJars(final List<String> excludedJars) {
        this.excludedJars = excludedJars;
    }

    /**
     * Setter de l'attribut mode.
     * 
     * @param mode
     *            L'attribut mode.
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    /**
     * Sets the use reg exp.
     * 
     * @param useRegExp
     *            the new use reg exp
     */
    public void setUseRegExp(final boolean useRegExp) {
        this.useRegExp = useRegExp;
    }

}
