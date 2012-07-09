package org.tensin.sonos;

import org.tensin.sonos.commander.JavaCommander;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CmdPlaySalon.
 */
public class JavaCmdDiscover {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        new JavaCommander().execute("discover");
    }
}
