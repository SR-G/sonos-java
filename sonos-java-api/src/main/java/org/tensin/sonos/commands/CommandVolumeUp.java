package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Class CommandVolumeUp.
 */
public class CommandVolumeUp extends AbstractCommand implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        int previousVolume = sonos.getMediaRendererDevice().getRenderingControlService().getVolume();
        previousVolume = previousVolume + 5;
        if (previousVolume > 100) {
            previousVolume = 100;
        }
        sonos.getMediaRendererDevice().getRenderingControlService().setVolume(previousVolume);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Up volume by 5";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "up";
    }

}
