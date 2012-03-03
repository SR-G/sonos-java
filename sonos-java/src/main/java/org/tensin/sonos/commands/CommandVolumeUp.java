package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandVolumeUp extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        int previousVolume = sonos.volume();
        previousVolume = previousVolume + 5;
        if (previousVolume > 100) {
            previousVolume = 100;
        }
        sonos.volume(previousVolume);
    }

    @Override
    public String getName() {
        return "up";
    }

}
