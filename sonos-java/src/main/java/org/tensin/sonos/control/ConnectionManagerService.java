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
import org.fourthline.cling.model.meta.Service;

/**
 * The Class ConnectionManagerService.
 */
public class ConnectionManagerService extends AbstractService {

    /**
     * Instantiates a new connection manager service.
     *
     * @param upnpService the upnp service
     * @param service the service
     */
    protected ConnectionManagerService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_CONNECTION_MANAGER);
    }

    /**
     * Handle state variable event.
     *
     * @param varName the var name
     * @param newValue the new value
     */
    public void handleStateVariableEvent(final String varName, final String newValue) {
        // TODO Auto-generated method stub

    }

}
