package org.tensin.sonos;

import org.tensin.sonos.commander.JavaController;

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
        JavaController.createController().execute("discover");
    }
}
