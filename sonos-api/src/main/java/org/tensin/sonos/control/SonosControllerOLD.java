/*
 * Copyright 2007 David Wheeler
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.control;

/**
 * The main controller class that discovers the sonos devices, and provides
 * control over all the known zone player devices.
 * 
 * provides convenience methods for zone and zone player related activities.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class SonosControllerOLD {

    // /**
    // * A class to handle both types of discovery events.
    // *
    // * @author David WHEELER
    // *
    // */
    // private class DiscoveryHandler implements DiscoveryEventHandler, DiscoveryResultsHandler {
    // public void discoveredDevice(final String usn, final String udn, final String nt, final String maxAge, final URL location, final String firmware) {
    // controllerExecutor.execute(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // addZonePlayer(new RemoteDevice(location, maxAge, firmware));
    // } catch (MalformedURLException e) {
    // LOGGER.warn("Discovered device " + usn + " with invalid URL: " + location);
    // } catch (IllegalStateException e) {
    // LOGGER.warn("Discovered device of a too-recent version.");
    // }
    // }
    // });
    // }
    //
    // public void eventSSDPAlive(final String usn, final String udn, final String nt, final String maxAge, final URL location) {
    // controllerExecutor.execute(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // addZonePlayer(new RemoteDevice(location, maxAge));
    // } catch (MalformedURLException e) {
    // LOGGER.warn("Discovered device " + usn + " with invalid URL: " + location);
    // } catch (IllegalStateException e) {
    // LOGGER.warn("Discovered device of a too-recent version.");
    // }
    // }
    // });
    // }
    //
    // public void eventSSDPByeBye(final String usn, final String udn, final String nt) {
    // controllerExecutor.execute(new Runnable() {
    // @Override
    // public void run() {
    // removeZonePlayer(udn);
    // LOGGER.info("Bye bye from " + usn);
    // }
    // });
    // }
    // }
    //
    // private static final Logger LOGGER = LoggerFactory.getLogger(SonosControllerOLD.class);
    //
    // private static SonosControllerOLD INSTANCE;
    //
    // /**
    // * @return the singleton instance of SonosController
    // */
    // // TODO I hate singletons
    // public synchronized static SonosControllerOLD getInstance() {
    // if (INSTANCE == null) {
    // INSTANCE = new SonosControllerOLD();
    // }
    // return INSTANCE;
    // }
    //
    // private final Executor workerExecutor = Executors.newFixedThreadPool(3, new ThreadFactory() {
    // // yes, this i isn't threadsafe, but who cares?
    // private int i = 0;
    //
    // @Override
    // public Thread newThread(final Runnable r) {
    // Thread t = new Thread(r, "SonosControllerWorkerThread" + i++);
    // t.setDaemon(true);
    // return t;
    // }
    // });;
    //
    // private final Executor controllerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
    // @Override
    // public Thread newThread(final Runnable r) {
    // Thread t = new Thread(r, "SonosControllerThread");
    // t.setDaemon(true);
    // return t;
    // }
    // });
    // private final DiscoveryHandler discoveryHandler = new DiscoveryHandler();
    // private final ZoneGroupStateModel groups = new ZoneGroupStateModel();
    // private final ZonePlayerModel zonePlayers = new ZonePlayerModel();
    // // a map from zone UDN to when the zone was last seen
    // private final Map<String, Long> zonePlayerDiscoveries = new HashMap<String, Long>();
    //
    // private final boolean registeredForSearching = false;
    //
    // private SonosControllerOLD() {
    // // private constructor
    // }
    //
    // /**
    // * Creates a new ZonePlayer from the given device and adds it to our list.
    // *
    // * @param dev
    // * @throws IllegalArgumentException
    // * if <code>dev</code> is not a sonos device
    // */
    // private void addZonePlayer(final RemoteDevice dev) {
    // synchronized (zonePlayers) {
    // zonePlayerDiscoveries.put(dev.getIdentity().getUdn().getIdentifierString().substring(5), System.currentTimeMillis());
    //
    // // Check if we've already got this zone player
    // for (ZonePlayer zone : zonePlayers.getAllZones()) {
    // if (zone.getRootDevice().getIdentity().getUdn().equals(dev.getIdentity().getUdn().getIdentifierString())) {
    // return;
    // }
    // }
    // // LOGGER.info("Discovered device " + dev.getDiscoveryUDN());
    //
    // // Ignore zone bridges
    // // TODO may need to implement cut down zone player for the zone bridge
    // // I believe the bridge only supports the following interfaces:
    // // DeviceProperties
    // // GroupManagement
    // // SystemProperties
    // // ZoneGroup
    //
    // if (dev.getDetails().getModelDetails().getModelNumber().contains("ZB100") || dev.getDetails().getModelDetails().getModelNumber().contains("BR100")) {
    // // LOGGER.warn("Ignoring Zone " + dev.getDeviceType() + " " + dev.getModelDescription() + " " + dev.getModelName() + " " + dev.getModelNumber());
    // return;
    // }
    // if (LOGGER.isInfoEnabled()) {
    // LOGGER.info("Adding zone: " + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " "
    // + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber());
    // }
    // try {
    // ZonePlayer zone = new ZonePlayer(dev);
    // zonePlayers.addZonePlayer(zone);
    // zone.getZoneGroupTopologyService().addZoneGroupTopologyListener(this);
    // zoneGroupTopologyChanged(zone.getZoneGroupTopologyService().getGroupState());
    // } catch (Exception e) {
    // LOGGER.error("Couldn't add zone" + dev.getType().getDisplayString() + " " + dev.getDetails().getModelDetails().getModelDescription() + " "
    // + dev.getDetails().getModelDetails().getModelName() + " " + dev.getDetails().getModelDetails().getModelNumber(), e);
    // }
    // }
    // }
    //
    // public void dispose() {
    // DiscoveryAdvertisement.getInstance().unRegisterEvent(DiscoveryAdvertisement.EVENT_SSDP_ALIVE, ZonePlayerConstants.SONOS_DEVICE_TYPE, discoveryHandler);
    // DiscoveryAdvertisement.getInstance()
    // .unRegisterEvent(DiscoveryAdvertisement.EVENT_SSDP_BYE_BYE, ZonePlayerConstants.SONOS_DEVICE_TYPE, discoveryHandler);
    // DiscoveryListener.getInstance().unRegisterResultsHandler(discoveryHandler, ZonePlayerConstants.SONOS_DEVICE_TYPE);
    // synchronized (zonePlayers) {
    // for (ZonePlayer zp : zonePlayers.getAllZones()) {
    // zp.dispose();
    // }
    // }
    // }
    //
    // public Executor getControllerExecutor() {
    // return controllerExecutor;
    // }
    //
    // /**
    // * @param zp
    // * a zone player
    // * @return the coordinator of the zone player's group, or zp if it could not
    // * be discovered.
    // */
    // public ZonePlayer getCoordinatorForZonePlayer(final ZonePlayer zp) {
    // if ((zp == null) || (zp.getZoneGroupTopologyService().getGroupState() == null)) {
    // return zp;
    // }
    // for (ZoneGroup zg : zp.getZoneGroupTopologyService().getGroupState().getGroups()) {
    // if (zg.getMembers().contains(zp.getId())) {
    // return getZonePlayerModel().getById(zg.getCoordinator());
    // }
    // }
    // return zp;
    // }
    //
    // /**
    // * @return an Executor for performing asynchronous activities.
    // */
    // public Executor getWorkerExecutor() {
    // return workerExecutor;
    // }
    //
    // /**
    // * @return the ZoneGroupStateModel
    // */
    // public ZoneGroupStateModel getZoneGroupStateModel() {
    // return groups;
    // }
    //
    // /**
    // * @return the ZonePlayerModel
    // */
    // public ZonePlayerModel getZonePlayerModel() {
    // synchronized (zonePlayers) {
    // return zonePlayers;
    // }
    // }
    //
    // public void purgeStaleDevices(final long staleThreshold) {
    // long now = System.currentTimeMillis();
    // for (Entry<String, Long> zone : zonePlayerDiscoveries.entrySet()) {
    // if ((now - zone.getValue()) > staleThreshold) {
    // removeZonePlayer(zone.getKey());
    // }
    // }
    // }
    //
    // /**
    // * Removes a zone player if it has the specified UDN.
    // *
    // * @param udn
    // */
    // private void removeZonePlayer(final String udn) {
    // synchronized (zonePlayers) {
    // ZonePlayer zp = zonePlayers.getById(udn);
    // if (zp != null) {
    // LOGGER.info("Removing ZonePlayer " + udn + " " + zp.getRootDevice().getModelDescription());
    // zonePlayers.remove(zp);
    // zp.getZoneGroupTopologyService().removeZoneGroupTopologyListener(this);
    // if (zonePlayers.getSize() == 0) {
    // zoneGroupTopologyChanged(new ZoneGroupState(Collections.EMPTY_LIST));
    // }
    // zp.dispose();
    // }
    // }
    // }
    //
    // public void searchForDevices() {
    // try {
    // if (!registeredForSearching) {
    // ServicesEventing.getInstance().setDaemonPort(Integer.parseInt(System.getProperty("net.sf.EventingPort", "2001")));
    // // These first two listen for broadcast advertisements, while the 3rd
    // // listens for responses to a search request.
    // DiscoveryAdvertisement.getInstance().registerEvent(DiscoveryAdvertisement.EVENT_SSDP_ALIVE, ZonePlayerConstants.SONOS_DEVICE_TYPE,
    // discoveryHandler);
    // DiscoveryAdvertisement.getInstance().registerEvent(DiscoveryAdvertisement.EVENT_SSDP_BYE_BYE, ZonePlayerConstants.SONOS_DEVICE_TYPE,
    // discoveryHandler);
    // DiscoveryListener.getInstance().registerResultsHandler(discoveryHandler, ZonePlayerConstants.SONOS_DEVICE_TYPE);
    // }
    // sendSearchPacket(ZonePlayerConstants.SONOS_DEVICE_TYPE);
    // } catch (IOException e) {
    // LOGGER.error("Could not search for devices.", e);
    // }
    // }
    //
    // /**
    // * Broadcasts a search packet on all network interfaces. This method should
    // * really be a part of the Discovery class, but that's not my code...
    // *
    // * @param searchTarget
    // * @
    // */
    // public void sendSearchPacket(final String searchTarget) {
    // for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
    // NetworkInterface intf = e.nextElement();
    // for (Enumeration<InetAddress> adrs = intf.getInetAddresses(); adrs.hasMoreElements();) {
    // InetAddress adr = adrs.nextElement();
    // if ((adr instanceof Inet4Address) && !adr.isLoopbackAddress()) {
    // Discovery.sendSearchMessage(adr, Discovery.DEFAULT_TTL, Discovery.DEFAULT_MX, searchTarget);
    // }
    // }
    // }
    //
    // }
    //
    // /**
    // * @see net.sf.janos.control.ZoneGroupTopologyListener#valuesChanged()
    // */
    // @Override
    // public synchronized void zoneGroupTopologyChanged(final ZoneGroupState groupState) {
    // if (groupState == null) {
    // return;
    // }
    // groups.handleGroupUpdate(groupState);
    // }
}
