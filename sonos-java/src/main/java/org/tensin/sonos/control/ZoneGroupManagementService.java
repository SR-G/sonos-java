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
import org.tensin.sonos.model.GroupJoinResponse;

/**
 * Provides management of the zone player's group.
 * 
 * NOTE: this class is incomplete
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ZoneGroupManagementService extends AbstractService {

    /**
     * Instantiates a new zone group management service.
     *
     * @param upnpService the upnp service
     * @param service the service
     */
    public ZoneGroupManagementService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_ZONE_GROUP_MANAGEMENT);
    }

    /**
     * Adds the Zone Player with the given member ID TODO to what?.
     *
     * @param memberId the member id
     * @return the group join response
     * @ Signals that an I/O exception has occurred.
     */
    public GroupJoinResponse addMember(final String memberId)  {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "AddMember");
        message.setInput("MemberID", memberId);
        executeImmediate(message);
        return new GroupJoinResponse(message.getOutputAsString("CurrentTransportSettings"), message.getOutputAsString("GroupUUIDJoined"));
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
     * <name>A_ARG_TYPE_MemberID</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_TransportSettings</name>
     * <dataType>string</dataType>
     * </stateVariable>
     * <stateVariable sendEvents="no">
     * <name>A_ARG_TYPE_BufferingResultCode</name>
     * <dataType>i4</dataType>
     * </stateVariable>
     */
    /**
     * Handle state variable event.
     *
     * @param varName the var name
     * @param newValue the new value
     */
    public void handleStateVariableEvent(final String varName, final String newValue) {
        /*
         * <stateVariable sendEvents="yes">
         * <name>GroupCoordinatorIsLocal</name>
         * <dataType>boolean</dataType>
         * </stateVariable>
         * <stateVariable sendEvents="yes">
         * <name>LocalGroupUUID</name>
         * <dataType>string</dataType>
         * </stateVariable>
         */

    }

    /**
     * Removes the given member.
     *
     * @param memberId the member id
     * @ Signals that an I/O exception has occurred.
     */
    public void removeMember(final String memberId)  {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "RemoveMember");
        message.setInput("MemberID", memberId);
        execute(message);
    }

    /**
     * Report track buffering result.
     *
     * @param memberId the member id
     * @param resultCode the result code
     * @ Signals that an I/O exception has occurred.
     */
    public void reportTrackBufferingResult(final String memberId, final int resultCode)  {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "ReportTrackBufferingResult");
        message.setInput("MemberID", memberId);
        message.setInput("ResultCode", resultCode);
        execute(message);
    }
}
