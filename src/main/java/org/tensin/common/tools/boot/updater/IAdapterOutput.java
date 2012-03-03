package org.tensin.common.tools.boot.updater;

import java.util.Collection;

/**
 * IAdapterOutput.
 * 
 * @author u248663
 * @version $Revision: 1.1 $
 * @since 19 mars 2010 11:19:46
 */
public interface IAdapterOutput {

    /**
     * Méthode.
     * 
     * @param jars
     *            the jars
     * @throws DependencyException
     *             the dependency exception
     */
    public void generate(final Collection<JarContainer> jars) throws DependencyException;

    /**
     * Méthode.
     * 
     * @return the name
     */
    public String getName();

}
