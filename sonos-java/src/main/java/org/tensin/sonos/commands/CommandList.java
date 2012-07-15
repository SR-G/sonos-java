package org.tensin.sonos.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.upnp.ISonosBrowseListener;
import org.tensin.sonos.upnp.SonosItem;

/**
 * The Class CommandList.
 */
public class CommandList extends AbstractCommand implements IZoneCommand {

    /**
     * The listener interface for receiving consolerSonos events.
     * The class that is interested in processing a consolerSonos
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addConsolerSonosListener<code> method. When
     * the consolerSonos event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see ConsolerSonosEvent
     */
    private static final class ConsoleSonosBrowseListener implements ISonosBrowseListener {

        /** The Constant LOGGER. */
        private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleSonosBrowseListener.class);

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosBrowseListener#updateDone(java.lang.String)
         */
        @Override
        public void updateDone(final String id) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosBrowseListener#updateItem(java.lang.String, int, org.tensin.sonos.upnp.SonosItem)
         */
        @Override
        public void updateItem(final String id, final int idx, final SonosItem item) {
            final StringBuilder sb = new StringBuilder();
            sb.append("(" + idx + ")\t    id: " + item.idURI);
            sb.append("\t   res: " + item.playURI);
            sb.append("\t title: " + item.title);
            if (item.album != null) {
                sb.append("\t album: " + item.album);
            }
            if (item.artist != null) {
                sb.append("\tartist: " + item.artist);
            }
            LOGGER.info(sb.toString());
        }
    }

    /** The listener. */
    private ISonosBrowseListener listener = new ConsoleSonosBrowseListener();

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        if (hasArgs()) {
            // sonos.browse(getArgs().get(0), listener);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Browse the sonos informations";
    }

    /**
     * Gets the listener.
     * 
     * @return the listener
     */
    public ISonosBrowseListener getListener() {
        return listener;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "list";
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
    public void setListener(final ISonosBrowseListener listener) {
        this.listener = listener;
    }

}
