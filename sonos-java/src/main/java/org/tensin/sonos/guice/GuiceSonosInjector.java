package org.tensin.sonos.guice;

import com.google.inject.Injector;

/**
 * The Class GuiceInjector.
 * 
 * @author Serge SIMON - u248663
 * @version $Revision: 1.1 $
 * @since 19 avril 2012 10:15:12
 */
public class GuiceSonosInjector {

    /** The injector. */
    public static Injector injector = null;

    /**
     * Gets the single instance of GuiceInjector.
     * 
     * @return single instance of GuiceInjector
     */
    public static Injector getInstance() {
        return injector;
    }

    /**
     * Sets the instance.
     * 
     * @param i
     *            the new instance
     */
    public static void setInstance(final Injector i) {
        injector = i;
    }
}