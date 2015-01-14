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

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.model.UnresponsiveDeviceActionType;
import org.tensin.sonos.model.UpdateType;
import org.tensin.sonos.model.ZoneGroupState;

/**
 * Provides information and control over the group topology.
 * 
 * NOTE: this class is incomplete.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ZoneGroupTopologyService extends AbstractService {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneGroupTopologyService.class);

    /** The listeners. */
    private final List<ZoneGroupTopologyListener> listeners = new ArrayList<ZoneGroupTopologyListener>();

    // private final ServiceEventHandler serviceEventHandler = new ServiceEventHandler() {
    // public void handleStateVariableEvent(final String varName, final String newValue) {
    // LOGGER.debug(varName + "=" + newValue);
    // try {
    // if (varName.equals("AvailableSoftwareUpdate")) {
    // } else if (varName == "ZoneGroupState") {
    // zoneGroup = ResultParser.getGroupStateFromResult(newValue);
    // fireStateChanged();
    // } else if (varName == "ThirdPartyMediaServers") {
    //
    // } else if (varName == "AlarmRunSequence") {
    //
    // }
    // } catch (SAXException e) {
    // LOGGER.error("Could not parse state var: " + e);
    // }
    //
    // }
    // };

    /** The zone group. */
    private final ZoneGroupState zoneGroup = null;

    /**
     * Instantiates a new zone group topology service.
     *
     * @param upnpService the upnp service
     * @param service the service
     */
    public ZoneGroupTopologyService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_ZONE_GROUP_TOPOLOGY);
        // registerServiceEventing(serviceEventHandler);
    }

    /**
     * registers the given listener to be notified of changes to the state of the AVTransportService.
     *
     * @param l the l
     */
    public void addZoneGroupTopologyListener(final ZoneGroupTopologyListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /*
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_UpdateType</name>
     * <dataType>string</dataType>
     * <allowedValueList>
     * <allowedValue>All</allowedValue>
     * <allowedValue>Software</allowedValue>
     * </allowedValueList>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_CachedOnly</name>
     * <dataType>boolean</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_UpdateItem</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_UpdateURL</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_UpdateFlags</name>
     * <dataType>ui4</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_Version</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_MemberID</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_UnresponsiveDeviceActionType</name>
     * <dataType>string</dataType>
     * <allowedValueList>
     * <allowedValue>Remove</allowedValue>
     * <allowedValue>VerifyThenRemoveSystemwide</allowedValue>
     * </allowedValueList>
     * </stateVariable>
     * </serviceStateTable>
     */

    /**
     * Begin software update.
     *
     * @param updateUrl the update url
     * @param updateFlags the update flags
     * @ Signals that an I/O exception has occurred.
     */
    public void beginSoftwareUpdate(final String updateUrl, final int updateFlags)  {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "BeginSoftwareUpdate");
        message.setInput("UpdateURL", updateUrl);
        message.setInput("Flags", updateFlags);
        execute(message);
    }

    /**
     * Checks for the availability of an update.
     *
     * @param type the type
     * @param cachedOnly the cached only
     * @param version the version
     * @return the string
     * @ Signals that an I/O exception has occurred.
     */
    public String checkForUpdate(final UpdateType type, final boolean cachedOnly, final String version)  {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "CheckForUpdate");
        message.setInput("UpdateType", type);
        message.setInput("CachedOnly", cachedOnly);
        message.setInput("Version", version);
        executeImmediate(message);
        return message.getOutputAsString("UpdateItem");
    }

    /** {@inheritDoc}
     * @see org.tensin.sonos.control.AbstractService#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        // unregisterServiceEventing(serviceEventHandler);
    }

    /*
     * <action>
     * <name>ReportAlarmStartedRunning</name>
     * </action>
     */

    /**
     * Fire state changed.
     */
    private void fireStateChanged() {
        synchronized (listeners) {
            for (ZoneGroupTopologyListener l : listeners) {
                l.zoneGroupTopologyChanged(getGroupState());
            }
        }
    }

    /**
     * Gets the group state.
     *
     * @return an object representing the (perceived) group state.
     */
    public ZoneGroupState getGroupState() {

        if (zoneGroup != null) {
            return zoneGroup;
        }

        // if we don't already have the zoneState, ask for it.
        // TODO: figure out why this returns 401
        // StateVariableMessage groupMessage = messageFactory.getStateVariableMessage( ZonePlayerConstants.SONOS_VARIABLE_ZONE_GROUP_STATE );
        // try {
        // StateVariableResponse resp = groupMessage.service();
        // zoneGroup = ResultParser.getGroupStateFromResult(SonosController.getInstance(), resp.getStateVariableValue());
        // } catch ( Exception ex ) {
        // LOGGER.error("Could not retrieve zone group topology", ex);
        // }

        return zoneGroup;
    }

    /**
     * unregisters the given listener: no further notifications shall be recieved.
     *
     * @param l the l
     */
    public void removeZoneGroupTopologyListener(final ZoneGroupTopologyListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    /**
     * Report unresponsive device.
     *
     * @param deviceUuid the device uuid
     * @param action the action
     * @ Signals that an I/O exception has occurred.
     */
    public void reportUnresponsiveDevice(final String deviceUuid, final UnresponsiveDeviceActionType action)  {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "ReportUnresponsiveDevice");
        message.setInput("DeviceUUID", deviceUuid);
        message.setInput("DesiredAction", action);
        execute(message);
    }
}
