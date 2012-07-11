package org.tensin.sonos;

import java.util.ArrayList;
import java.util.Collection;

import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.ISonosZonesDiscoverListener;

/**
 * The Class DiscoverMock.
 */
public class DiscoverMock implements IDiscover {

    /** The cb. */
    private ISonosZonesDiscoverListener cb;

    /** The zones. */
    private final Collection<String> zones = new ArrayList<String>();

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
    public DiscoverMock(final ISonosZonesDiscoverListener cb, final Integer controlPort) {
        this.cb = cb;
        eventAddNewZone("SALON");
        eventAddNewZone("CHAMBRE");
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
     * Event add new zone.
     * 
     * @param zoneName
     *            the zone name
     */
    public void eventAddNewZone(final String zoneName) {
        cb.found(zoneName);
        zones.add(zoneName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#getList()
     */
    @Override
    public String[] getList() {
        return zones.toArray(new String[] {});
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#launch()
     */
    @Override
    public void launch() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#setSsdpControlPort(int)
     */
    @Override
    public void setSsdpControlPort(final int ssdpControlPort) {

    }
}
