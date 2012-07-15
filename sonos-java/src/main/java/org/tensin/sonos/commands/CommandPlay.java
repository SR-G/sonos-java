package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.model.PlayMode;

/**
 * The Class CommandPlay.
 */
public class CommandPlay extends AbstractCommand implements IZoneCommand {

    /** The Constant NB_REPETITIONS. */
    private static final int NB_REPETITIONS = 1;

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        sonos.getMediaRendererDevice().getRenderingControlService().setMute(false);
        sonos.getMediaRendererDevice().getAvTransportService().setPlayMode(PlayMode.NORMAL);
        for (int i = 0; i < NB_REPETITIONS; i++) {
            sonos.getMediaRendererDevice().getAvTransportService().play();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Starts playing";
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
