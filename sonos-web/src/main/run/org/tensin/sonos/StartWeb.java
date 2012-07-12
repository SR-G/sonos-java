package org.tensin.sonos;

import org.tensin.sonos.commander.WebCommander;

/**
 * The Class StartWeb.
 */
public class StartWeb {

    /**
     * Activate mocks.
     */
    private static final void activateMocks() {
        org.tensin.sonos.SonosFactory.setiSonosClass(org.tensin.sonos.SonosMock.class);
        org.tensin.sonos.upnp.DiscoverFactory.setiDiscoverClass(org.tensin.sonos.DiscoverMock.class);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        activateMocks();

        WebCommander.main(new String[] {});
    }
}