package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandPause extends AbstractCommand implements IZoneCommand {

	@Override
	public String getName() {
		return "pause";
	}

	@Override
	public void execute(final ISonos sonos) throws SonosException {
		sonos.pause();
	}

}
