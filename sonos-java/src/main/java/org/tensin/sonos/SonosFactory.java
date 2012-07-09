package org.tensin.sonos;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.tensin.sonos.upnp.SonosException;

/**
 * A factory for creating Sonos objects.
 * TODO Get rid of all those factories and switch to Google Guice 3.0
 */
public class SonosFactory {

    /** The i sonos class. */
    private static Class<? extends ISonos> iSonosClass = SonosImpl.class;

    /**
     * Builds the.
     * 
     * @param host
     *            the host
     * @return the i sonos
     * @throws SonosException
     *             the sonos exception
     */
    public static ISonos build(final String host) throws SonosException {
        try {
            Constructor<? extends ISonos> constructor = iSonosClass.getConstructor(new Class[] { String.class });
            return constructor.newInstance(new String[] { host });
        } catch (IllegalArgumentException e) {
            throw new SonosException(e);
        } catch (InstantiationException e) {
            throw new SonosException(e);
        } catch (IllegalAccessException e) {
            throw new SonosException(e);
        } catch (InvocationTargetException e) {
            throw new SonosException(e);
        } catch (SecurityException e) {
            throw new SonosException(e);
        } catch (NoSuchMethodException e) {
            throw new SonosException(e);
        }

    }

    /**
     * Sets the i sonos class.
     * 
     * @param iSonosClass
     *            the new i sonos class
     */
    public static void setiSonosClass(final Class<? extends ISonos> iSonosClass) {
        SonosFactory.iSonosClass = iSonosClass;
    }

}
