package org.tensin.sonos;

import org.apache.log4j.BasicConfigurator;
import org.tensin.common.EmbeddedJetty;
import org.tensin.sonos.upnp.SonosException;

public class StartWeb {

    public static void main(final String[] args) throws SonosException {
        BasicConfigurator.configure();
        EmbeddedJetty jetty = new EmbeddedJetty();
        jetty.start("src/main/webapp/", "/");
    }

}
