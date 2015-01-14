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
 * A device for serving media to other devices. Contains a
 * ContentDirectoryService and a ConnectionManagerService.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class MediaServerDevice {

    /** The dev. */
    private final RemoteDevice dev;
    
    /** The content directory. */
    private final ContentDirectoryService contentDirectory;
    
    /** The connection manager. */
    private final ConnectionManagerService connectionManager;

    /**
     * Creates a new MediaServerDevice from the given RemoteDevice which must be of
     * type {@link ZonePlayerConstants#MEDIA_SERVER_DEVICE_TYPE}.
     *
     * @param upnpService the upnp service
     * @param dev the dev
     */
    protected MediaServerDevice(final UpnpService upnpService, final RemoteDevice dev) {
        if ((dev != null) && (dev.getType() != null) && !dev.getType().toString().equals(ZonePlayerConstants.MEDIA_SERVER_DEVICE_TYPE)) {
            throw new IllegalArgumentException("Device must be media server, not " + dev.getType());
        }
        this.dev = dev;
        contentDirectory = new ContentDirectoryService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_CONTENT_DIRECTORY));
        connectionManager = new ConnectionManagerService(upnpService, AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_CONNECTION_MANAGER));
    }

    /**
     * Dispose.
     */
    public void dispose() {
        contentDirectory.dispose();
        connectionManager.dispose();
    }

    /**
     * Gets the connection manager service.
     *
     * @return the ConnectionManagerService for this MediaServer.
     */
    public ConnectionManagerService getConnectionManagerService() {
        return connectionManager;
    }

    /**
     * Gets the content directory service.
     *
     * @return a ContentDirectoryService that allows the searching and listing of
     * music.
     */
    public ContentDirectoryService getContentDirectoryService() {
        return contentDirectory;
    }

    /**
     * Gets the remote device.
     *
     * @return the wrapped RemoteDevice.
     */
    public RemoteDevice getRemoteDevice() {
        return dev;
    }
}
