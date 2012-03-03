package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandPlay extends AbstractCommand implements IZoneCommand {

	/** The Constant NB_REPETITIONS. */
	private static final int NB_REPETITIONS = 2;

	@Override
	public String getName() {
		return "play";
	}

	@Override
	public void execute(final Sonos sonos) throws SonosException {
		for ( int i = 0 ; i < NB_REPETITIONS ; i++ ) {
			sonos.play();
		}
	}

}
