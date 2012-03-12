package org.tensin.sonos;

import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CmdDiscover.
 */
public class CmdDiscover {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        SonosCommander.main(new String[] { "--command", "discover" });
    }
}