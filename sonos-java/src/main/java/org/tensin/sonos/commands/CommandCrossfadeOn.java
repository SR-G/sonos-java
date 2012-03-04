package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandCrossfadeOn extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        sonos.setCrossfade(false);
    }

    @Override
    public String getName() {
        return "crossfadeoff";
    }
}
