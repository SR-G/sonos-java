package org.tensin.sonos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.UDAServiceTypeHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.RemoteDeviceHelper;
import org.tensin.sonos.model.ZonePlayerModel;

/**
 * DeviceScanner handles Sonos device detection, registration and 
 * cleanup of stale devices.
 *
 * @author Mikkel Wendt-Larsen<phylock@gmail.com>
 */
public class DeviceScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceScanner.class);
    /**
     * The Constant DEFAULT_UDP_SEARCH_TIME.
     */
    private static final long DEFAULT_UDP_SEARCH_TIME = TimeUnit.MINUTES.toSeconds(2);
    private static final long STALE_DEVICE_REMOVEAL = TimeUnit.MINUTES.toMillis(5);
    /**
     * The upnp service.
     */
    private UpnpService upnpService = null;
    /**
     * The udp search time.
     */
    private int udpSearchTime = (int) DEFAULT_UDP_SEARCH_TIME;
    /**
     * set of listeners who wants to get notified about new devices or lost
     * devices
     */
    private final Set<DeviceScannerListener> listeners = new CopyOnWriteArraySet<DeviceScannerListener>();
    /**
     * The controller executor.
     */
    private final ScheduledExecutorService controllerExecutor;
    private final UPnPListener backendListener = new UPnPListener();
    private final ZonePlayerModel zonePlayers;
    /**
     * The zone player discoveries.
     */
    private final Map<String, Long> zonePlayerDiscoveries = new HashMap<String, Long>();

    private static final Set<String> IGNORED_DEVICES;

    static {
	Set<String> local_set = new TreeSet<String>();
	local_set.add("ZB100");
	local_set.add("BR100");
	IGNORED_DEVICES = Collections.unmodifiableSet(local_set);
    }

    public DeviceScanner(final ZonePlayerModel zonePlayers, final String name) {
	if (zonePlayers == null) {
	    throw new IllegalArgumentException("zonePlayers can't be null", new NullPointerException());
	}
	this.zonePlayers = zonePlayers;
	controllerExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
	    @Override
	    public Thread newThread(final Runnable r) {
		final Thread t = new Thread(r, name);
		t.setDaemon(true);
		return t;
	    }
	});
	controllerExecutor.scheduleAtFixedRate(new CleanUpTask(), 2, 2, TimeUnit.MINUTES);
    }

    public void addListener(DeviceScannerListener listener) {
	if (!listeners.contains(listener)) {
	    listeners.add(listener);
	}
    }

    public void removeListener(DeviceScannerListener listener) {
	if (listeners.contains(listener)) {
	    listeners.remove(listener);
	}
    }

    /**
     * Search for devices.
     */
    public void start() {
	if (upnpService == null) {
	    upnpService = new UpnpServiceImpl(backendListener);

	    // Send a search message to all devices and services, they should respond soon
	    final UDAServiceType udaType = new UDAServiceType(SonosConstants.AV_TRANSPORT);
	    upnpService.getControlPoint().search(new UDAServiceTypeHeader(udaType), udpSearchTime);
	}
    }

    public void stop() {
	if (upnpService != null) {
	    upnpService.shutdown();
	    upnpService = null;
	}
    }

    /**
     * Removes a zone player if it has the specified UDN.
     *
     * @param udn the udn
     */
    void removeZonePlayer(final String udn) {
	synchronized (zonePlayers) {
	    final ZonePlayer zp = zonePlayers.getById(udn);
	    if (zp != null) {
		LOGGER.info("Removing ZonePlayer " + udn + " " + zp.getRootDevice().getDetails().getModelDetails().getModelDescription());
		zonePlayers.remove(zp);

		controllerExecutor.execute(new Runnable() {
		    @Override
		    public void run() {
			for (DeviceScannerListener listener : listeners) {
			    listener.zonePlayerRemoved(zp);
			}
			zp.dispose();
		    }
		});
	    }
	}
    }

    void addZonePlayer(final RemoteDevice dev) {
	synchronized (zonePlayers) {
	    zonePlayerDiscoveries.put(dev.getIdentity().getUdn().getIdentifierString(), System.currentTimeMillis());

	    if (zonePlayers.containsZonePlayer(dev.getIdentity().getUdn().getIdentifierString())) {
		return;
	    }
	    // Ignore zone bridges
	    // TODO may need to implement cut down zone player for the zone bridge
	    // I believe the bridge only supports the following interfaces:
	    // - DeviceProperties
	    // - GroupManagement
	    // - SystemProperties
	    // - ZoneGroup
	    if(IGNORED_DEVICES.contains(dev.getDetails().getModelDetails().getModelNumber().toUpperCase()))
	    {
		return;
	    }
	    
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("Adding zone: " + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " " + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber());
	    }
	    try {
		//TODO: hide ZonePlayer Creation behind a factory - to handle specialiced cases
		final ZonePlayer zone = new ZonePlayer(upnpService, dev);
		zonePlayers.addZonePlayer(zone);
		controllerExecutor.execute(new Runnable() {
		    @Override
		    public void run() {
			for (DeviceScannerListener listener : listeners) {
			    listener.zonePlayerAdded(zone);
			}
		    }
		});
	    } catch (final Exception e) {
		LOGGER.error("Couldn't add zone" + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " " + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber(), e);
	    }
	}
    }

    public int getUdpSearchTime() {
	return udpSearchTime;
    }

    public void setUdpSearchTime(int udpSearchTime) {
	this.udpSearchTime = udpSearchTime;
    }

    /**
     * The listener.
     */
    private final class UPnPListener extends DefaultRegistryListener {

	/**
	 * Checks if is concerned device.
	 *
	 * @param device the device
	 * @return true, if is concerned device
	 */
	private boolean isConcernedDevice(final Device device) {
	    return device.getDetails().getManufacturerDetails().getManufacturer().toUpperCase().contains("SONOS");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceAdded(org.teleal.cling.registry.Registry,
	 * org.teleal.cling.model.meta.RemoteDevice)
	 */
	@Override
	public void deviceAdded(final Registry registry, final Device device) {
	    if (isConcernedDevice(device) && device instanceof RemoteDevice) {
		controllerExecutor.execute(new Runnable() {
		    /**
		     * {@inheritDoc}
		     *
		     * @see java.lang.Runnable#run()
		     */
		    @Override
		    public void run() {
			LOGGER.debug(RemoteDeviceHelper.dumpDevice(device));
			addZonePlayer((RemoteDevice) device);
		    }
		});
	    }
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceRemoved(org.teleal.cling.registry.Registry,
	 * org.teleal.cling.model.meta.RemoteDevice)
	 */
	@Override
	public void deviceRemoved(final Registry registry, final Device device) {
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
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceUpdated(org.teleal.cling.registry.Registry,
	 * org.teleal.cling.model.meta.RemoteDevice)
	 */
	@Override
	public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
	}
    };

    private final class CleanUpTask implements Runnable {

	@Override
	public void run() {
	    Long now = System.currentTimeMillis();
	    Iterator<Entry<String, Long>> iter = zonePlayerDiscoveries.entrySet().iterator();
	    while (iter.hasNext()) {
		Entry<String, Long> entry = iter.next();
		if ((now - entry.getValue()) > STALE_DEVICE_REMOVEAL) {
		    removeZonePlayer(entry.getKey());
		    iter.remove();
		}
	    }
	}
    }
}
