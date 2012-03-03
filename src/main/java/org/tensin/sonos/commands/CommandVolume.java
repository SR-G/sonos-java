package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandVolume extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final Sonos sonos) throws SonosException {
        if (!hasArgs()) {
            int n = sonos.volume();
            System.out.println("Volume of zone [" + sonos.getZoneName() + "] = [" + n + "]");
        } else {
            sonos.volume(Integer.parseInt(getArgs().get(0)));
        }
    }

    @Override
    public String getName() {
        return "volume";
    }

}
