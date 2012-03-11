package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CommandPlay.
 */
public class CommandPlay extends AbstractCommand implements IZoneCommand {

    /** The Constant NB_REPETITIONS. */
    private static final int NB_REPETITIONS = 2;

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ISonos sonos) throws SonosException {
        sonos.setMute(false);
        sonos.playmode(false, false);
        for (int i = 0; i < NB_REPETITIONS; i++) {
            sonos.play();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "play";
    }

}
