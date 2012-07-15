package org.tensin.sonos;

import org.tensin.sonos.commander.CLIController;

/**
 * The Class CmdPlaySalon.
 */
public class CmdBrowse {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        CLIController.main(new String[] { "--command", "browse", "A:TRACKS", "--zone", "salon" });
    }
}
