package org.tensin.common.tools.boot.updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterLTPXml.
 * 
 * @author u248663
 * @version $Revision: 1.2 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterLTPXml implements IAdapterInput, IAdapterOutput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterLTPXml.class);

    /**
     * Méthode.
     * 
     * @param destFileName
     *            the dest file name
     * @return the adapter ltp xml
     */
    public static AdapterLTPXml buildAdapter(final String destFileName) {
        final AdapterLTPXml adapter = new AdapterLTPXml();
        adapter.setDestFileName(destFileName);
        return adapter;
    }

    /** destFileName. */
    private String destFileName;

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

        final Element racine = new Element("project");
        final Document document = new Document(racine);

        final Element dependencies = new Element("dependencies");
        racine.addContent(dependencies);

        for (final JarContainer jar : JarContainer.fusion(jars)) {
            final Element dependency = new Element("dependency");
            dependencies.addContent(dependency);

            String groupId = jar.getGroupId();
            if (StringUtils.isEmpty(groupId)) {
                groupId = JarContainer.getMavenGroupIdDatabase().get(jar.getArtifactId());
            }
            if (StringUtils.isEmpty(groupId)) {
                groupId = "";
            }
            dependency.setAttribute("groupId", groupId);
            dependency.setAttribute("artifactId", jar.getArtifactId());
            dependency.setAttribute("version", jar.getVersion());
            dependency.setAttribute("destination", updateDestinationWithStartConstants(jar));
            dependency.setAttribute("modules", jar.getModules());
            dependency.setAttribute("description", jar.getDescription());
            dependency.setAttribute("path", jar.getPath());
            dependency.setAttribute("key", jar.getKey());
            if (!jar.isActif()) {
                dependency.setAttribute("actif", String.valueOf(false));
            }
        }

        final XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        try {
            sortie.output(document, System.out);
        } catch (final IOException e) {
            throw new DependencyException(e);
        }
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
        return "Load and save from and to LTP internal XML file";
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

        final SAXBuilder sxb = new SAXBuilder();
        try {
            final Document document = sxb.build(new File(destFileName));
            final Element racine = document.getRootElement();
            final Element dependencies = racine.getChild("dependencies", racine.getNamespace());
            final List<Element> dependency = dependencies.getChildren("dependency", racine.getNamespace());
            for (final Element element : dependency) {
                final JarContainer jar = new JarContainer(AdapterLTPXml.class);
                jar.setGroupId(element.getAttributeValue("groupId", racine.getNamespace(), ""));
                jar.setArtifactId(element.getAttributeValue("artifactId", racine.getNamespace(), ""));
                jar.setVersion(element.getAttributeValue("version", racine.getNamespace(), ""));
                jar.setDescription(element.getAttributeValue("description", racine.getNamespace(), ""));
                jar.setDestinations(element.getAttributeValue("destination", racine.getNamespace(), ""));
                jar.setModules(element.getAttributeValue("modules", racine.getNamespace(), ""));
                jar.setPath(element.getAttributeValue("path", racine.getNamespace(), ""));
                jar.setKey(element.getAttributeValue("key", racine.getNamespace(), ""));
                jar.setActif(element.getAttributeValue("key", racine.getNamespace(), "true"));
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
     * Setter de l'attribut destFileName.
     * 
     * @param destFileName
     *            L'attribut destFileName.
     */
    public void setDestFileName(final String destFileName) {
        this.destFileName = destFileName;
    }

    /**
     * Update destination with start constants.
     * 
     * @param jar
     *            the jar
     * @return the string
     */
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
