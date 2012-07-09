package org.tensin.sonos.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.upnp.SonosListener;

/**
 * The Class CommandList.
 */
public class CommandList extends AbstractCommand implements IZoneCommand, SonosListener {

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(CommandList.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ISonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.browse(getArgs().get(0), this);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Browse the playlist";
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
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.SonosListener#updateDone(java.lang.String)
     */
    @Override
    public void updateDone(final String id) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.SonosListener#updateItem(java.lang.String, int, org.tensin.sonos.upnp.SonosItem)
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
