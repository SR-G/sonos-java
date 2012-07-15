package org.tensin.sonos;

import org.tensin.sonos.commander.WebController;

/**
 * The Class StartWeb.
 */
public class StartWeb {

    /**
     * Activate mocks.
     */
    private static final void activateMocks() {
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        // activateMocks();
        WebController.main(new String[] {});
    }
}