package org.tensin.sonos.upnp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;

/**
 * A factory for creating Discover objects.
 */
public class DiscoverFactory {

    /** The i discover class. */
    private static Class<? extends IDiscover> iDiscoverClass = DiscoverImpl.class;

    /** discoverControlPort. */
    private static int discoverControlPort = SonosConstants.DEFAULT_SSDP_CONTROL_PORT;

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
    public static IDiscover build(final ISonosZonesDiscoverListener listener) throws SonosException {
        try {
            Constructor<? extends IDiscover> constructor = iDiscoverClass.getConstructor(new Class[] { ISonosZonesDiscoverListener.class, Integer.class });
            IDiscover discover = constructor.newInstance(new Object[] { listener, Integer.valueOf(discoverControlPort) });
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
     * Builds the.
     * 
     * @param listener
     *            the listener
     * @param port
     *            the port
     * @return the i discover
     * @throws SonosException
     *             the sonos exception
     */
    public static IDiscover build(final ISonosZonesDiscoverListener listener, final int port) throws SonosException {
        setDiscoverControlPort(port);
        return build(listener);
    }

    /**
     * Gets the discover control port.
     * 
     * @return the discover control port
     */
    public static int getDiscoverControlPort() {
        return discoverControlPort;
    }

    /**
     * Sets the discover control port.
     * 
     * @param discoverControlPort
     *            the new discover control port
     */
    public static void setDiscoverControlPort(final int discoverControlPort) {
        DiscoverFactory.discoverControlPort = discoverControlPort;
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
