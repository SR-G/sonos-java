package org.tensin.sonos;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.ISonosZonesDiscoverListener;

/**
 * The Class DiscoverMock.
 */
public class DiscoverMock implements IDiscover {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverMock.class);

    /** The cb. */
    private ISonosZonesDiscoverListener cb;

    /** The zones. */
    private final Collection<String> zones = new ArrayList<String>();

    /** The random new zone adding. */
    private boolean randomNewZoneAdding = true;

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
     * @param controlPort
     *            the control port
     */
    public DiscoverMock(final ISonosZonesDiscoverListener cb, final Integer controlPort) {
        this();
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
        randomNewZoneAdding = false;
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
     * Inits the background test thread.
     */
    private void initBackgroundTestThread() {
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                int i = 1;
                while (randomNewZoneAdding) {
                    try {
                        Thread.sleep(5000);
                        eventAddNewZone("RANDOM NEW MOCKED ZONE " + i++);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error while adding random new mocked zone", e);
                    }
                }
            }

        }.start();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#launch()
     */
    @Override
    public void launch() {
        initBackgroundTestThread();
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
