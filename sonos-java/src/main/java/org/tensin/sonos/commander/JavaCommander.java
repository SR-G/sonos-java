package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.commands.AbstractCommand;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.Listener;

/**
 * The Class JavaCommander.
 */
public class JavaCommander extends AbstractCommander {

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
         * @see org.tensin.sonos.upnp.Listener#found(java.lang.String)
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
                        // in "all zones" mode, commands have not yet been pushed (as we don't know the zone yet, we can't create ZoneCommandExecutor before), so
                        // we propage all needed command to the ZoneCommandDispatcher for him to propagate the command to the newly created ZonecommandExecutor
                        for (final IZoneCommand command : getCommandStackZone()) {
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

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(JavaCommander.class);

    /** The debug. */
    private boolean debug;

    /** The discover. */
    private IDiscover discover;

    /** The zones to work on. */
    private Collection<String> zonesToWorkOn;

    /** The work on all zones. */
    private boolean workOnAllZones = false;

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
     * Execute.
     * 
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    public void execute(final String command) throws SonosException {
        execute("ALL", command);
    }

    /**
     * Execute.
     * 
     * @param zoneNames
     *            the zone names
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    public void execute(final String zoneNames, final String command) throws SonosException {
        initLog();
        if (StringUtils.isEmpty(command)) {
            LOGGER.error("No command provided, won't do anything");
        } else {
            try {
                final Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
                setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
                setCommandStackStandard((Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class));
                executeStandardCommands();
                executeZoneCommands(zoneNames, new ArrayList<String>());
            } finally {
                ZoneCommandDispatcher.getInstance().logSummary();
                ZoneCommandDispatcher.getInstance().stopExecutors();
            }
        }
    }

    /**
     * Execute standard commands.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public void executeStandardCommands() throws SonosException {
        if (!CollectionUtils.isEmpty(getCommandStackStandard())) {
            for (final IStandardCommand command : getCommandStackStandard()) {
                command.execute();
            }
        }
    }

    /**
     * Execute zone commands.
     * 
     * @param zone
     *            the zone
     * @param parameters
     *            the parameters
     * @throws SonosException
     *             the sonos exception
     */
    public void executeZoneCommands(final String zone, final List<String> parameters) throws SonosException {
        if (!CollectionUtils.isEmpty(getCommandStackZone())) {
            try {
                startDiscovery(debug);
                setZonesToWorkOn(CollectionHelper.convertStringToCollection(zone));
                detectIfWorkOnAllZones();
                if (!isWorkOnAllZones()) {
                    // We propagate immediately the command that have to be runned
                    // on a specific zone, even if that zone has still not be found
                    // (it's up to the discovery process to detect Sonos box and to
                    // fire up that event to the corresponding executor, allowing at
                    // that time the executor to run every command that are in its
                    // queue.
                    for (final IZoneCommand command : getCommandStackZone()) {
                        ((AbstractCommand) command).setArgs(parameters);
                        for (final String zoneToWorkOn : getZonesToWorkOn()) {
                            ZoneCommandDispatcher.getInstance().dispatchCommand(command, zoneToWorkOn);
                        }
                    }
                } else {
                    // Commands will be re-forwared from listener each time a new
                    // zones is discovered
                    // TODO purge command list after a certain amount of time, maybe
                    // ?
                }
                ZoneCommandDispatcher.getInstance().waitEndExecution(SonosConstants.DEFAULT_MAX_TIMEOUT_SONOS_COMMANDER_WHEN_WORKING_ON_ALL_ZONES,
                        !isWorkOnAllZones());
                // if "ALL" mode, then we don't want to check empty queues (are queues may be filled at a later time, once a new zone will be
                // discovered (and at this time, the wanted commands will be propagated by the listener)
            } finally {
                stopDiscovery();
            }
        }
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
     * Checks if is debug.
     * 
     * @return true, if is debug
     */
    public boolean isDebug() {
        return debug;
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
     * Sets the debug.
     * 
     * @param debug
     *            the new debug
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
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
     * @param debug
     *            the debug
     * @throws SonosException
     *             the sonos exception
     */
    protected void startDiscovery(final boolean debug) throws SonosException {
        final ZonesDiscoveredListener zonesDiscoveredListener = new ZonesDiscoveredListener();
        zonesDiscoveredListener.setDebug(debug);
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