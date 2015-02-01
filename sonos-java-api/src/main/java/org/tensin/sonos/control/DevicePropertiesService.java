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
import org.tensin.sonos.model.ZoneAttributes;
import org.tensin.sonos.model.ZoneInfo;

/**
 * For querying the device properties of a zone player. This is a Sonos specific
 * class.
 * 
 * NOTE: this class is incomplete.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class DevicePropertiesService extends AbstractService {

    /**
     * Instantiates a new device properties service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     */
    protected DevicePropertiesService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_DEVICE_PROPERTIES);
    }

    /**
     * Gets the household id.
     * 
     * @return A string representation of the HouseholdID for this Sonos Device.
     *         @ Signals that an I/O exception has occurred.
     */
    public String getHouseholdID() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetHouseholdID");
        executeImmediate(message);
        return message.getOutputAsString("HouseholdID");
    }

    /**
     * Gets the invisible.
     * 
     * @return <code>true</code> if the Sonos Device is currently operating in
     *         Invisible mode, or <code>false</code> otherwise.
     *         @ Signals that an I/O exception has occurred.
     */
    public boolean getInvisible() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetInvisible");
        executeImmediate(message);
        return Boolean.parseBoolean(message.getOutputAsString("CurrentInvisible"));
    }

    /**
     * Gets the current state of the Sonos Device's LED.
     * 
     * @return the lED state
     *         @ Signals that an I/O exception has occurred.
     */
    public boolean getLEDState() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetLEDState");
        executeImmediate(message);
        String state = message.getOutputAsString("CurrentLEDState");
        return "On".equals(state);
    }

    /**
     * Gets the zone attributes.
     * 
     * @return a ZoneAttributes object with the name and icon for this zone
     *         player, or null if the request fails
     */
    public ZoneAttributes getZoneAttributes() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetZoneAttributes");
        executeImmediate(message);
        return new ZoneAttributes(message.getOutputAsString("CurrentZoneName"), message.getOutputAsString("CurrentIcon"));
    }

    /**
     * Gets the zone info.
     * 
     * @return a ZoneInfo object containing the information for this Sonos Device.
     *         @ Signals that an I/O exception has occurred.
     */
    public ZoneInfo getZoneInfo() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetZoneInfo");
        executeImmediate(message);

        return new ZoneInfo(message.getOutputAsString("SerialNumber"), message.getOutputAsString("SoftwareVersion"),
                message.getOutputAsString("DisplaySoftwareVersion"), message.getOutputAsString("HardwareVersion"), message.getOutputAsString("IPAddress"),
                message.getOutputAsString("MACAddress"), message.getOutputAsString("CopyrightInfo"), message.getOutputAsString("ExtraInfo"));
    }

    /**
     * Handle state variable event.
     * 
     * @param varName
     *            the var name
     * @param newValue
     *            the new value
     */
    public void handleStateVariableEvent(final String varName, final String newValue) {
        /*
         * <stateVariable sendEvents="yes">
         * <name>SettingsReplicationState</name>
         * <dataType>string</dataType>
         * </stateVariable>
         * <stateVariable sendEvents="yes">
         * <name>ZoneName</name>
         * <dataType>string</dataType>
         * </stateVariable>
         * <stateVariable sendEvents="yes">
         * <name>Icon</name>
         * <dataType>string</dataType>
         * </stateVariable>
         * <stateVariable sendEvents="yes">
         * <name>Invisible</name>
         * <dataType>boolean</dataType>
         * </stateVariable>
         * <stateVariable sendEvents="yes">
         * <name>IsZoneBridge</name>
         * <dataType>boolean</dataType>
         * </stateVariable>
         */
    }

    // public ZoneAttributes getZoneAttributes() {
    // ActionInvocation message= messageFactory.getMessage(getService(), "GetZoneAttributes");
    // executeImmediate(message);
    // String name = message.getOutputAsString("DesiredZoneName");
    // String icon = message.getOutputAsString("DesiredIcon");
    // return new ZoneAttributes(name, icon);
    // }

    /**
     * Sets or unsets the Sonos Device to run in invisible mode.
     * 
     * @param isInvisible
     *            the new invisible
     *            @ Signals that an I/O exception has occurred.
     */
    public void setInvisible(final boolean isInvisible) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetInvisible");
        message.setInput("DesiredInvisible", isInvisible ? "1" : "0");
        execute(message);
    }

    /**
     * Sets the state of the Sonos Device's LED to the given state.
     * 
     * @param ledEnabled
     *            the new lED state
     *            @ Signals that an I/O exception has occurred.
     */
    public void setLEDState(final boolean ledEnabled) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetLEDState");
        message.setInput("DesiredLEDState", ledEnabled ? "On" : "Off");
        execute(message);
    }

    /*
     * TODO
     * <?xml version="1.0" encoding="utf-8" ?>
     * <scpd xmlns="urn:schemas-upnp-org:service-1-0">
     * <specVersion>
     * <major>1</major>
     * <minor>0</minor>
     * </specVersion>
     * <serviceStateTable>
     * <stateVariable sendEvents="no">
     * <name>HouseholdID</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>LEDState</name>
     * <dataType>string</dataType>
     * <allowedValueList>
     * <allowedValue>On</allowedValue>
     * <allowedValue>Off</allowedValue>
     * </allowedValueList>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>SerialNumber</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>SoftwareVersion</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>DisplaySoftwareVersion</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>HardwareVersion</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>IPAddress</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>MACAddress</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>CopyrightInfo</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>ExtraInfo</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * </serviceStateTable>
     * </scpd>
     */

    /**
     * Applies the given {@link ZoneAttributes} to this Sonos Device.
     * 
     * @param atts
     *            the new zone attributes
     *            @ Signals that an I/O exception has occurred.
     */
    public void setZoneAttributes(final ZoneAttributes atts) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetZoneAttributes");
        message.setInput("DesiredZoneName", atts.getName());
        message.setInput("DesiredIcon", atts.getIcon());
        execute(message);
    }

}
