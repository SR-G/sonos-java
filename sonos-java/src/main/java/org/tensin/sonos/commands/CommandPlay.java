package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandPlay extends AbstractCommand implements IZoneCommand {

    /** The Constant NB_REPETITIONS. */
    private static final int NB_REPETITIONS = 2;

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        sonos.setMute(false);
        sonos.playmode(false, false);
        for (int i = 0; i < NB_REPETITIONS; i++) {
            sonos.play();
        }
    }

    @Override
    public String getName() {
        return "play";
    }

}
