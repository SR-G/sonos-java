package org.tensin.sonos.upnp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.tensin.sonos.upnp.DiscoverImpl.Listener;

/**
 * A factory for creating Discover objects.
 */
public class DiscoverFactory {

    /** The i discover class. */
    private static Class<? extends IDiscover> iDiscoverClass = DiscoverImpl.class;

    /**
     * Builds the.
     * 
     * @return the i discover
     * @throws SonosException
     *             the sonos exception
     */
    public static IDiscover build() throws SonosException {
        try {
            IDiscover discover = iDiscoverClass.newInstance();
            return discover;
        } catch (IllegalArgumentException e) {
            throw new SonosException(e);
        } catch (InstantiationException e) {
            throw new SonosException(e);
        } catch (IllegalAccessException e) {
            throw new SonosException(e);
        } catch (SecurityException e) {
            throw new SonosException(e);
        }

    };

    /**
     * Builds the.
     * 
     * @param listener
     *            the listener
     * @return the i discover
     * @throws SonosException
     *             the sonos exception
     */
    public static IDiscover build(final Listener listener) throws SonosException {
        try {
            Constructor<? extends IDiscover> constructor = iDiscoverClass.getConstructor(new Class[] { Listener.class });
            IDiscover discover = constructor.newInstance(new Object[] { listener });
            return discover;
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
     * Sets the i discover class.
     * 
     * @param iDiscoverClass
     *            the new i discover class
     */
    public static void setiDiscoverClass(final Class<? extends IDiscover> iDiscoverClass) {
        DiscoverFactory.iDiscoverClass = iDiscoverClass;
    }
}
