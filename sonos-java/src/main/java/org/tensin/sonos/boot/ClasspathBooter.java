package org.tensin.sonos.boot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.tensin.common.tools.boot.updater.FilenameFilterJar;
import org.tensin.sonos.SonosException;

/**
 * The Class ClasspathBooter.
 */
public class ClasspathBooter {

    /**
     * UrlComparator.
     * 
     * @author u248663
     * @version $Revision: 1.12 $
     * @since 11 déc. 2009 15:53:33
     */
    public class UrlComparator implements Comparator<URL> {

        /**
         * Compare.
         * 
         * @param u1
         *            the u1
         * @param u2
         *            the u2
         * @return the int {@inheritDoc}
         * @see java.util.Comparator#compare(T, T)
         */
        @Override
        public int compare(final URL u1, final URL u2) {
            int ret = u1.getHost().compareTo(u2.getHost());
            if (ret != 0) {
                return ret;
            }
            ret = u1.getPath().compareTo(u2.getPath());
            if (ret != 0) {
                return ret;
            }
            ret = u1.getProtocol().compareTo(u2.getProtocol());
            if (ret != 0) {
                return ret;
            }
            if (u1.getPort() < u2.getPort()) {
                return -1;
            } else if (u1.getPort() < u2.getPort()) {
                return 1;
            }
            return 0;
        }
    }

    /** ADD_URL_METHOD_NAME. */
    private static final String ADD_URL_METHOD_NAME = "addURL";

    /** SUFFIX_CLASS. */
    private static final String SUFFIX_CLASS = ".class";

    /** addUrlMethod. */
    private static Method addUrlMethod;

    /** sysLoader. */
    private static URLClassLoader sysLoader;

    /**
     * Method.
     * 
     * @param l
     *            the l
     */
    public static void dump(final Collection<?> l) {
        StringBuilder sb = new StringBuilder();
        if (l == null) {
            sb.append("Uninitialized variable (null)");
        } else if (l.isEmpty()) {
            sb.append("Empty list");
        } else {
            Iterator<?> iterator = l.iterator();
            while (iterator.hasNext()) {
                sb.append(" - ").append(iterator.next().toString()).append("\n");
            }
        }
    }

    /**
     * Get AddUrl java method.
     * 
     * @return Method
     * @throws SonosException
     *             Error
     */
    private static Method getAddUrlMethod() throws SonosException {
        if (addUrlMethod == null) {
            final Class<?> sysClass = URLClassLoader.class;
            try {
                addUrlMethod = sysClass.getDeclaredMethod(ADD_URL_METHOD_NAME, new Class[] { URL.class });
                addUrlMethod.setAccessible(true);
            } catch (final SecurityException e) {
                throw new SonosException(e);
            } catch (final NoSuchMethodException e) {
                throw new SonosException(e);
            }
        }

        return addUrlMethod;
    }

    /**
     * Méthode.
     * 
     * @return URLClassLoader
     */
    public static URLClassLoader getURLClassLoader() {
        if (sysLoader == null) {
            sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        }
        return sysLoader;
    }

    /**
     * Is empty.
     * 
     * @param str
     *            String
     * @return boolean
     */
    public static boolean isEmpty(final String str) {
        return (str == null) || (str.trim().length() == 0);
    }

    /** debug. */
    private boolean debug;

    /** missingJars. */
    private final List<String> missingJars = new ArrayList<String>();

    /** initialJars. */
    private final List<URL> initialJars = new ArrayList<URL>();

    /** potentialProblems. */
    private final List<String> potentialProblems = new ArrayList<String>();

    /** potentialproblemsChecked. */
    private boolean potentialproblemsChecked;

    /** jarBaseName. */
    private String jarBaseName;

    /** stopIfMissingJars. */
    private boolean stopIfMissingJars;

    /** projectName. */
    private String projectName;

    /**
     * Constructeur.
     * 
     * @param jarBaseName
     *            Le début du nom du jar, ex. "ddl-java" ou "elf-java").
     * @param projectName
     *            Le nom du projet (ex. "DDL"
     */
    public ClasspathBooter(final String jarBaseName, final String projectName) {
        super();
        this.jarBaseName = jarBaseName;
        this.projectName = projectName;
        initialJars.addAll(Arrays.asList(getURLClassLoader().getURLs()));
        Collections.sort(initialJars, new UrlComparator());
    }

    /**
     * Ajout au sein du classpath de tous les jars trouvés dans le chemin qui
     * contient le jar courant.
     * 
     * @throws SonosException
     *             Error
     */
    public void addAllJars() throws SonosException {
        final String lib = getLibraryPathName(jarBaseName);
        if (!isEmpty(lib)) {
            final File dir = getLibraryPathFile(lib);
            final List<File> potentialJars = loadJarsInPath(dir);
            updateCurrentClasspath(potentialJars);
        }
    }

    /**
     * Ajout au sein du classpath de tous les jars trouvés dans un chemin .
     * 
     * @param pathToLoad
     *            Le chemin à analyser
     * @throws SonosException
     *             Error
     */
    public void addAllJars(final String pathToLoad) throws SonosException {
        if (!isEmpty(pathToLoad)) {
            final File dir = getLibraryPathFile(pathToLoad);
            final List<File> potentialJars = loadJarsInPath(dir);
            updateCurrentClasspath(potentialJars);
        }
    }

    /**
     * Construction du classpath.
     * 
     * @param expectedJars
     *            List
     * @throws SonosException
     *             Error
     */
    public void addJarsToClasspath(final List<String> expectedJars) throws SonosException {
        addJarsToClasspath(getLibraryPathName(jarBaseName), expectedJars);
    }

    /**
     * Construction du classpath.
     * 
     * @param expectedJars
     *            String[]
     * @throws SonosException
     *             Error
     */
    public void addJarsToClasspath(final Set<String> expectedJars) throws SonosException {
        addJarsToClasspath(getLibraryPathName(jarBaseName), new ArrayList<String>(expectedJars));
    }

    /**
     * Construction du classpath.
     * 
     * @param lib
     *            String
     * @param expectedJars
     *            List
     * @throws SonosException
     *             Error
     */
    public void addJarsToClasspath(final String lib, final List<String> expectedJars) throws SonosException {
        if (!isEmpty(lib)) {
            if (new File(lib.replaceAll("file:", "")).isDirectory()) {
                final File dir = getLibraryPathFile(lib);
                final List<File> potentialJars = loadJarsInPath(dir);
                if ((potentialJars == null) || (potentialJars.size() == 0)) {
                    throw new SonosException("No jars found in path [" + dir.getAbsolutePath() + "]");
                }
                final List<File> neededJars = loadNeededJars(dir, potentialJars, expectedJars);
                updateCurrentClasspath(neededJars);
            }
        }
    }

    /**
     * Construction du classpath.
     * 
     * @param lib
     *            String
     * @param expectedJars
     *            Set
     * @throws SonosException
     *             Error
     */
    public void addJarsToClasspath(final String lib, final Set<String> expectedJars) throws SonosException {
        if ((expectedJars != null) && (expectedJars.size() > 0)) {
            addJarsToClasspath(lib, new ArrayList<String>(expectedJars));
        }
    }

    /**
     * Construction du classpath.
     * 
     * @param lib
     *            String
     * @param expectedJars
     *            String[]
     * @throws SonosException
     *             Error
     */
    public void addJarsToClasspath(final String lib, final String[] expectedJars) throws SonosException {
        if ((expectedJars != null) && (expectedJars.length > 0)) {
            addJarsToClasspath(lib, Arrays.asList(expectedJars));
        }
    }

    /**
     * Construction du classpath.
     * 
     * @param expectedJars
     *            String[]
     * @throws SonosException
     *             Error
     */
    public void addJarsToClasspath(final String[] expectedJars) throws SonosException {
        if ((expectedJars != null) && (expectedJars.length > 0)) {
            addJarsToClasspath(getLibraryPathName(jarBaseName), expectedJars);
        }
    }

    /**
     * Ajoute le premier jar trouvé dans la première localisation possible.
     * Si aucun jar trouvé nulle part, n'ajoute rien.
     * 
     * @param possibleJarNames
     *            Liste des noms possibles de jars (regexp) : le premier trouvé sera pris
     * @param possiblePaths
     *            Liste des paths possibles où trouver les jars : le premier trouvé sera pris
     * @return true si un jar (et un seul) a été ajouté. false si aucun jar n'a été trouvé.
     * @throws SonosException
     *             the core exception
     */
    public boolean addJarsToClasspathWithMagickDiscovery(final List<String> possibleJarNames, final List<String> possiblePaths) throws SonosException {
        File possiblePathFile;
        File[] foundFiles;
        File foundFile;
        for (String possibleJarName : possibleJarNames) {
            for (String possiblePath : possiblePaths) {
                possiblePathFile = new File(possiblePath);
                if (possiblePathFile.exists() && possiblePathFile.isDirectory()) {
                    foundFiles = possiblePathFile.listFiles();
                    for (File foundFile2 : foundFiles) {
                        foundFile = foundFile2;
                        if (foundFile.getName().matches(possibleJarName)) {
                            List<File> l = new ArrayList<File>();
                            l.add(foundFile);
                            updateCurrentClasspath(l);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Méthode dumpant le classpath (espaces entre chaque item).
     * 
     * @return String
     */
    public String displayClasspath() {
        return displayClasspath(" ");
    }

    /**
     * Méthode dumpant le classpath.
     * 
     * @param lineSeparator
     *            Le caractère de séparation (" ", "\n", etc.)
     * @return String
     */
    public String displayClasspath(final String lineSeparator) {
        final StringBuilder cpLog = new StringBuilder(lineSeparator);
        if ((initialJars != null) && (initialJars.size() > 0)) {
            cpLog.append("initial classpath=").append(lineSeparator);
            final Iterator<URL> itr = initialJars.iterator();
            while (itr.hasNext()) {
                cpLog.append(" - ").append((itr.next()).getPath()).append(lineSeparator);
            }
        }

        cpLog.append("dynamic classpath=").append(lineSeparator);
        final List<URL> dynamicJars = new ArrayList<URL>();
        dynamicJars.addAll(Arrays.asList(getURLClassLoader().getURLs()));
        Collections.sort(dynamicJars, new UrlComparator());
        final Iterator<URL> itr = dynamicJars.iterator();
        URL currentJar;
        while (itr.hasNext()) {
            currentJar = itr.next();
            if (!initialJars.contains(currentJar)) {
                cpLog.append(" - ").append(currentJar.getPath()).append(lineSeparator);
            }
        }
        return cpLog.toString();
    }

    /**
     * Affichage de la liste des jars manquants.
     * 
     * @return String.
     */
    public String displayMissingJars() {
        final StringBuffer sb = new StringBuffer();
        if (hasMissingJars()) {
            final Iterator<String> itr = missingJars.iterator();
            String item;
            while (itr.hasNext()) {
                item = itr.next();
                sb.append(" - ").append(item).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Crée un objet de la classe spécifié (sauf si méthode statique) et appelle
     * la méthode indiquée. Appel d'une méthode n'ayant pas de paramètres.
     * 
     * @param className
     *            Le nom de la classe sur laquelle travailler (mettre "null" si
     *            on travaille sur une classe statique, par ex. avec une méthode
     *            "main" en 2e paramètre)
     * @param methodName
     *            Le nom de la méthode à exécuter
     * @throws SonosException
     *             Error
     */
    public void execute(final String className, final String methodName) throws SonosException {
        execute(className, methodName, new Class[] {}, new Object[] {});
    }

    /**
     * Crée un objet de la classe spécifié (sauf si méthode statique) et appelle
     * la méthode indiquée. Appel d'une méthode ayant des paramètres.
     * 
     * @param className
     *            Le nom de la classe sur laquelle travailler (mettre "null" si
     *            on travaille sur une classe statique, par ex. avec une méthode
     *            "main" en 2e paramètre)
     * @param methodName
     *            Le nom de la méthode à exécuter
     * @param argsDefinitions
     *            Les paramètres de la méthode à exécuter
     * @param argsValues
     *            Les valeurs à passer en paramètre à la méthode à exécuer
     * @throws SonosException
     *             Error
     */
    public void execute(final String className, final String methodName, final Class<?>[] argsDefinitions, final Object[] argsValues) throws SonosException {
        Class<?> mainClass;
        try {
            mainClass = Class.forName(className);
            final Method mainMethod = mainClass.getDeclaredMethod(methodName, argsDefinitions);
            if (Modifier.isStatic(mainMethod.getModifiers())) {
                mainMethod.invoke(null, argsValues);
            } else {
                final Object o = mainClass.newInstance();
                mainMethod.invoke(o, argsValues);
            }
        } catch (final ClassNotFoundException e) {
            throw new SonosException(e);
        } catch (final InstantiationException e) {
            throw new SonosException(e);
        } catch (final IllegalAccessException e) {
            throw new SonosException(e);
        } catch (final SecurityException e) {
            throw new SonosException(e);
        } catch (final NoSuchMethodException e) {
            throw new SonosException(e);
        } catch (final IllegalArgumentException e) {
            throw new SonosException(e);
        } catch (final InvocationTargetException e) {
            throw new SonosException(e);
        }
    }

    /**
     * Méthode.
     * 
     * @param l
     *            URLClassLaoder
     * @return Liste de string
     */
    private List<String> getAlreadyLoadedJar(final URLClassLoader l) {
        final List<String> alreadyLoadedJars = new ArrayList<String>();
        final URL[] urls = l.getURLs();
        if (urls != null) {
            for (final URL url : urls) {
                alreadyLoadedJars.add(url.getPath());
            }
        }
        return alreadyLoadedJars;
    }

    /**
     * Getter de l'attribut jarBaseName.
     * 
     * @return Returns L'attribut jarBaseName.
     */
    public String getJarBaseName() {
        return jarBaseName;
    }

    /**
     * REtourne l'URI sur la librairie.
     * 
     * @param lib
     *            La librairie
     * @return File
     * @throws SonosException
     *             Error
     */
    private File getLibraryPathFile(final String lib) throws SonosException {
        File result = null;
        String libToAdd = null;
        try {
            libToAdd = lib.replaceAll("\\\\", "/");
            if (!libToAdd.startsWith("file:/")) {
                libToAdd = libToAdd.replaceAll("file:", "file:/");
            }
            result = new File(new URI(libToAdd));
        } catch (final URISyntaxException e) {
            throw new SonosException(e);
        } catch (final IllegalArgumentException e) {
            throw new SonosException("URI " + libToAdd + " incorrecte", e);
        }
        return result;
    }

    /**
     * Gets the library path name.
     * 
     * @param containingJarName
     *            the containing jar name
     * @return the library path name
     */
    public String getLibraryPathName(final String containingJarName) {

        String result = null;
        final URLClassLoader sysLoader = getURLClassLoader();

        /* Recherche répertoire lib */
        final String startClass = ClasspathBooter.class.getName().replace('.', '/');
        if (sysLoader != null) {
            final URL startClassUrl = sysLoader.getResource(startClass + SUFFIX_CLASS);
            if (startClassUrl != null) {
                final String path = startClassUrl.getPath();
                if (!isEmpty(path) && !isEmpty(containingJarName)) { // Ne
                    if (path.matches(".*" + containingJarName + ".*")) {
                        result = path;
                        int p = result.indexOf("!");
                        if (p != -1) {
                            result = result.substring(0, p);
                        }
                        p = result.lastIndexOf("/");
                        result = path.substring(0, p + 1);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the manifest.
     * 
     * @param jarName
     *            the jar name
     * @param lineSeparator
     *            the line separator
     * @return the manifest
     */
    public String getManifest(final String jarName, final String lineSeparator) {
        StringBuilder sb = new StringBuilder();

        String lib = getLibraryPathName(jarBaseName);
        if (!isEmpty(lib)) {
            lib = lib.replaceAll("file:", "");
            final File[] files = new File(lib).listFiles(new FilenameFilterJar());
            if ((files != null) && (files.length > 0)) {
                for (File jar : files) {
                    if (jar.getName().matches(".*" + jarName + ".*") && jar.exists()) {
                        try {
                            JarFile jarFile = new JarFile(jar);
                            final JarEntry entry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
                            final InputStream is = jarFile.getInputStream(entry);
                            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(lineSeparator).append(line);
                            }
                            if (jarFile != null) {
                                jarFile.close();
                                jarFile = null;
                            }
                        } catch (final Exception e) {
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Getter de l'attribut projectName.
     * 
     * @return Returns L'attribut projectName.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Détermine le nom réel du jar matchant une expression régulière (ex.
     * hsqldb.*\\.jar).
     * 
     * @param regExp
     *            L'expression régulière à matcher
     * @return Le nom du jar trouvé
     * @throws SonosException
     *             Erreur
     */
    public List<File> getProvidedJarName(final String regExp) throws SonosException {
        final String lib = getLibraryPathName(jarBaseName);
        if (!isEmpty(lib)) {
            final File dir = getLibraryPathFile(lib);
            final Collection<File> potentialJars = loadJarsInPath(dir);
            return pickCorrespondingJar(regExp, potentialJars);
        }
        return null;
    }

    /**
     * Détermine s'il y a des jars manquants ou non.
     * 
     * @return boolean
     */
    public boolean hasMissingJars() {
        return (missingJars != null) && (missingJars.size() > 0);
    }

    /**
     * Getter de l'attribut debug.
     * 
     * @return Returns L'attribut debug.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Getter de l'attribut stopIfMissingJars.
     * 
     * @return Returns L'attribut stopIfMissingJars.
     */
    public boolean isStopIfMissingJars() {
        return stopIfMissingJars;
    }

    /**
     * Get all jars from a file path.
     * 
     * @param directory
     *            String
     * @return List
     */
    public List<File> loadJarsInPath(final File directory) {
        final List<File> result = new ArrayList<File>();
        if (directory.isDirectory()) {
            // On charge tous les jars du répertoire
            final FilenameFilter filter = new FilenameFilterJar();
            final File[] liste = directory.listFiles(filter);
            for (final File element : liste) {
                if (element.isFile()) {
                    result.add(element);
                }
            }

            // Puis tous les répertoires
            final File[] listeDirectories = directory.listFiles();
            for (final File element : listeDirectories) {
                if (element.isDirectory()) {
                    result.addAll(loadJarsInPath(element));
                }
            }
        }

        Collections.reverse(result);

        return result;
    }

    /**
     * Chargement des jars nécessaires.
     * 
     * @param dir
     *            File
     * @param potentialJars
     *            List
     * @param neededJars
     *            List
     * @return List
     * @throws SonosException
     *             Error
     */
    private List<File> loadNeededJars(final File dir, final List<File> potentialJars, final List<String> neededJars) throws SonosException {
        final List<File> result = new ArrayList<File>();
        final Iterator<String> itr = neededJars.iterator();
        String regExp = null;
        final StringBuffer msgFoundJars = new StringBuffer();
        int errors = 0;
        List<File> foundJars = null;
        // File jar = null;
        // StringBuffer fileName;
        while (itr.hasNext()) {
            regExp = itr.next();
            // S'il s'agit d'un chemin absolu, on l'embarque direct ...
            File f = new File(regExp);
            if (f.exists()) {
                result.add(f);
            } else {
                // Sinon on considère que c'est une regexp et on pioche le meilleur jar correpondant à cette regexp dans la liste des jars
                // disponibles dans le répertoire lib/
                if (regExp == null) {
                    regExp = ".*";
                }
                foundJars = pickCorrespondingJar(regExp, potentialJars);
                if ((foundJars == null) || (foundJars.size() == 0)) {
                    errors++;
                    registerMissingJar(regExp);
                } else {
                    Iterator<File> itrResults = foundJars.iterator();
                    File currentJar;
                    while (itrResults.hasNext()) {
                        currentJar = itrResults.next();
                        // fileName = new StringBuffer(dir.getAbsolutePath()).append("/").append(currentJar);
                        // jar = new File(fileName.toString());
                        if (!currentJar.exists()) {
                            // Cas bizarre, jar identifié mais pas trouvé au final
                            errors++;
                            registerMissingJar(regExp, currentJar.getName());
                        } else {
                            // Cas normal, on a trouvé un jar qu'on embarque
                            msgFoundJars.append("- ").append(currentJar.getName()).append("").append("\n");
                            result.add(currentJar);
                        }
                    }
                }
            }
        }

        if (errors > 0) {
            if (stopIfMissingJars) {
                throw new SonosException("Some expected jars are missing in the path [" + dir.getAbsolutePath() + "] : \n" + displayMissingJars());
            }
        }

        return result;
    }

    /**
     * Récupération du jar souhaité.
     * 
     * @param regExp
     *            La regExp qui doit être validée
     * @param potentialJars
     *            La liste des jars disponibles
     * @return File
     */
    private List<File> pickCorrespondingJar(String regExp, final Collection<File> potentialJars) {
        List<File> result = new ArrayList<File>();
        final Iterator<File> itr = potentialJars.iterator();
        String currentName = null;
        File currentFile = null;
        if (!regExp.startsWith(".*")) {
            regExp = ".*" + regExp;
        }
        if (!regExp.endsWith(".*")) {
            regExp = regExp + ".*";
        }
        while (itr.hasNext()) {
            currentFile = itr.next();
            currentName = currentFile.getAbsolutePath().replaceAll("\\\\", "/");
            if ((currentName != null) && currentName.matches(regExp)) {
                result.add(currentFile);
                itr.remove();
                // TODO gestion des jars trouvés en double ? => actuellement on
                // prend le premier jar trouvé (si regexp et non pas version
                // figée) ...
                // changement 15.03 : on prend tous les jars qui matchent
                // l'expression régulière ...
            }
        }
        return result;
    }

    /**
     * Ajout un jar manquant.
     * 
     * @param regExp
     *            L'expression régulière que l'on a cherché au sein de la liste
     *            des jars
     */
    private void registerMissingJar(final String regExp) {
        missingJars.add("Missing jar [" + regExp + "]");
    }

    /**
     * Ajoute un jar manquant.
     * 
     * @param regExp
     *            L'expression régulière que l'on a cherché au sein de la liste
     *            des jars
     * @param jarName
     *            Le nom du jar manquant
     */
    private void registerMissingJar(final String regExp, final String jarName) {
        missingJars.add("Missing jar [" + jarName + "] > [" + regExp + "]");
    }

    /**
     * Setter de l'attribut debug.
     * 
     * @param debug
     *            L'attribut debug.
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Setter de l'attribut debug.
     * 
     * @param value
     *            the new debug
     */
    public void setDebug(final String value) {
        final String mode = System.getProperty("debug");
        if (!isEmpty(mode)) {
            if ("on".equalsIgnoreCase(mode) || "oui".equalsIgnoreCase(mode) || "o".equalsIgnoreCase(mode) || "yes".equalsIgnoreCase(mode)
                    || "y".equalsIgnoreCase(mode) || "1".equalsIgnoreCase(mode) || "true".equalsIgnoreCase(mode)) {
                debug = true;
            }
        }
    }

    /**
     * Setter de l'attribut jarBaseName.
     * 
     * @param jarBaseName
     *            L'attribut jarBaseName.
     */
    public void setJarBaseName(final String jarBaseName) {
        this.jarBaseName = jarBaseName;
    }

    /**
     * Setter de l'attribut projectName.
     * 
     * @param projectName
     *            L'attribut projectName.
     */
    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    /**
     * Setter de l'attribut stopIfMissingJars.
     * 
     * @param stopIfMissingJars
     *            L'attribut stopIfMissingJars.
     */
    public void setStopIfMissingJars(final boolean stopIfMissingJars) {
        this.stopIfMissingJars = stopIfMissingJars;
    }

    /**
     * Mise à jour du classpath courant par ajout des jars spécifiés (s'ils ne
     * sont pas déjà présents au sein du classpath).
     * 
     * @param neededJars
     *            Liste des jars à charger
     * @throws SonosException
     *             Error
     */
    protected void updateCurrentClasspath(final List<File> neededJars) throws SonosException {
        File jar = null;
        final Iterator<File> itr = neededJars.iterator();
        final Method m = getAddUrlMethod();
        final URLClassLoader l = getURLClassLoader();
        final List<String> alreadyLoadedJars = getAlreadyLoadedJar(l);
        while (itr.hasNext()) {
            jar = itr.next();
            if (!jar.exists()) {
                throw new SonosException("Expected jar not found [" + jar.getAbsolutePath() + "]");
            }
            // CHECKSTYLE:OFF TODO later
            if ((alreadyLoadedJars != null) && alreadyLoadedJars.contains(jar.getAbsolutePath())) {
                // doublon
                // CHECKSTYLE:ON
            } else {
                try {
                    m.invoke(l, new Object[] { jar.toURL() });
                } catch (final IllegalArgumentException e) {
                    throw new SonosException(e);
                } catch (final MalformedURLException e) {
                    throw new SonosException(e);
                } catch (final IllegalAccessException e) {
                    throw new SonosException(e);
                } catch (final InvocationTargetException e) {
                    throw new SonosException(e);
                }
            }
        }
    }
}
