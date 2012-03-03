package org.tensin.sonos.commands;

import org.tensin.sonos.upnp.Sonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandSave extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final Sonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.save(getArgs().get(0), "SQ:3");
        }
    }

    @Override
    public String getName() {
        return "save";
    }

}
