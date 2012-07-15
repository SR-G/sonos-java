package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.UDAServiceTypeHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.tensin.sonos.LogInitializer;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.AbstractCommand;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZoneGroupTopologyListener;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.helpers.RemoteDeviceHelper;
import org.tensin.sonos.model.ZoneGroup;
import org.tensin.sonos.model.ZoneGroupState;
import org.tensin.sonos.model.ZoneGroupStateModel;
import org.tensin.sonos.model.ZonePlayerModel;

/**
 * The Class AbstractCommander.
 */
public abstract class AbstractCommander implements ZoneGroupTopologyListener {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommander.class);

    /** The controller executor. */
    private final Executor controllerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(final Runnable r) {
            Thread t = new Thread(r, "SonosControllerThread");
            t.setDaemon(true);
            return t;
        }
    });

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

    /** The listener. UPnP discovery is asynchronous, we need a callback */
    private final RegistryListener listener = new RegistryListener() {

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#afterShutdown()
         */
        @Override
        public void afterShutdown() {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#beforeShutdown(org.teleal.cling.registry.Registry)
         */
        @Override
        public void beforeShutdown(final Registry registry) {
        }

        /**
         * Checks if is concerned device.
         * 
         * @param device
         *            the device
         * @return true, if is concerned device
         */
        private boolean isConcernedDevice(final RemoteDevice device) {
            return device.getDetails().getManufacturerDetails().getManufacturer().toUpperCase().contains("SONOS");
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#localDeviceAdded(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.LocalDevice)
         */
        @Override
        public void localDeviceAdded(final Registry registry, final LocalDevice device) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#localDeviceRemoved(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.LocalDevice)
         */
        @Override
        public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#remoteDeviceAdded(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
         */
        @Override
        public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
            if (isConcernedDevice(device)) {
                controllerExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.debug(RemoteDeviceHelper.dumpRemoteDevice(device));
                        ZonePlayer zone = addZonePlayer(device);

                        if (zone != null) {
                            final String name = zone.getDevicePropertiesService().getZoneAttributes().getName();
                            ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(zone, name);
                            if (isWorkOnAllZones()) {
                                // in "all zones" mode, commands have not yet been pushed (as we don't know the zone yet, we can't create ZoneCommandExecutor before), so
                                // we propage all needed command to the ZoneCommandDispatcher for him to propagate the command to the newly created ZonecommandExecutor
                                for (final IZoneCommand command : getCommandStackZone()) {
                                    ZoneCommandDispatcher.getInstance().dispatchCommand(command, name);
                                }
                            }
                        }
                    }
                });
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#remoteDeviceDiscoveryFailed(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice, java.lang.Exception)
         */
        @Override
        public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#remoteDeviceDiscoveryStarted(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
         */
        @Override
        public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#remoteDeviceRemoved(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
         */
        @Override
        public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
            if (isConcernedDevice(device)) {
                controllerExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        removeZonePlayer(device.getIdentity().getUdn().toString());
                        LOGGER.info("Device [" + device.getIdentity().getUdn().toString() + "] disconnected");
                    }
                });
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teleal.cling.registry.RegistryListener#remoteDeviceUpdated(org.teleal.cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
         */
        @Override
        public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
        }
    };

    /** The command stack zone. */
    private Collection<IZoneCommand> commandStackZone;

    /** The command stack standard. */
    private Collection<IStandardCommand> commandStackStandard;

    /**
     * Creates a new ZonePlayer from the given device and adds it to our list.
     * 
     * @param dev
     *            the dev
     */
    private ZonePlayer addZonePlayer(final RemoteDevice dev) {
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
                LOGGER.info("Adding zone: " + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " "
                        + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber());
            }
            try {
                final ZonePlayer zone = new ZonePlayer(upnpService, dev);
                zonePlayers.addZonePlayer(zone);
                zone.getZoneGroupTopologyService().addZoneGroupTopologyListener(this);
                zoneGroupTopologyChanged(zone.getZoneGroupTopologyService().getGroupState());
                return zone;
            } catch (Exception e) {
                LOGGER.error("Couldn't add zone" + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " "
                        + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber(), e);
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
            LOGGER.info("Working on zones " + CollectionHelper.singleDump(zonesToWorkOn) + "");
        }
    }

    /**
     * Dispose.
     */
    public void dispose() {
        // DiscoveryAdvertisement.getInstance().unRegisterEvent(DiscoveryAdvertisement.EVENT_SSDP_ALIVE, ZonePlayerConstants.SONOS_DEVICE_TYPE, discoveryHandler);
        // DiscoveryAdvertisement.getInstance()
        // .unRegisterEvent(DiscoveryAdvertisement.EVENT_SSDP_BYE_BYE, ZonePlayerConstants.SONOS_DEVICE_TYPE, discoveryHandler);
        // DiscoveryListener.getInstance().unRegisterResultsHandler(discoveryHandler, ZonePlayerConstants.SONOS_DEVICE_TYPE);
        // synchronized (zonePlayers) {
        // for (ZonePlayer zp : zonePlayers.getAllZones()) {
        // zp.dispose();
        // }
        // }
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
        LogInitializer.initLog();
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
                startDiscovery();
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
                // stopDiscovery();
            }
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
     * Gets the controller executor.
     * 
     * @return the controller executor
     */
    public Executor getControllerExecutor() {
        return controllerExecutor;
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
        long now = System.currentTimeMillis();
        for (Entry<String, Long> zone : zonePlayerDiscoveries.entrySet()) {
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
    private void removeZonePlayer(final String udn) {
        synchronized (zonePlayers) {
            ZonePlayer zp = zonePlayers.getById(udn);
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
     * Search for devices.
     */
    public void startDiscovery() {
        upnpService = new UpnpServiceImpl(listener);
        // Send a search message to all devices and services, they should respond soon
        final UDAServiceType udaType = new UDAServiceType(SonosConstants.AV_TRANSPORT);
        upnpService.getControlPoint().search(new UDAServiceTypeHeader(udaType));
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
