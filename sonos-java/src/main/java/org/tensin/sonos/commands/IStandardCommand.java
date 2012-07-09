package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;

/**
 * The Interface ICommand.
 */
public interface IStandardCommand extends ICommand {

	/**
	 * Execute.
	 *
	 * @throws SonosException the sonos exception
	 */
	public void execute() throws SonosException;

}
