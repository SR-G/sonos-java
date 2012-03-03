package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Interface ICommand.
 */
public interface IZoneCommand extends ICommand {

	/**
	 * Execute.
	 *
	 * @param sonos the sonos
	 * @throws SonosException the sonos exception
	 */
	public void execute(final ISonos sonos) throws SonosException;

}
