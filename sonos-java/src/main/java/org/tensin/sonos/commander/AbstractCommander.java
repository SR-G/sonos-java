package org.tensin.sonos.commander;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.DiscoverImpl.Listener;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class AbstractCommander.
 */
public abstract class AbstractCommander {

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

        /** The debug. */
        private boolean debug;

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.DiscoverImpl.Listener#found(java.lang.String)
         */
        @Override
        public void found(final String host) {
            try {
                ISonos sonos = SonosFactory.build(host);
                sonos.refreshZoneAttributes();
                String name = sonos.getZoneName();
                if (StringUtils.isNotEmpty(name)) {
                    LOGGER.info("New zone found [" + name + "]");
                    if (isDebug()) {
                        sonos.trace_io(true);
                        sonos.trace_reply(true);
                        sonos.trace_browse(true);
                    }
                    ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(sonos, name);
                    if (isWorkOnAllZones()) {
                        for (final IZoneCommand command : commandStackZone) {
                            ZoneCommandDispatcher.getInstance().dispatchCommand(command, name);
                        }
                    }
                }
            } catch (SonosException e) {
                LOGGER.error("Internal error while working on new found host [" + host + "]", e);
            }
        }

        /**
         * Checks if is debug.
         * 
         * @return true, if is debug
         */
        public boolean isDebug() {
            return debug;
        }

        /**
         * Sets the debug.
         * 
         * @param debug
         *            the new debug
         */
        public void setDebug(final boolean debug) {
            this.debug = debug;
        }
    }

    /** The command stack zone. */
    private Collection<IZoneCommand> commandStackZone;

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(AbstractCommander.class);

    /** The zones to work on. */
    private Collection<String> zonesToWorkOn;

    /** The work on all zones. */
    private boolean workOnAllZones = false;

    /** The init log. */
    private static boolean initLog;

    /** The discover. */
    private IDiscover discover;

    /**
     * Detect if work on all zones.
     */
    protected void detectIfWorkOnAllZones() {
        if ((zonesToWorkOn == null) || (zonesToWorkOn.size() == 0)) {
            // No zones provided on command line => we will work on every zone
            // found
            workOnAllZones = true;
        } else {
            final Iterator<String> itr = zonesToWorkOn.iterator();
            String s;
            while (itr.hasNext()) {
                s = itr.next();
                // ALL keyword has been specified, we'll work on every zone
                // found too
                if (s.equalsIgnoreCase("ALL")) {
                    itr.remove();
                    workOnAllZones = true;
                }
            }
        }
        if (workOnAllZones) {
            LOGGER.info("Working on all available zones");
        } else {
            LOGGER.info("Working on zones " + CollectionHelper.singleDump(zonesToWorkOn) + "");
        }
    }

    /**
     * Gets the command stack zone.
     * 
     * @return the command stack zone
     */
    public Collection<IZoneCommand> getCommandStackZone() {
        return commandStackZone;
    }

    /**
     * Gets the discover.
     * 
     * @return the discover
     */
    public IDiscover getDiscover() {
        return discover;
    }

    /**
     * Gets the zones to work on.
     * 
     * @return the zones to work on
     */
    public Collection<String> getZonesToWorkOn() {
        return zonesToWorkOn;
    }

    /**
     * Inits the log.
     */
    protected void initLog() {
        if (!initLog) {
            BasicConfigurator.configure();
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.setLevel(Level.INFO);
            initLog = true;
        }
    }

    /**
     * Checks if is work on all zones.
     * 
     * @return true, if is work on all zones
     */
    public boolean isWorkOnAllZones() {
        return workOnAllZones;
    }

    /**
     * Sets the command stack zone.
     * 
     * @param commandStackZone
     *            the new command stack zone
     */
    public void setCommandStackZone(final Collection<IZoneCommand> commandStackZone) {
        this.commandStackZone = commandStackZone;
    }

    /**
     * Sets the work on all zones.
     * 
     * @param workOnAllZones
     *            the new work on all zones
     */
    public void setWorkOnAllZones(final boolean workOnAllZones) {
        this.workOnAllZones = workOnAllZones;
    }

    /**
     * Sets the zones to work on.
     * 
     * @param zonesToWorkOn
     *            the new zones to work on
     */
    public void setZonesToWorkOn(final Collection<String> zonesToWorkOn) {
        this.zonesToWorkOn = zonesToWorkOn;
    }

    /**
     * Start discovery.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    protected void startDiscovery() throws SonosException {
        Listener zonesDiscoveredListener = new ZonesDiscoveredListener();
        discover = DiscoverFactory.build(zonesDiscoveredListener);
        discover.launch();
    }

    /**
     * Stop discovery.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    protected void stopDiscovery() throws SonosException {
        discover.done();
    }

}
