package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandNext extends AbstractCommand implements IZoneCommand {

	@Override
	public String getName() {
		return "next";
	}

	@Override
	public void execute(final Sonos sonos) throws SonosException {
		sonos.next();
	}

}
