package org.tensin.common.tools.boot.updater;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdapterDump.
 * 
 * @author u248663
 * @version $Revision: 1.2 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterDump implements IAdapterOutput {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterDump.class);

    /**
     * Méthode.
     * 
     * @return the adapter dump
     */
    public static AdapterDump buildAdapter() {
        AdapterDump adapter = new AdapterDump();
        return adapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterOutput#generate()
     */
    public void generate(final Collection<JarContainer> jars) throws DependencyException {
        StringBuilder sb = new StringBuilder("Liste de tous les jars chargés :\n");
        if ((jars != null) && (jars.size() > 0)) {
            Iterator<JarContainer> itr = jars.iterator();
            JarContainer jar;
            while (itr.hasNext()) {
                jar = itr.next();
                sb.append("    ").append(jar.toString()).append("\n");
            }
        }
        LOGGER.info(sb.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#getName()
     */
    public String getName() {
        return "Dump items (for debug)";
    }

}
