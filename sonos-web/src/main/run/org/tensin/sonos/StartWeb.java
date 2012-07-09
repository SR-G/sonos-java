package org.tensin.sonos;

import org.apache.log4j.BasicConfigurator;
import org.tensin.sonos.commands.ZoneCommandDispatcher;

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
        BasicConfigurator.configure();

        // SonosFactory.setiSonosClass(SonosMock.class);
        // DiscoverFactory.setiDiscoverClass(DiscoverMock.class);

        final SonosJetty jetty = new SonosJetty();
        jetty.setPort(8081);
        jetty.start("src/main/webapp/", "/");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(SonosFactory.build("127.0.0.1"), "BUREAU");
    }
}