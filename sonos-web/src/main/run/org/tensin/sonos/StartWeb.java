package org.tensin.sonos;

import org.apache.log4j.BasicConfigurator;
import org.tensin.sonos.upnp.SonosException;

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
        final SonosJetty jetty = new SonosJetty();
        jetty.setPort(8081);
        jetty.start("src/main/webapp/", "/");
    }
}