package org.tensin.sonos.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.upnp.ISonosVolumeListener;

/**
 * The Class CommandVolume.
 */
public class CommandVolume extends AbstractCommand implements IZoneCommand {

    /**
     * The listener interface for receiving consoleSonosVolume events.
     * The class that is interested in processing a consoleSonosVolume
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addConsoleSonosVolumeListener<code> method. When
     * the consoleSonosVolume event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see ConsoleSonosVolumeEvent
     */
    private final class ConsoleSonosVolumeListener implements ISonosVolumeListener {

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosVolumeListener#volumeDone(java.lang.String, int)
         */
        @Override
        public void volumeDone(final String zoneName, final int currentVolume) {
            LOGGER.info("Volume of zone [" + zoneName + "] = [" + currentVolume + "]");
        }

    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandVolume.class);

    /** The listener. */
    private ISonosVolumeListener listener = new ConsoleSonosVolumeListener();

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        if (!hasArgs()) {
            int n = sonos.getMediaRendererDevice().getRenderingControlService().getVolume();
            if (listener != null) {
                listener.volumeDone(sonos.getId(), n);
            }
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
     * Gets the listener.
     * 
     * @return the listener
     */
    public ISonosVolumeListener getListener() {
        return listener;
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

    /**
     * Sets the listener.
     * 
     * @param listener
     *            the new listener
     */
    public void setListener(final ISonosVolumeListener listener) {
        this.listener = listener;
    }
}
