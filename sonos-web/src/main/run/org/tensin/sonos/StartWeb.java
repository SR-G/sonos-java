package org.tensin.sonos;

import org.tensin.sonos.commander.WebCommander;

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
        // SonosFactory.setiSonosClass(SonosMock.class);
        // DiscoverFactory.setiDiscoverClass(DiscoverMock.class);

        WebCommander.main(new String[] {});

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(SonosFactory.build("127.0.0.1"), "BUREAU");
    }
}