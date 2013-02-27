package org.tensin.common.tools.boot.updater;

import java.util.Collection;

import org.tensin.common.tools.boot.updater.DependencyException;
import org.tensin.common.tools.boot.updater.IAdapterOutput;
import org.tensin.common.tools.boot.updater.JarContainer;

/**
 * AdapterApt.
 * 
 * @author u248663
 * @version $Revision: 1.1 $
 * @since 19 mars 2010 11:19:46
 */
public class AdapterApt implements IAdapterOutput {

    /**
     * Méthode.
     * 
     * @param destination
     *            the destination
     * @return the adapter apt
     */
    public static AdapterApt buildAdapter(final String destination) {
        final AdapterApt adapter = new AdapterApt();
        adapter.setDestination(destination);
        return adapter;
    }

    /** destination. */
    private String destination;

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
        final StringBuffer sb = new StringBuffer();
    }

    /**
     * Getter de l'attribut destination.
     * 
     * @return Returns L'attribut destination.
     */
    public String getDestination() {
        return destination;
    }

    /*
     * (non-Javadoc)
     * @see com.inetpsa.ltp.tools.excluded.IAdapterInput#getName()
     */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.tools.boot.updater.IAdapterOutput#getName()
     */
    @Override
    public String getName() {
        return "Save to APT file";
    }

    /**
     * Setter de l'attribut destination.
     * 
     * @param destination
     *            L'attribut destination.
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }
}
