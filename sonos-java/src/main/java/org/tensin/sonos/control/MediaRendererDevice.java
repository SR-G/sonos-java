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

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;

/**
 * A device that "renders media" ie plays music and/or video. Contains an
 * AVTransportService, a ConnectionManager and a RenderingControlService.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class MediaRendererDevice {

    /** The dev. */
    private final RemoteDevice dev;

    /** The rendering control. */
    private final RenderingControlService renderingControl;
    
    /** The connection manager. */
    private final ConnectionManagerService connectionManager;
    
    /** The av transport. */
    private final AVTransportService avTransport;

    /**
     * Instantiates a new media renderer device.
     *
     * @param upnpService the upnp service
     * @param dev the dev
     */
    protected MediaRendererDevice(final UpnpService upnpService, final RemoteDevice dev) {
        if ((dev != null) && (dev.getType() != null) && !dev.getType().toString().equals(ZonePlayerConstants.MEDIA_RENDERER_DEVICE_TYPE)) {
            throw new IllegalArgumentException("Device must be media renderer, not " + dev.getType());
        }
        this.dev = dev;

        renderingControl = new RenderingControlService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_RENDERING_CONTROL));
        connectionManager = new ConnectionManagerService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_CONNECTION_MANAGER));
        avTransport = new AVTransportService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_AV_TRANSPORT));
    }

    /**
     * Dispose.
     */
    public void dispose() {
        renderingControl.dispose();
        connectionManager.dispose();
        avTransport.dispose();
    }

    /**
     * Gets the av transport service.
     *
     * @return the AVTransportService for this MediaRenderer
     */
    public AVTransportService getAvTransportService() {
        return avTransport;
    }

    /**
     * Gets the connection manager service.
     *
     * @return the ConnectionManager for this MediaRenderer.
     */
    public ConnectionManagerService getConnectionManagerService() {
        return connectionManager;
    }

    /**
     * Gets the rendering control service.
     *
     * @return The RenderingControlService for this MediaRenderer.
     */
    public RenderingControlService getRenderingControlService() {
        return renderingControl;
    }

    /**
     * Gets the uPNP device.
     *
     * @return the wrapped RemoteDevice.
     */
    public RemoteDevice getUPNPDevice() {
        return dev;
    }

}
