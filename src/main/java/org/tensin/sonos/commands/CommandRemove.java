package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandRemove extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final Sonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.remove(getArgs().get(0));
        }
    }

    @Override
    public String getName() {
        return "remove";
    }

}
