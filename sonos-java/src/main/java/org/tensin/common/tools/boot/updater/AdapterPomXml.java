package org.tensin.common.tools.boot.updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterPomXml.
 * 
 * @author u248663
 * @version $Revision: 1.2 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterPomXml implements IAdapterInput, IAdapterOutput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterPomXml.class);

    /**
     * Méthode.
     * 
     * @param destFileName
     *            the dest file name
     * @return the adapter pom xml
     */
    public static AdapterPomXml buildAdapter(final String destFileName) {
        final AdapterPomXml adapter = new AdapterPomXml();
        adapter.setDestFileName(destFileName);
        return adapter;
    }

    /**
     * Méthode.
     * 
     * @param destFileName
     *            the dest file name
     * @param rootFileName
     *            the root file name
     * @return the adapter pom xml
     */
    public static AdapterPomXml buildAdapter(final String destFileName, final String rootFileName) {
        final AdapterPomXml adapter = new AdapterPomXml();
        adapter.setDestFileName(destFileName);
        adapter.setRootFileName(rootFileName);
        return adapter;
    }

    /** destFileName. */
    private String destFileName;

    /** rootFileName. */
    private String rootFileName;

    /**
     * Méthode. <dependency> <groupId>${pom.groupId}</groupId>
     * <artifactId>ddl-core</artifactId> <version>${pom.version}</version>
     * </dependency>
     * 
     * @param dependencies
     *            the dependencies
     * @return the string
     */
    private String dumpDependencies(final Collection<JarContainer> dependencies) {
        final StringBuffer sb = new StringBuffer();
        sb.append("<dependencies>").append("\n");
        JarContainer dependency;

        final Iterator<JarContainer> itr = dependencies.iterator();
        while (itr.hasNext()) {
            dependency = itr.next();
            if (!StringUtils.isEmpty(dependency.getGroupId())) {
                sb.append("\t<dependency>").append("\n");
                sb.append("\t\t<groupId>").append(dependency.getGroupId()).append("</groupId>").append("\n");
                sb.append("\t\t<artifactId>").append(dependency.getArtifactId()).append("</artifactId>").append("\n");
                sb.append("\t\t<version>").append(dependency.getVersion()).append("</version>").append("\n");
                sb.append("\t</dependency>").append("\n");
            }
        }

        sb.append("</dependencies>").append("\n");

        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterOutput#generate()
     */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.tools.boot.updater.IAdapterOutput#generate(java.util.Collection)
     */
    @Override
    public void generate(final Collection<JarContainer> jars) throws DependencyException {
        updatePomAutonomeDefs(jars, getDestFileName());
    }

    /**
     * Getter de l'attribut destFileName.
     * 
     * @return Returns L'attribut destFileName.
     */
    public String getDestFileName() {
        return destFileName;
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#getName()
     */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.tools.boot.updater.IAdapterInput#getName()
     */
    @Override
    public String getName() {
        return "Load and save from and to Maven POM XML file";
    }

    /**
     * Méthode getRootFileName.
     * 
     * @return String
     */
    public String getRootFileName() {
        return rootFileName;
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#load()
     */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.tools.boot.updater.IAdapterInput#load()
     */
    @Override
    public Collection<JarContainer> load() throws DependencyException {
        final List<JarContainer> result = new ArrayList<JarContainer>();
        final Map<String, String> properties = new HashMap<String, String>();

        loadPropertiesFromPom(properties, rootFileName);
        loadPropertiesFromPom(properties, destFileName);

        final SAXBuilder sxb = new SAXBuilder();
        try {
            final Document document = sxb.build(new File(destFileName));
            final Element racine = document.getRootElement();

            final Element eltDependencies = racine.getChild("dependencies", racine.getNamespace());
            final List<Element> eltDependency = eltDependencies.getChildren("dependency", racine.getNamespace());
            for (final Element element : eltDependency) {
                final JarContainer jar = new JarContainer(AdapterPomXml.class);
                jar.setGroupId(loadText(element, "groupId", properties));
                jar.setArtifactId(loadText(element, "artifactId", properties));
                jar.setVersion(loadText(element, "version", properties));
                result.add(jar);
            }

        } catch (final JDOMException e) {
            throw new DependencyException(e);
        } catch (final IOException e) {
            throw new DependencyException(e);
        }

        return result;
    }

    /**
     * Méthode loadPropertiesFromPom.
     * 
     * @param properties
     *            the properties
     * @param fileName
     *            the file name
     * @throws DependencyException
     *             the dependency exception
     */
    private void loadPropertiesFromPom(final Map<String, String> properties, final String fileName) throws DependencyException {
        final SAXBuilder sxb = new SAXBuilder();
        try {
            final Document document = sxb.build(new File(fileName));
            final Element racine = document.getRootElement();

            final Element eltProperties = racine.getChild("properties", racine.getNamespace());
            final List<Element> eltProperty = eltProperties.getChildren();
            for (final Element element : eltProperty) {
                properties.put(element.getName(), element.getValue());
            }
        } catch (final JDOMException e) {
            throw new DependencyException(e);
        } catch (final IOException e) {
            throw new DependencyException(e);
        }

    }

    /**
     * Méthode loadText.
     * 
     * @param element
     *            the element
     * @param key
     *            the key
     * @param properties
     *            the properties
     * @return String
     */
    private String loadText(final Element element, final String key, final Map<String, String> properties) {
        String s = element.getChildText(key, element.getNamespace());
        final Pattern p = Pattern.compile(".*\\$\\{([^\\}]*)\\}.*");
        final Matcher m = p.matcher(s);
        if (m.find()) {
            String propName = m.group();
            propName = propName.substring(2, propName.length() - 1);
            final String propValue = properties.get(propName);
            if (StringUtils.isNotEmpty(propValue)) {
                s = s.replace("${" + propName + "}", propValue);
            }

        }
        return s;
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
     * Méthode setRootFileName.
     * 
     * @param rootFileName
     *            void
     */
    public void setRootFileName(final String rootFileName) {
        this.rootFileName = rootFileName;
    }

    /**
     * Mise à jour d'une liste compatible POM.xml des jars à prendre en compte.
     * 
     * @param jars
     *            the jars
     * @param pomAutonomeDefsFileName
     *            the pom autonome defs file name
     */
    private void updatePomAutonomeDefs(final Collection<JarContainer> jars, final String pomAutonomeDefsFileName) {
        LOGGER.info("Updating : " + pomAutonomeDefsFileName);

        final Iterator<JarContainer> itr = jars.iterator();
        final Collection<JarContainer> dependencies = new ArrayList<JarContainer>();
        while (itr.hasNext()) {
            final JarContainer jar = itr.next();
            final String jarName = jar.getJarName();
            final JarContainer dependency = new JarContainer(AdapterPomXml.class);
            dependency.setJarName(jarName);
            dependencies.add(dependency);
        }

        LOGGER.info(dumpDependencies(dependencies));
    }
}