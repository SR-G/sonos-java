package org.tensin.sonos;

import org.tensin.sonos.commander.WebCommander;
import org.tensin.sonos.upnp.DiscoverFactory;

/**
 * The Class StartWeb.
 */
public class StartWeb {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        SonosFactory.setiSonosClass(SonosMock.class);
        DiscoverFactory.setiDiscoverClass(DiscoverMock.class);

        WebCommander.main(new String[] {});
    }
}