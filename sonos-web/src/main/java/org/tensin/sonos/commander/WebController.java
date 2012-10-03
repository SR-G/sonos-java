package org.tensin.sonos.commander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.tensin.sonos.LogInitializer;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosJetty;
import org.tensin.sonos.SonosWebConstants;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.RemoteDeviceHelper;
import org.tensin.sonos.web.ISonosState;
import org.tensin.sonos.web.SonosState;

import com.beust.jcommander.Parameter;

/**
 * The Class WebCommander.
 */
public class WebController extends AbstractController implements ISonosController {

    /**
     * Creates the controller.
     * 
     * @return the i sonos controller
     */
    public static WebController createController() {
        return new WebController();
    }

    /** The sonos state. */
    private final ISonosState sonosState = SonosState.getInstance();

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = SonosState.getInstance().getZoneCommandDispatcher();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        final WebController webCommander = createController();
        webCommander.start(args);
    }

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
                LOGGER.debug(RemoteDeviceHelper.dumpRemoteDevice(device));
                final ZonePlayer zone = addZonePlayer(device);

                if (zone != null) {
                    final String name = zone.getDevicePropertiesService().getZoneAttributes().getName();
                    zoneCommandDispatcher.registerZoneAsAvailable(zone, name);
                    synchronized (sonosState.getZonesData()) {
                        final Object id = sonosState.getZonesData().addItem();
                        sonosState.getZonesData().getContainerProperty(id, "Name").setValue(name);
                        sonosState.loadMusicLibrary(zone, name);
                    }
                }
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
                removeZonePlayer(device.getIdentity().getUdn().toString());
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

    /** The port. */
    @Parameter(names = { "--http-port" }, description = "HTTP Port to run the web application (default : " + SonosWebConstants.DEFAULT_HTTP_PORT + ")")
    private int port = SonosWebConstants.DEFAULT_HTTP_PORT;

    /**
     * Instantiates a new web controller.
     */
    protected WebController() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String)
     */
    @Override
    public void execute(final String command) throws SonosException {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String, java.lang.String)
     */
    @Override
    public void execute(final String zoneNames, final String command) throws SonosException {
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

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Start.
     * 
     * @param args
     *            the args
     * @throws SonosException
     *             the sonos exception
     */
    public void start(final String[] args) throws SonosException {
        LogInitializer.initLog();
        final SonosJetty jetty = new SonosJetty();
        startDiscovery();
        jetty.setPort(getPort());
        jetty.start("src/main/webapp/", "/");
    }
}
