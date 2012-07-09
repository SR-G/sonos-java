package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;

/**
 * The Class CommandCrossfadeOff.
 */
public class CommandCrossfadeOff extends AbstractCommand implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ISonos sonos) throws SonosException {
        sonos.setCrossfade(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Deactivate cross fading";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "crossfadeoff";
    }

}
