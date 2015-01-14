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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.xml.RenderingControlEventHandler.RenderingControlEventType;
import org.tensin.sonos.xml.ResultParser;
import org.xml.sax.SAXException;

/**
 * Provides control over the rendering (playing) of media (audio).
 * 
 * NOTE: this class is incomplete
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class RenderingControlService extends AbstractService { // implements ServiceEventHandler

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RenderingControlService.class);

    /**
     * The known state of the ZonePlayer rendering control service.
     */
    private final Map<RenderingControlEventType, String> state = new HashMap<RenderingControlEventType, String>();

    /**
     * The listeners to be notified when the state changes.
     */
    private final List<RenderingControlListener> listeners = new ArrayList<RenderingControlListener>();

    /**
     * Instantiates a new rendering control service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     */
    protected RenderingControlService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_RENDERING_CONTROL);
        // registerServiceEventing(this);
    }

    /**
     * Adds a listener to be notified when notifications are received from the ZonePlayer.
     * 
     * @param l
     *            the l
     */
    public void addListener(final RenderingControlListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
     * Fire change event.
     * 
     * @param events
     *            the events
     */
    private void fireChangeEvent(final Set<RenderingControlEventType> events) {
        synchronized (listeners) {
            for (RenderingControlListener l : listeners) {
                l.valuesChanged(events, this);
            }
        }
    }

    /**
     * Gets the mute.
     * 
     * @return the mute
     *         @ Signals that an I/O exception has occurred.
     */
    public boolean getMute() {

        String mute_master = state.get(RenderingControlEventType.MUTE_MASTER);
        if (mute_master == null) {
            SonosActionInvocation message = messageFactory.getMessage(getService(), "GetMute");
            message.setInput("InstanceID", "0");
            message.setInput("Channel", "Master"); // can also be LF or RF
            executeImmediate(message);
            state.put(RenderingControlEventType.MUTE_MASTER, message.getOutputAsString("CurrentMute"));
        }
        return Integer.parseInt(state.get(RenderingControlEventType.MUTE_MASTER)) == 1;
    }

    /**
     * Gets the volume.
     * 
     * @return the current volume, as a percentage of maximum volume.
     *         @ Signals that an I/O exception has occurred.
     */
    public int getVolume() {
        String volume_master = state.get(RenderingControlEventType.VOLUME_MASTER);
        if (volume_master == null) {
            SonosActionInvocation message = messageFactory.getMessage(getService(), "GetVolume");
            message.setInput("InstanceID", "0");
            message.setInput("Channel", "Master"); // can also be LF or RF
            executeImmediate(message);
            state.put(RenderingControlEventType.VOLUME_MASTER, message.getOutputAsString("CurrentVolume"));
        }
        return Integer.parseInt(state.get(RenderingControlEventType.VOLUME_MASTER));
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
         * EG:
         * <Event xmlns="urn:schemas-upnp-org:metadata-1-0/RCS/">
         * <InstanceID val="0">
         * <Volume channel="Master" val="58"/>
         * <Volume channel="LF" val="100"/>
         * <Volume channel="RF" val="100"/>
         * <Mute channel="Master" val="0"/>
         * <Mute channel="LF" val="0"/>
         * <Mute channel="RF" val="0"/>
         * <Bass val="10"/>
         * <Treble val="0"/>
         * <Loudness channel="Master" val="1"/>
         * <OutputFixed val="0"/>
         * <PresetNameList>FactoryDefaults</PresetNameList>
         * </InstanceID>
         * </Event>
         */
        LOGGER.debug("received event " + varName + ": " + newValue);
        try {
            Map<RenderingControlEventType, String> changes = ResultParser.parseRenderingControlEvent(newValue);
            state.putAll(changes);
            fireChangeEvent(changes.keySet());
        } catch (SAXException e) {
            LOGGER.error("Ignored event due to SAX parsing error: ", e);
        }
    }

    /**
     * Removes a listener.
     * 
     * @param l
     *            the l
     */
    public void removeListener(final RenderingControlListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    /**
     * Sets the mute to the given value. Must be between 0-1 inclusive.
     * 
     * @param mute
     *            the new mute state.
     *            @ Signals that an I/O exception has occurred.
     */

    public void setCrossFade(final boolean crossfade) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetCrossfadeMode");
        message.setInput("InstanceID", "0");
        message.setInput("DesiredMute", crossfade ? "1" : "0");
        execute(message);
    }

    /**
     * Sets the mute to the given value. Must be between 0-1 inclusive.
     * 
     * @param mute
     *            the new mute state.
     *            @ Signals that an I/O exception has occurred.
     */

    public void setMute(final boolean mute) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetMute");
        message.setInput("InstanceID", "0");
        message.setInput("Channel", "Master"); // can also be LF or RF
        message.setInput("DesiredMute", mute ? "1" : "0");
        execute(message);
    }

    /**
     * Sets the volume to the given %. Must be between 0-100 inclusive.
     * 
     * @param vol
     *            the new volume as a percentage of maximum volume.
     *            @ Signals that an I/O exception has occurred.
     */
    public void setVolume(final int vol) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetVolume");
        message.setInput("InstanceID", "0");
        message.setInput("Channel", "Master"); // can also be LF or RF
        message.setInput("DesiredVolume", String.valueOf(vol));
        execute(message);
    }

}
