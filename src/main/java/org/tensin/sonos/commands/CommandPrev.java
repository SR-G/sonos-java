package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandPrev extends AbstractCommand implements IZoneCommand {

	@Override
	public String getName() {
		return "prev";
	}

	@Override
	public void execute(final Sonos sonos) throws SonosException {
		sonos.prev();
	}

}
