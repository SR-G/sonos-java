package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;

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
    public void execute(final ISonos sonos) throws SonosException {
        int previousVolume = sonos.volume();
        previousVolume = previousVolume + 5;
        if (previousVolume > 100) {
            previousVolume = 100;
        }
        sonos.volume(previousVolume);
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
