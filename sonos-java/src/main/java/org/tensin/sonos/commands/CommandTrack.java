package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandTrack extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.seekTrack(Integer.parseInt(getArgs().get(0)));
        }
    }

    @Override
    public String getName() {
        return "track";
    }

}
