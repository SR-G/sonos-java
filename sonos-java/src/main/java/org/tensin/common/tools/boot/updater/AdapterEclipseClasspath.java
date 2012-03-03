package org.tensin.common.tools.boot.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * AdapterEclipseClasspath.
 * 
 * @author u248663
 * @version $Revision: 1.1 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterEclipseClasspath implements IAdapterInput, IAdapterOutput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterEclipseClasspath.class);

    /** ECLIPSE_CLASSPATH. */
    public static final String DEFAULT_ECLIPSE_CLASSPATH = ".classpath";

    /**
     * Méthode.
     * 
     * @param eclipseClasspath
     *            the eclipse classpath
     * @return the adapter eclipse classpath
     */
    public static AdapterEclipseClasspath buildAdapter(final String eclipseClasspath) {
        AdapterEclipseClasspath adapter = new AdapterEclipseClasspath();
        adapter.setEclipseClassPath(eclipseClasspath);
        return adapter;
    }

    /** eclipseClassPath. */
    private String eclipseClassPath = DEFAULT_ECLIPSE_CLASSPATH;

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterOutput#generate()
     */
    public void generate(final Collection<JarContainer> jars) throws DependencyException {
        for (JarContainer jar : jars) {

        }

    }

    /**
     * Getter de l'attribut eclipseClassPath.
     * 
     * @return Returns L'attribut eclipseClassPath.
     */
    public String getEclipseClassPath() {
        return eclipseClassPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#getName()
     */
    public String getName() {
        return "Load and save from and to eclipse .classpath XML file";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#load()
     */
    public Collection<JarContainer> load() throws DependencyException {
        return loadClasspathContent();
    }

    /**
     * Charge les éléments issus du classpath Eclipse.
     * 
     * @return List
     * @throws DependencyException
     *             the dependency exception
     */
    private Collection<JarContainer> loadClasspathContent() throws DependencyException {
        return loadClasspathContent(getEclipseClassPath());
    }

    /**
     * Charge les éléments issus d'un fichier XML classpath.
     * 
     * @param path
     *            Le path vers le fichier XML (format Eclipse) à analyser
     * @return La liste des éléments trouvés
     * @throws DependencyException
     *             the dependency exception
     */
    private Collection<JarContainer> loadClasspathContent(final String path) throws DependencyException {
        List<JarContainer> result = new ArrayList<JarContainer>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser xmlParser = null;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);

            xmlParser = factory.newSAXParser();
            EclipseClasspathHandler xmlHandler = new EclipseClasspathHandler();
            xmlParser.parse(fis, xmlHandler);

            result = xmlHandler.getResults();
            Collections.sort(result);
        } catch (ParserConfigurationException e) {
            throw new DependencyException(e);
        } catch (SAXException e) {
            throw new DependencyException(e);
        } catch (IOException e) {
            throw new DependencyException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.info("Erreur sur fermeture ressources" + e.getMessage());
                }
                fis = null;
            }
        }
        return result;
    }

    /**
     * Setter de l'attribut eclipseClassPath.
     * 
     * @param eclipseClassPath
     *            L'attribut eclipseClassPath.
     */
    public void setEclipseClassPath(final String eclipseClassPath) {
        this.eclipseClassPath = eclipseClassPath;
    }

}

/**
 * Handler SAX pour interprêter un fichier XML type classpath Eclipse (.classpath)
 * 
 * Exemple : <?xml version="1.0" encoding="UTF-8"?> <classpath> <classpathentry kind="src" path="source/java"/> <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/> <classpathentry combineaccessrules="false"
 * kind="src" path="/PYRENV-JEditLauncher"/> <classpathentry kind="lib" path="/PYRENV-Root/libext/jedit-4.2.jar"/> <classpathentry kind="output" path="bin"/> </classpath>
 * 
 * @author Serge SIMON - u248663
 * @version $Revision: 1.1 $
 * @since Dec 22, 2007 5:50:46 PM
 * 
 */
class EclipseClasspathHandler extends DefaultHandler {

    /** XML_CLASSPATH_BALISE */
    private static final String XML_CLASSPATH_BALISE = "classpath";
    /** XML_CLASSPATHENTRY_BALISE */
    private static final String XML_CLASSPATHENTRY_BALISE = "classpathentry";
    /** XML_CLASSPATHENTRY_ATTRIBUTE_KIND */
    private static final String XML_CLASSPATHENTRY_ATTRIBUTE_KIND = "kind";
    /** XML_CLASSPATHENTRY_ATTRIBUTE_PATH */
    private static final String XML_CLASSPATHENTRY_ATTRIBUTE_PATH = "path";

    /** results */
    private final List<JarContainer> results = new ArrayList<JarContainer>();

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(final SAXParseException exception) throws SAXException {
        printSAXException(exception);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(final SAXParseException exception) throws SAXException {
        String message = "*** Erreur fatale ***\n";
        message += message(exception);
        SAXException se = new SAXException(message, exception);
        throw se;
    }

    /**
     * Méthode retournant la liste des résultats.
     * 
     * @return La liste des résultats (List)
     */
    public List<JarContainer> getResults() {
        return results;
    }

    /**
     * Construction d'un message SAX
     * 
     * @param e
     *            L'exception SAX
     * @return Le message formaté suite à l'erreur SAX
     */
    protected String message(final SAXParseException e) {
        String message = "Message : " + e.getMessage() + "\n";
        message += "Ligne " + e.getLineNumber() + ", colonne " + e.getColumnNumber() + "\n";
        message += "Public id : " + e.getPublicId() + "\n";
        message += "System id : " + e.getSystemId();
        return message;
    }

    /**
     * Affichage d'une erreur SAX
     * 
     * @param e
     *            L'exception SAX à traiter
     */
    protected void printSAXException(final SAXParseException e) {
        if (e.getException() != null) {
            e.getException().printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        // CHECKSTYLE:OFF later
        if (qName.equalsIgnoreCase(XML_CLASSPATH_BALISE)) {
            // Classpath
        } else if (qName.equalsIgnoreCase(XML_CLASSPATHENTRY_BALISE)) {
            // CHECKSTYLE:ON
            String kind = attributes.getValue(XML_CLASSPATHENTRY_ATTRIBUTE_KIND);
            if (kind.equalsIgnoreCase("lib")) {
                String path = attributes.getValue(XML_CLASSPATHENTRY_ATTRIBUTE_PATH);
                File f = new File(path);
                JarContainer jar = new JarContainer(AdapterEclipseClasspath.class);
                jar.setJarName(f.getName());
                results.add(jar);
            } else if (kind.equalsIgnoreCase("var")) {
                String path = attributes.getValue(XML_CLASSPATHENTRY_ATTRIBUTE_PATH);
                if (!StringUtils.isEmpty(path) && !path.endsWith("-sources.jar")) {
                    // results.add(path.replaceAll(".*/", ""));
                    // FIXME
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(final SAXParseException exception) throws SAXException {
        printSAXException(exception);
    }
}
