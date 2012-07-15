package org.tensin.sonos.commander;

import org.tensin.sonos.SonosException;

/**
 * The Interface ISonosController.
 */
public interface ISonosController {

    /**
     * Execute.
     * 
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    public abstract void execute(final String command) throws SonosException;

    /**
     * Execute.
     * 
     * @param zoneNames
     *            the zone names
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    public abstract void execute(final String zoneNames, final String command) throws SonosException;

}