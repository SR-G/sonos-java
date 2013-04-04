package org.tensin.sonos.commander;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.registry.RegistryListener;
import org.tensin.sonos.DeviceScanner;
import org.tensin.sonos.DeviceScannerListener;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.AbstractCommand;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZoneGroupTopologyListener;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.model.ZoneGroup;
import org.tensin.sonos.model.ZoneGroupState;
import org.tensin.sonos.model.ZoneGroupStateModel;
import org.tensin.sonos.model.ZonePlayerModel;

/**
 * The Class AbstractCommander.
 */
public abstract class AbstractController implements ZoneGroupTopologyListener, DeviceScannerListener {

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = ZoneCommandDispatcher.getInstance();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    /** The zones to work on. */
    private Collection<String> zonesToWorkOn;

    /** The work on all zones. */
    private boolean workOnAllZones = false;

    /** The debug. */
    private boolean debug;

    /** The groups. */
    private final ZoneGroupStateModel groups = new ZoneGroupStateModel();

    /** The zone players. */
    private final ZonePlayerModel zonePlayers = new ZonePlayerModel();

    /** The command stack zone. */
    private Collection<IZoneCommand> commandStackZone;

    /** The command stack standard. */
    private Collection<IStandardCommand> commandStackStandard;

    private final DeviceScanner scanner = new DeviceScanner(zonePlayers, getName());

    /**
     * Creates a new ZonePlayer from the given device and adds it to our list.
     * 
     * @param dev
     *            the dev
     * @return the zone player
     */
    @Override
    public void zonePlayerAdded(final ZonePlayer player) {
        synchronized (zonePlayers) {
            player.getZoneGroupTopologyService().addZoneGroupTopologyListener(this);
            zoneGroupTopologyChanged(player.getZoneGroupTopologyService().getGroupState());
        }
    }

    /**
     * Detect if work on all zones.
     */
    protected void detectIfWorkOnAllZones() {
        if ((zonesToWorkOn == null) || (zonesToWorkOn.isEmpty())) {
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
            LOGGER.info("Working on zones " + CollectionHelper.singleDump(zonesToWorkOn).toUpperCase() + "");
        }
    }

    /**
     * Dispose.
     */
    public void dispose() {
        synchronized (zonePlayers) {
            for (final ZonePlayer zp : zonePlayers.getAllZones()) {
                zp.dispose();
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
                        zoneCommandDispatcher.dispatchCommand(command, zoneToWorkOn);
                    }
                }
            } else {
                // Commands will be re-forwared from listener each time a new
                // zones is discovered
                // TODO purge command list after a certain amount of time, maybe
                // ?
            }
            zoneCommandDispatcher.waitEndExecution(SonosConstants.DEFAULT_MAX_TIMEOUT_SONOS_COMMANDER_WHEN_WORKING_ON_ALL_ZONES, !isWorkOnAllZones());
            // if "ALL" mode, then we don't want to check empty queues (as queues may be filled at a later time, once a new zone will be
            // discovered (and at this time, the wanted commands will be propagated by the listener))
        }
    }

    /**
     * Gets the command stack standard.
     * 
     * @return the command stack standard
     */
    public Collection<IStandardCommand> getCommandStackStandard() {
        return commandStackStandard;
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
     * Gets the coordinator for zone player.
     * 
     * @param zp
     *            a zone player
     * @return the coordinator of the zone player's group, or zp if it could not
     *         be discovered.
     */
    public ZonePlayer getCoordinatorForZonePlayer(final ZonePlayer zp) {
        if ((zp == null) || (zp.getZoneGroupTopologyService().getGroupState() == null)) {
            return zp;
        }
        for (final ZoneGroup zg : zp.getZoneGroupTopologyService().getGroupState().getGroups()) {
            if (zg.getMembers().contains(zp.getId())) {
                return getZonePlayerModel().getById(zg.getCoordinator());
            }
        }
        return zp;
    }

    /**
     * Gets the udp search time.
     * 
     * @return the udp search time
     */
    public int getUdpSearchTime() {
        return scanner.getUdpSearchTime();
    }

    /**
     * Gets the zone group state model.
     * 
     * @return the ZoneGroupStateModel
     */
    public ZoneGroupStateModel getZoneGroupStateModel() {
        return groups;
    }

    /**
     * Gets the zone player by udn.
     * 
     * @param UDN
     *            the udn
     * @return the zone player by udn
     */
    public ZonePlayer getZonePlayerByUDN(final String UDN) {
        for (final ZonePlayer zone : zonePlayers.getAllZones()) {
            if (zone.getRootDevice().getIdentity().getUdn().getIdentifierString().equals(UDN)) {
                return zone;
            }
        }
        return null;
    }

    /**
     * Gets the zone player model.
     * 
     * @return the ZonePlayerModel
     */
    public ZonePlayerModel getZonePlayerModel() {
        synchronized (zonePlayers) {
            return zonePlayers;
        }
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
     * Removes a zone player if it has the specified UDN.
     * 
     * @param udn
     *            the udn
     */
    @Override
    public void zonePlayerRemoved(final ZonePlayer player) {
        synchronized (zonePlayers) {
	    LOGGER.info("Removing" + player.toString());
	    player.getZoneGroupTopologyService().removeZoneGroupTopologyListener(this);
	    if (zonePlayers.getSize() == 0) {
		zoneGroupTopologyChanged(new ZoneGroupState(Collections.EMPTY_LIST));
	    }
        }
    }

    /**
     * Sets the command stack standard.
     * 
     * @param commandStackStandard
     *            the new command stack standard
     */
    public void setCommandStackStandard(final Collection<IStandardCommand> commandStackStandard) {
        this.commandStackStandard = commandStackStandard;
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
     * Sets the debug.
     * 
     * @param debug
     *            the new debug
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the udp search time.
     * 
     * @param udpSearchTime
     *            the new udp search time
     */
    public void setUdpSearchTime(final int udpSearchTime) {
        scanner.setUdpSearchTime(udpSearchTime);
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
     * Stop discovery.
     */
    public void shutdown() {
        LOGGER.info("Shutting down UPNP services and discovery");
        scanner.stop();
	scanner.removeListener(this);
        LOGGER.info("Cleaning up internal resources");
        dispose();
    }

    /**
     * Search for devices.
     */
    public void startDiscovery() {
	scanner.addListener(this);
        scanner.start();
    }

    /**
     * Zone group topology changed.
     * 
     * @param groupState
     *            the group state
     * @see org.tensin.sonos.control.ZoneGroupTopologyListener#valuesChanged()
     */
    @Override
    public synchronized void zoneGroupTopologyChanged(final ZoneGroupState groupState) {
        if (groupState == null) {
            return;
        }
        groups.handleGroupUpdate(groupState);
    }

    protected abstract String getName();
}
