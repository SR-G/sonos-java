package org.tensin.common.tools.boot.updater;

import java.util.Collection;

/**
 * IAdapterInput.
 * 
 * @author u248663
 * @version $Revision: 1.1 $
 * @since 19 mars 2010 11:26:15
 */
public interface IAdapterInput {

    /**
     * Méthode.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Méthode.
     * 
     * @return the collection
     * @throws DependencyException
     *             the dependency exception
     */
    public Collection<JarContainer> load() throws DependencyException;
}
