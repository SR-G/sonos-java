package org.tensin.sonos.commands;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.Listener;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CommandDiscover.
 */
public class CommandDiscover implements IStandardCommand {

    /**
     * The listener interface for receiving zonesDiscovered events. The class
     * that is interested in processing a zonesDiscovered event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addZonesDiscoveredListener<code> method. When
     * the zonesDiscovered event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see ZonesDiscoveredEvent
     */
    class ZonesDiscoveredListener implements Listener {

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.Listener#found(java.lang.String)
         */
        @Override
        public void found(final String host) {
            try {
                ISonos sonos = SonosFactory.build(host);
                sonos.refreshZoneAttributes();
                String name = sonos.getZoneName();
                if (StringUtils.isNotEmpty(name)) {
                    LOGGER.info("Zone [" + name + "] discovered, IP [" + host + "]");
                    count++;
                }
            } catch (SonosException e) {
                LOGGER.error("Internal error while working on new found host [" + host + "]", e);
            }
        }
    }

    /** The count. */
    private int count = 0;

    /** The Constant MAX_DISCOVER_TIME_IN_MILLISECONDS. */
    private static final int MAX_DISCOVER_TIME_IN_MILLISECONDS = 2500;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDiscover.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IStandardCommand#execute()
     */
    @Override
    public void execute() throws SonosException {
        IDiscover d = DiscoverFactory.build(new ZonesDiscoveredListener());
        d.launch();
        try {
            Thread.sleep(MAX_DISCOVER_TIME_IN_MILLISECONDS);
        } catch (InterruptedException x) {
        }
        d.done();
        LOGGER.info(count + " Sonos zones found on Network in " + MAX_DISCOVER_TIME_IN_MILLISECONDS + "ms.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "discover";
    }

}
