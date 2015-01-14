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
import org.tensin.sonos.model.AudioInputAttributes;
import org.tensin.sonos.model.LineLevel;

/**
 * For controlling the audio in service of a zone player.
 * 
 * NOTE: this class is incomplete.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class AudioInService extends AbstractAudioInService {

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
     * <name>A_ARG_TYPE_MemberID</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_TransportSettings</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="yes">
     * <name>AudioInputName</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="yes">
     * <name>Icon</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="yes">
     * <name>LineInConnected</name>
     * <dataType>boolean</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="yes">
     * <name>LeftLineInLevel</name>
     * <dataType>i4</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="yes">
     * <name>RightLineInLevel</name>
     * <dataType>i4</dataType>
     * </stateVariable>
     * </serviceStateTable>
     */

    /**
     * Instantiates a new audio in service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     */
    public AudioInService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_AUDIO_IN);
    }

    /**
     * Gets the audio input attributes.
     * 
     * @return an object containing the attributes of the audio input service.
     *         @ Signals that an I/O exception has occurred.
     */
    public AudioInputAttributes getAudioInputAttributes() {
        final SonosActionInvocation message = messageFactory.getMessage(getService(), "GetAudioInputAttributes");
        executeImmediate(message);
        return new AudioInputAttributes(message.getOutputAsString("AudioInputName"), message.getOutputAsString("Icon"));
    }

    /**
     * Gets the line in level.
     * 
     * @return the current line levels for the audio input
     *         @ Signals that an I/O exception has occurred.
     */
    public LineLevel getLineInLevel() {
        final SonosActionInvocation message = messageFactory.getMessage(getService(), "GetLineInLevel");
        executeImmediate(message);

        return new LineLevel(Integer.parseInt(message.getOutputAsString("CurrentLeftLineInLevel")), Integer.parseInt(message.getOutputAsString("CurrentRightLineInLevel")));
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
        // TODO Auto-generated method stub

    }

    /**
     * Sets the attributes for the audio input.
     * 
     * @param name
     *            the name of the audio input
     * @param iconUri
     *            a URI defining an icon for the audio input
     *            @ Signals that an I/O exception has occurred.
     */
    public void setAudioInputAttributes(final String name, final String iconUri) {
        final SonosActionInvocation message = messageFactory.getMessage(getService(), "SetAudioInputAttributes");
        message.setInput("DesiredName", name);
        message.setInput("DesiredIcon", iconUri);
        execute(message);
    }

    /**
     * Sets the level for each component of the line in.
     * 
     * @param leftLevel
     *            the left level
     * @param rightLevel
     *            the right level
     *            @ Signals that an I/O exception has occurred.
     */
    public void setLineInLevel(final int leftLevel, final int rightLevel) {
        final SonosActionInvocation message = messageFactory.getMessage(getService(), "SetLineInLevel");
        message.setInput("DesiredLeftLineInLevel", leftLevel);
        message.setInput("DesiredRightLineInLevel", rightLevel);
        execute(message);
    }

    /**
     * TODO return a TransportSettings object.
     * 
     * @param groupId
     *            The group to which the transmission should be.
     * @return the string
     *         @ Signals that an I/O exception has occurred.
     */
    public String startTransmissionToGroup(final String groupId) {
        final SonosActionInvocation message = messageFactory.getMessage(getService(), "StartTransmissionToGroup");
        message.setInput("CoordinatorID", groupId);
        executeImmediate(message);
        return message.getOutputAsString("CurrentTransportSettings");
    }

    /**
     * Stop transmission to group.
     * 
     * @param groupId
     *            the group id
     *            @ Signals that an I/O exception has occurred.
     */
    public void stopTransmissionToGroup(final String groupId) {
        final SonosActionInvocation message = messageFactory.getMessage(getService(), "StopTransmissionToGroup");
        message.setInput("CoordinatorID", groupId);
        execute(message);
    }
}
