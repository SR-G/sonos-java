package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.UDAServiceTypeHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.RegistryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public abstract class AbstractController implements ZoneGroupTopologyListener {

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = ZoneCommandDispatcher.getInstance();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    /** The Constant DEFAULT_UDP_SEARCH_TIME. */
    private static final int DEFAULT_UDP_SEARCH_TIME = 120;

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

    /** The zone player discoveries. */
    private final Map<String, Long> zonePlayerDiscoveries = new HashMap<String, Long>();

    /** The upnp service. */
    private UpnpService upnpService;

    /** The command stack zone. */
    private Collection<IZoneCommand> commandStackZone;

    /** The command stack standard. */
    private Collection<IStandardCommand> commandStackStandard;

    /** The udp search time. */
    private int udpSearchTime = DEFAULT_UDP_SEARCH_TIME;

    /**
     * Creates a new ZonePlayer from the given device and adds it to our list.
     * 
     * @param dev
     *            the dev
     * @return the zone player
     */
    public ZonePlayer addZonePlayer(final RemoteDevice dev) {
        synchronized (zonePlayers) {
            zonePlayerDiscoveries.put(dev.getIdentity().getUdn().getIdentifierString().substring(5), System.currentTimeMillis());

            if (isZonePlayerAlreadyDefined(dev.getIdentity().getUdn().getIdentifierString())) {
                return null;
            }

            // Ignore zone bridges
            // TODO may need to implement cut down zone player for the zone bridge
            // I believe the bridge only supports the following interfaces:
            // - DeviceProperties
            // - GroupManagement
            // - SystemProperties
            // - ZoneGroup
            final Collection<String> ignoredDevicesModelName = new ArrayList<String>();
            ignoredDevicesModelName.add("ZB100");
            ignoredDevicesModelName.add("BR100");

            for (final String ignoredDeviceModelName : ignoredDevicesModelName) {
                if (dev.getDetails().getModelDetails().getModelNumber().toUpperCase().contains("ZB100")) {
                    // LOGGER.warn("Ignoring Zone " + dev.getDeviceType() + " " + dev.getModelDescription() + " " + dev.getModelName() + " " + dev.getModelNumber());
                    return null;
                }
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Adding zone: " + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " " + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber());
            }
            try {
                final ZonePlayer zone = new ZonePlayer(upnpService, dev);
                zonePlayers.addZonePlayer(zone);
                zone.getZoneGroupTopologyService().addZoneGroupTopologyListener(this);
                zoneGroupTopologyChanged(zone.getZoneGroupTopologyService().getGroupState());
                return zone;
            } catch (final Exception e) {
                LOGGER.error("Couldn't add zone" + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " " + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber(), e);
            }

            return null;
        }
    }

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

    public abstract RegistryListener getListener();

    /**
     * Gets the udp search time.
     * 
     * @return the udp search time
     */
    public int getUdpSearchTime() {
        return udpSearchTime;
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
     * Checks if is zone player already defined.
     * 
     * @param UDN
     *            the udn
     * @return true, if is zone player already defined
     */
    public boolean isZonePlayerAlreadyDefined(final String UDN) {
        final ZonePlayer other = getZonePlayerByUDN(UDN);
        return (other != null);
    }

    /**
     * Purge stale devices.
     * 
     * @param staleThreshold
     *            the stale threshold
     */
    public void purgeStaleDevices(final long staleThreshold) {
        final long now = System.currentTimeMillis();
        for (final Entry<String, Long> zone : zonePlayerDiscoveries.entrySet()) {
            if ((now - zone.getValue()) > staleThreshold) {
                removeZonePlayer(zone.getKey());
            }
        }
    }

    /**
     * Removes a zone player if it has the specified UDN.
     * 
     * @param udn
     *            the udn
     */
    public void removeZonePlayer(final String udn) {
        synchronized (zonePlayers) {
            final ZonePlayer zp = zonePlayers.getById(udn);
            if (zp != null) {
                LOGGER.info("Removing ZonePlayer " + udn + " " + zp.getRootDevice().getDetails().getModelDetails().getModelDescription());
                zonePlayers.remove(zp);
                zp.getZoneGroupTopologyService().removeZoneGroupTopologyListener(this);
                if (zonePlayers.getSize() == 0) {
                    zoneGroupTopologyChanged(new ZoneGroupState(Collections.EMPTY_LIST));
                }
                zp.dispose();
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
        this.udpSearchTime = udpSearchTime;
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
        upnpService.shutdown();
        LOGGER.info("Cleaning up internal resources");
        dispose();
    }

    /**
     * Search for devices.
     */
    public void startDiscovery() {
        upnpService = new UpnpServiceImpl(getListener());

        // Send a search message to all devices and services, they should respond soon
        final UDAServiceType udaType = new UDAServiceType(SonosConstants.AV_TRANSPORT);
        upnpService.getControlPoint().search(new UDAServiceTypeHeader(udaType), getUdpSearchTime());
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
}
