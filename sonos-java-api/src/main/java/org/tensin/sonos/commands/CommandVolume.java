package org.tensin.sonos.commands;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Class CommandVolume.
 */
public class CommandVolume extends AbstractCommand implements IZoneCommand {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        if (!hasArgs()) {
            int n = sonos.getMediaRendererDevice().getRenderingControlService().getVolume();
            LOGGER.info("Volume is [" + n + "]");
        } else {
            sonos.getMediaRendererDevice().getRenderingControlService().setVolume(Integer.parseInt(getArgs().get(0)));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Set volume to a given level [0-100]";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "volume";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.AbstractCommand#needArgs()
     */
    @Override
    public boolean needArgs() {
        return true;
    }
}
