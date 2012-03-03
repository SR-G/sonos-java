package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Discover;
import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandDiscover implements IStandardCommand {

	@Override
	public String getName() {
		return "discover";
	}

	@Override
	public void execute() throws SonosException {
		Discover d = new Discover();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException x) {
		}
		d.done();
		String[] list = d.getList();
		for (int n = 0; n < list.length; n++) {
			Sonos s = new Sonos(list[n]);
			String name = s.getZoneName();
			if (name != null)
				System.out.println(list[n] + " - " + name);
		}
	}

}
