package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.upnp.SonosListener;

/**
 * The Class CommandList.
 */
public class CommandList extends AbstractCommand implements IZoneCommand, SonosListener {

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
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "list";
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
        System.out.println("(" + idx + ")\t    id: " + item.idURI);
        System.out.println("\t   res: " + item.playURI);
        System.out.println("\t title: " + item.title);
        if (item.album != null) {
            System.out.println("\t album: " + item.album);
        }
        if (item.artist != null) {
            System.out.println("\tartist: " + item.artist);
        }
    }
}
