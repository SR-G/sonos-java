package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandPrev extends AbstractCommand implements IZoneCommand {

	@Override
	public String getName() {
		return "prev";
	}

	@Override
	public void execute(final ISonos sonos) throws SonosException {
		sonos.prev();
	}

}
