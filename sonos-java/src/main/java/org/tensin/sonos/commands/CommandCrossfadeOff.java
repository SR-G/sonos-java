package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandCrossfadeOff extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        sonos.setCrossfade(true);
    }

    @Override
    public String getName() {
        return "crossfadeon";
    }
}
