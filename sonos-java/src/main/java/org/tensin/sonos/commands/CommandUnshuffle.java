package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandUnshuffle extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        sonos.playmode(false, false);
    }

    @Override
    public String getName() {
        return "unshuffle";
    }

}
