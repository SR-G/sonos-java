package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.StringUtils;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.LogInitializer;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.helpers.RemoteDeviceHelper;

/**
 * The Class JavaCommander.
 */
public class JavaController extends AbstractController implements ISonosController {

    /**
     * Creates the controller.
     * 
     * @return the i sonos controller
     */
    public static JavaController createController() {
        return new JavaController();
    }

    /** The controller executor. */
    private final Executor controllerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r, "SonosControllerThread");
            t.setDaemon(true);
            return t;
        }
    });

    /** The listener. */
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

                    /**
                     * {@inheritDoc}
                     * 
                     * @see java.lang.Runnable#run()
                     */
                    @Override
                    public void run() {
                        LOGGER.debug(RemoteDeviceHelper.dumpRemoteDevice(device));
                        final ZonePlayer zone = addZonePlayer(device);

                        if (zone != null) {
                            final String name = zone.getDevicePropertiesService().getZoneAttributes().getName();
                            zoneCommandDispatcher.registerZoneAsAvailable(zone, name);
                            if (isWorkOnAllZones()) {
                                // in "all zones" mode, commands have not yet been pushed (as we don't know the zone yet, we can't create ZoneCommandExecutor before), so
                                // we propage all needed command to the ZoneCommandDispatcher for him to propagate the command to the newly created ZonecommandExecutor
                                for (final IZoneCommand command : getCommandStackZone()) {
                                    zoneCommandDispatcher.dispatchCommand(command, name);
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

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaController.class);

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = ZoneCommandDispatcher.getInstance();

    /**
     * Instantiates a new java controller.
     */
    public JavaController() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String)
     */
    @Override
    public void execute(final String command) throws SonosException {
        execute("ALL", command);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String, java.lang.String)
     */
    @Override
    public void execute(final String zoneNames, final String command) throws SonosException {
        LogInitializer.initLog();
        if (StringUtils.isEmpty(command)) {
            LOGGER.error("No command provided, won't do anything");
        } else {
            try {
                startDiscovery();
                final Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
                setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
                setCommandStackStandard((Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class));
                executeStandardCommands();
                executeZoneCommands(zoneNames, new ArrayList<String>());
            } finally {
                shutdown();
                zoneCommandDispatcher.logSummary();
                zoneCommandDispatcher.stopExecutors();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.AbstractController#getListener()
     */
    @Override
    public RegistryListener getListener() {
        return listener;
    }

}
