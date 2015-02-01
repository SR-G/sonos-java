package org.tensin.sonos;

import org.tensin.sonos.commander.CLIController;

/**
 * The Class CmdGetXPort.
 */
public class CmdGetXPort {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        CLIController.main(new String[] { "--command", "getxport", "--zone", "salon" });
    }
}