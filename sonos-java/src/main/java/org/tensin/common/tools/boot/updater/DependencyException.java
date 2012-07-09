package org.tensin.common.tools.boot.updater;

import org.tensin.sonos.SonosException;

/**
 * AdapterException.
 * 
 * @author u248663
 * @version $Revision: 1.1 $
 * @since 19 mars 2010 11:25:00
 */
public class DependencyException extends SonosException {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Renvoi LTPException.
     * 
     * @param e
     *            Throwable.
     * @throws DependencyException
     *             the dependency exception
     */
    public static final void throwLTPException(final Throwable e) throws DependencyException {
        throw new DependencyException(e.getMessage(), e);
    }

    /**
     * Constructeur.
     * 
     * @param message
     *            Message.
     */
    public DependencyException(final String message) {
        super(message);
    }

    /**
     * Constructeur.
     * 
     * @param message
     *            Message.
     * @param cause
     *            Cause.
     */
    public DependencyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur.
     * 
     * @param cause
     *            Cause.
     */
    public DependencyException(final Throwable cause) {
        super(cause);
    }

}
