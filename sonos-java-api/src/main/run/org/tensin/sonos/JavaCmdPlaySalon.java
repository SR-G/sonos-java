package org.tensin.sonos;

import org.tensin.sonos.commander.JavaController;
import org.tensin.sonos.helpers.LogHelper;

/**
 * The Class CmdPlaySalon.
 */
public class JavaCmdPlaySalon {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        LogHelper.initLoggers();
        JavaController.createController().execute("chambre", "play");
    }
}
