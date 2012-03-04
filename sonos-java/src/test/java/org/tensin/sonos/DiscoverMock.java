package org.tensin.sonos;

import org.tensin.sonos.upnp.DiscoverImpl;
import org.tensin.sonos.upnp.IDiscover;

/**
 * The Class DiscoverMock.
 */
public class DiscoverMock implements IDiscover {

    /**
     * Instantiates a new discover.
     */
    public DiscoverMock() {
    }

    /**
     * Instantiates a new discover.
     * 
     * @param cb
     *            the cb
     */
    public DiscoverMock(final DiscoverImpl.Listener cb) {
        cb.found("SALON");
        cb.found("CHAMBRE");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#done()
     */
    @Override
    public void done() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#getList()
     */
    @Override
    public String[] getList() {
        return new String[] { "SALON", "CHAMBRE" };
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#launch()
     */
    @Override
    public void launch() {
    }
}
