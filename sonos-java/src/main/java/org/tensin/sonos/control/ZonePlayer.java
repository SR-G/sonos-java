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

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.model.Entry;
import org.tensin.sonos.model.SeekTargetFactory;
import org.tensin.sonos.model.TransportInfo.TransportState;

/**
 * Corresponds to a physical Zone Player, and gives access all the devices and
 * services that a Zone Player has.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ZonePlayer {

    /**
     * Find child device.
     * 
     * @param device
     *            the device
     * @param type
     *            the type
     * @return the remote device
     */
    protected static RemoteDevice findChildDevice(final RemoteDevice device, final String type) {
        for (final RemoteDevice remoteDevice : device.getEmbeddedDevices()) {
            if (remoteDevice.getType().toString().equalsIgnoreCase(type)) {
                return remoteDevice;
            }
        }
        return null;
    }

    /** The dev. */
    private final RemoteDevice dev;

    /** The media server. */
    private final MediaServerDevice mediaServer;

    /** The media renderer. */
    private final MediaRendererDevice mediaRenderer;

    /** The alarm. */
    private final AlarmClockService alarm;

    /** The audio in. */
    private final AbstractAudioInService audioIn;

    /** The device properties. */
    private final DevicePropertiesService deviceProperties;

    /** The system properties. */
    private final SystemPropertiesService systemProperties;

    /** The zone group topology. */
    private final ZoneGroupTopologyService zoneGroupTopology;

    /** The zone group management. */
    private final ZoneGroupManagementService zoneGroupManagement;

    /** The ip. */
    private InetAddress ip;

    /** The port. */
    private final int port;

    /**
     * Creates a new sonos device around the given RemoteDevice. This device
     * must be a sonos device
     * 
     * @param upnpService
     *            the upnp service
     * @param dev
     *            the dev
     */
    public ZonePlayer(final UpnpService upnpService, final RemoteDevice dev) {
        if (!dev.getType().toString().equals(ZonePlayerConstants.SONOS_DEVICE_TYPE)) {
            throw new IllegalArgumentException("dev must be a sonos device, not [" + dev.getType() + "]");
        }
        this.dev = dev;
        try {
            ip = InetAddress.getByName(dev.getIdentity().getDescriptorURL().getHost());
        } catch (final UnknownHostException e) {
            // will not happen - should be IP not host
            e.printStackTrace();
        }
        port = dev.getIdentity().getDescriptorURL().getPort();
        mediaServer = new MediaServerDevice(upnpService, findChildDevice(dev, ZonePlayerConstants.MEDIA_SERVER_DEVICE_TYPE));
        mediaRenderer = new MediaRendererDevice(upnpService, findChildDevice(dev, ZonePlayerConstants.MEDIA_RENDERER_DEVICE_TYPE));
        alarm = new AlarmClockService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_ALARM_CLOCK));
        audioIn = AbstractAudioInService.buildAudioInService(upnpService, dev);
        deviceProperties = new DevicePropertiesService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_DEVICE_PROPERTIES));
        systemProperties = new SystemPropertiesService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_SYSTEM_PROPERTIES));
        zoneGroupTopology = new ZoneGroupTopologyService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_ZONE_GROUP_TOPOLOGY));
        zoneGroupManagement = new ZoneGroupManagementService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_ZONE_GROUP_MANAGEMENT));
    }

    /**
     * Creates a new URL by appending the given string to this zonePlayer's attributes.
     * 
     * @param url
     *            the url to append, eg "/images/image1.png"
     * @return the complete url eg "http://192.168.0.1:1400/images/image1.png"
     * @throws MalformedURLException
     *             the malformed url exception
     */
    public URL appendUrl(final String url) throws MalformedURLException {
        return new URL("http", getIP().getHostAddress(), getPort(), url);
    }

    /**
     * Dispose.
     */
    public void dispose() {
        mediaServer.dispose();
        mediaRenderer.dispose();
        alarm.dispose();
        audioIn.dispose();
        deviceProperties.dispose();
        systemProperties.dispose();
        zoneGroupTopology.dispose();
        zoneGroupManagement.dispose();
    }

    /**
     * Enqueues the given entry, skips to it and ensure the zone is playing.
     * 
     * @param entry
     *            the entry
     *            @ * Signals that an I/O exception has occurred.
     */
    public void enqueueAndPlayEntry(final Entry entry) throws SonosException {
        playQueueEntry(enqueueEntry(entry));
    }

    /**
     * Adds the given entry to the play queue for this zone player.
     * 
     * NOTE: this should only be called if this zone player is the zone group
     * coordinator.
     * 
     * @param entry
     *            the entry to enqueue.
     * @return the int
     *         @ * Signals that an I/O exception has occurred.
     */
    public int enqueueEntry(final Entry entry) {
        final AVTransportService serv = getMediaRendererDevice().getAvTransportService();
        final int index = serv.addToQueue(entry);
        return index;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ZonePlayer) {
            final ZonePlayer zp = (ZonePlayer) obj;
            return zp.getRootDevice().getIdentity().getUdn().getIdentifierString().equals(getRootDevice().getIdentity().getUdn().getIdentifierString());
        }
        return false;
    }

    /**
     * Gets the alarm service.
     * 
     * @return the AlarmClockService for this zone player.
     */
    public AlarmClockService getAlarmService() {
        return alarm;
    }

    /**
     * Gets the audio in service.
     * 
     * @return the audio in service for this zone player.
     */
    public AbstractAudioInService getAudioInService() {
        return audioIn;
    }

    /**
     * Gets the device properties service.
     * 
     * @return the DeviceProperties service for this zone player
     */
    public DevicePropertiesService getDevicePropertiesService() {
        return deviceProperties;
    }

    /**
     * Gets the id.
     * 
     * @return A string of characters identifying this sonos to other sonos
     */
    public String getId() {
        return getRootDevice().getIdentity().getUdn().getIdentifierString().substring(5);
    }

    /**
     * Gets the ip.
     * 
     * @return the IP address for this zone player.
     */
    public InetAddress getIP() {
        return ip;
    }

    // --- a few convenience methods

    /**
     * Gets the media renderer device.
     * 
     * @return a RemoteDevice of type MediaRenderer, from our sonos object.
     */
    public MediaRendererDevice getMediaRendererDevice() {
        return mediaRenderer;
    }

    /**
     * Gets the media server device.
     * 
     * @return a SonosMediaServerDevice for our zone player.
     */
    public MediaServerDevice getMediaServerDevice() {
        return mediaServer;
    }

    /**
     * Gets the port.
     * 
     * @return the port for HTTP requests to this zone player.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the root device.
     * 
     * @return the RemoteDevice around which this object has been created.
     */
    public RemoteDevice getRootDevice() {
        return dev;
    }

    /**
     * Gets the system properties service.
     * 
     * @return system properties service for this zone player.
     */
    public SystemPropertiesService getSystemPropertiesService() {
        return systemProperties;
    }

    /**
     * Gets the zone group management service.
     * 
     * @return the zone group management service for this player.
     */
    public ZoneGroupManagementService getZoneGroupManagementService() {
        return zoneGroupManagement;
    }

    /**
     * Gets the zone group topology service.
     * 
     * @return the zone group topology service for this zone player.
     */
    public ZoneGroupTopologyService getZoneGroupTopologyService() {
        return zoneGroupTopology;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getRootDevice().getIdentity().getUdn().getIdentifierString().hashCode();
    }

    /**
     * Seeks to the given entry in the queue (0 is the first entry in the queue).
     * 
     * @param index
     *            the index
     *            @ * Signals that an I/O exception has occurred.
     */
    public void playQueueEntry(final int index) throws SonosException {
        final AVTransportService serv = getMediaRendererDevice().getAvTransportService();
        if (!serv.getMediaInfo().getCurrentURI().startsWith("x-rincon-queue:")) {
            serv.setAvTransportUriToQueue(getId());
        }
        serv.seek(SeekTargetFactory.createTrackSeekTarget(index));
        if (!serv.getTransportInfo().getState().equals(TransportState.PLAYING)) {
            serv.play();
        }
    }
}
