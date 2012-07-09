package org.tensin.sonos.commands;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.Listener;

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

    /** The discover control port. May be changed if needed. */
    private int discoverControlPort = SonosConstants.DEFAULT_SSDP_CONTROL_PORT;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDiscover.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IStandardCommand#execute()
     */
    @Override
    public void execute() throws SonosException {
        final IDiscover d = DiscoverFactory.build(new ZonesDiscoveredListener(), discoverControlPort);
        d.launch();
        try {
            Thread.sleep(SonosConstants.MAX_DISCOVER_TIME_IN_MILLISECONDS);
        } catch (InterruptedException x) {
        }
        d.done();
        LOGGER.info(count + " Sonos zones found on Network in " + SonosConstants.MAX_DISCOVER_TIME_IN_MILLISECONDS + "ms.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Discover every Sonos box on the network";
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

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#needArgs()
     */
    @Override
    public boolean needArgs() {
        return false;
    }

    /**
     * Sets the discover control port.
     * 
     * @param discoverControlPort
     *            the new discover control port
     */
    public void setDiscoverControlPort(final int discoverControlPort) {
        this.discoverControlPort = discoverControlPort;
    }

}
