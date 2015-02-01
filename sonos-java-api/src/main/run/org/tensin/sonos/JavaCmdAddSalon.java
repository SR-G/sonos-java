package org.tensin.sonos;

import org.tensin.sonos.commander.JavaController;
import org.tensin.sonos.helpers.LogHelper;

import com.google.common.collect.ImmutableList;

/**
 * The Class CmdPlaySalon.
 */
public class JavaCmdAddSalon {

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
        JavaController.createController().execute("chambre", "add", ImmutableList.of("//JUPITER/Z/test.mp3"));
    }
}
