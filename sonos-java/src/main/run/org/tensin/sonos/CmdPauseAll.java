package org.tensin.sonos;

import org.tensin.sonos.commander.CLIController;

/**
 * The Class CmdPauseAll.
 */
public class CmdPauseAll {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        CLIController.main(new String[] { "--command", "pause", "--zone", "salon,chambre,bureau" });
    }
}