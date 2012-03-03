package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandMove extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final Sonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.move(Integer.parseInt(getArgs().get(0)), Integer.parseInt(getArgs().get(1)));
        }
    }

    @Override
    public String getName() {
        return "move";
    }

}
