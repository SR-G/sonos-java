package org.tensin.common.tools.boot.updater;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterWebInfLib.
 * 
 * @author u248663
 * @version $Revision: 1.1 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterWebInfLib implements IAdapterInput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterWebInfLib.class);

    /**
     * Méthode.
     * 
     * @param path
     *            the path
     * @return the adapter web inf lib
     */
    public static AdapterWebInfLib buildAdapter(final String path) {
        AdapterWebInfLib adapter = new AdapterWebInfLib();
        adapter.setPath(path);
        return adapter;
    }

    /** path. */
    private String path;

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#getName()
     */
    public String getName() {
        return "Load from web/WEB-INF/lib - or similar path - content";
    }

    /**
     * Getter de l'attribut path.
     * 
     * @return Returns L'attribut path.
     */
    public String getPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#load()
     */
    @SuppressWarnings("unchecked")
    public Collection<JarContainer> load() throws DependencyException {
        List<JarContainer> result = new ArrayList<JarContainer>();
        File fileWebLibs = new File(path).getAbsoluteFile();
        if (fileWebLibs.isDirectory()) {
            FilenameFilter filter = new FilenameFilterJar();
            File[] liste = fileWebLibs.listFiles(filter);
            JarContainer jar;
            for (int i = 0; i < liste.length; i++) {
                jar = new JarContainer(AdapterWebInfLib.class);
                jar.setJarName((liste[i]).getName());
                result.add(jar);
            }

            Collections.sort(result);
        } else {
            throw new DependencyException("Unknow path [" + fileWebLibs.getAbsolutePath() + "]");
        }
        return result;
    }

    /**
     * Setter de l'attribut path.
     * 
     * @param path
     *            L'attribut path.
     */
    public void setPath(final String path) {
        this.path = path;
    }
}