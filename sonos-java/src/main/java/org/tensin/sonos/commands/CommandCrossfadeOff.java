package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

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
    public void execute(final ZonePlayer sonos) throws SonosException {
        sonos.getMediaRendererDevice().getRenderingControlService().setCrossFade(true);
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
