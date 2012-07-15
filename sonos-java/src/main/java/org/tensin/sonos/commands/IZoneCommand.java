package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Interface ICommand.
 */
public interface IZoneCommand extends ICommand {

    /**
     * Execute.
     * 
     * @param sonos
     *            the sonos
     * @throws SonosException
     *             the sonos exception
     */
    public void execute(final ZonePlayer sonos) throws SonosException;

}
