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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.helpers.TimeUtilities;
import org.tensin.sonos.model.AlarmProperties;
import org.tensin.sonos.model.DeviceCapabilities;
import org.tensin.sonos.model.Entry;
import org.tensin.sonos.model.MediaInfo;
import org.tensin.sonos.model.PlayMode;
import org.tensin.sonos.model.PositionInfo;
import org.tensin.sonos.model.SeekTarget;
import org.tensin.sonos.model.SeekTargetFactory;
import org.tensin.sonos.model.TrackMetaData;
import org.tensin.sonos.model.TransportAction;
import org.tensin.sonos.model.TransportInfo;
import org.tensin.sonos.model.TransportSettings;
import org.tensin.sonos.xml.AVTransportEventHandler.AVTransportEventType;
import org.tensin.sonos.xml.ResultParser;
import org.xml.sax.SAXException;

/**
 * For controlling the audio transport service of a zone player.
 * 
 * NOTE: some methods in this class are incomplete stubs.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 */
public class AVTransportService extends AbstractService { // implements ServiceEventHandler

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AVTransportService.class);

    /** The Constant SET_AV_TRANSPORT_URI_ACTION. */
    private static final String SET_AV_TRANSPORT_URI_ACTION = "SetAVTransportURI";

    /** The Constant PLAY_ACTION. */
    private static final String PLAY_ACTION = "Play";

    /** The format for a metadata tag: 0: id 1: parent id 2: title 3: upnp:class. */
    private static final MessageFormat METADATA_FORMAT = new MessageFormat("<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
            + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " + "xmlns:r=\"urn:schemas-rinconnetworks-com:metadata-1-0/\" "
            + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\">" + "<item id=\"{0}\" parentID=\"{1}\" restricted=\"true\">" + "<dc:title>{2}</dc:title>"
            + "<upnp:class>{3}</upnp:class>" + "<desc id=\"cdudn\" nameSpace=\"urn:schemas-rinconnetworks-com:metadata-1-0/\">"
            + "RINCON_AssociatedZPUDN</desc>" + "</item></DIDL-Lite>");

    /**
     * Compile metadata string.
     * 
     * @param entry
     *            the entry
     * @return the string
     */
    private static String compileMetadataString(final Entry entry) {

        if (entry.getUpnpClass().equals("object.container.radioContainer") || entry.getUpnpClass().equals("object.container.lineInContainer")) {
            // I made up these ones for Janos - they cannot be used by sonos
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("compileMetadataString() called on inappropriate entry " + entry);
            }
            return "";
        }
        if (entry.getUpnpClass().equals("object.item.audioItem.audioBroadcast")) {
            // return "";
        }
        if (entry.getUpnpClass().equals("object.item.audioItem")) {
            // TODO line in has different metadata, and requires zone info

        }
        // Not too sure what's up with this, but it doesn't seem to like having long upnp class names
        String upnpClass = entry.getUpnpClass();
        if (upnpClass.startsWith("object.container")) {
            upnpClass = "object.container";
        }
        String metadata = METADATA_FORMAT.format(new Object[] { entry.getId(), entry.getParentId(), entry.getTitle(), upnpClass });
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created metadata: " + metadata);
        }
        return metadata;
    }

    /** The state. */
    private final Map<AVTransportEventType, String> state = new HashMap<AVTransportEventType, String>();

    /** The listeners. */
    private final List<AVTransportListener> listeners = new ArrayList<AVTransportListener>();

    /**
     * Instantiates a new aV transport service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     */
    protected AVTransportService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_AV_TRANSPORT);
        // registerServiceEventing(this);
    }

    /**
     * registers the given listener to be notified of changes to the state of the AVTransportService.
     * 
     * @param l
     *            the l
     */
    public void addAvTransportListener(final AVTransportListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
     * Adds the given entry to the end of the queue.
     * 
     * @param entry
     *            the entry
     * @return the position of the entry in the queue
     *         @ * Signals that an I/O exception has occurred.
     */
    public int addToQueue(final Entry entry) {
        return addToQueue(entry, -2); // converted to -1 one-relative
    }

    /**
     * Adds the given entry to the end of the queue.
     * 
     * @param entry
     *            the entry
     * @param position
     *            the desired position in the queue (zero-relative)
     * @return the position of the entry in the queue (zero-relative)
     */
    public int addToQueue(final Entry entry, final int position) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "AddURIToQueue");
        message.setInput("InstanceID", "0");
        message.setInput("EnqueuedURI", entry.getRes());
        message.setInput("EnqueuedURIMetaData", compileMetadataString(entry));
        message.setInput("DesiredFirstTrackNumberEnqueued", String.valueOf(position + 1));
        message.setInput("EnqueueAsNext", "1");
        executeImmediate(message);
        return Integer.parseInt(message.getOutputAsString("FirstTrackNumberEnqueued")) - 1;
    }

    /**
     * Indicates that this node should become the coordinator of its own
     * standalone group.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void becomeCoordinatorOfStandaloneGroup() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "BecomeCoordinatorOfStandaloneGroup");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * Tells this node to become group coordinator.
     * 
     * @param currentCoordinator
     *            the current coordinator
     * @param currentGroupId
     *            the current group id
     * @param otherMemebers
     *            the other memebers
     * @param transportSettings
     *            the transport settings
     * @param currentURI
     *            the current uri
     * @param currentURIMetadata
     *            the current uri metadata
     * @param sleepTimerState
     *            the sleep timer state
     * @param alarmState
     *            the alarm state
     * @param streamRestartState
     *            the stream restart state
     * @param currentQueueTrackList
     *            the current queue track list
     *            @ * Signals that an I/O exception has occurred.
     */
    public void becomeGroupCoordinator(final String currentCoordinator, final String currentGroupId, final String otherMemebers,
            final String transportSettings, final String currentURI, final String currentURIMetadata, final String sleepTimerState, final String alarmState,
            final String streamRestartState, final String currentQueueTrackList) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "BecomeGroupCoordinator");
        message.setInput("InstanceID", "0");
        message.setInput("CurrentCoordinator", currentCoordinator);
        message.setInput("CurrentGroupID", currentGroupId);
        message.setInput("OtherMembers", otherMemebers);
        message.setInput("TransportSettings", transportSettings);
        message.setInput("CurrentURI", currentURI);
        message.setInput("CurrentURIMetaData", currentURIMetadata);
        message.setInput("SleepTimerState", sleepTimerState);
        message.setInput("AlarmState", alarmState);
        message.setInput("StreamRestartState", streamRestartState);
        message.setInput("CurrentQueueTrackList", currentQueueTrackList);
        execute(message);
    }

    /**
     * Indicates that a group should become the coordinator and source of audio.
     * 
     * @param currentCoordinator
     *            the current coordinator
     * @param currentGroupId
     *            the current group id
     * @param otherMemebers
     *            the other memebers
     * @param currentURI
     *            the current uri
     * @param currentURIMetadata
     *            the current uri metadata
     * @param sleepTimerState
     *            the sleep timer state
     * @param alarmState
     *            the alarm state
     * @param streamRestartState
     *            the stream restart state
     * @param currentAVTTrackList
     *            the current avt track list
     * @param currentQueueTrackList
     *            the current queue track list
     * @param currentSourceState
     *            the current source state
     * @param resumePlayback
     *            the resume playback
     *            @ * Signals that an I/O exception has occurred.
     */
    public void becomeGroupCoordinatorAndSource(final String currentCoordinator, final String currentGroupId, final String otherMemebers,
            final String currentURI, final String currentURIMetadata, final String sleepTimerState, final String alarmState, final String streamRestartState,
            final String currentAVTTrackList, final String currentQueueTrackList, final String currentSourceState, final String resumePlayback) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "BecomeGroupCoordinatorAndSource");
        message.setInput("InstanceID", "0");
        message.setInput("CurrentCoordinator", currentCoordinator);
        message.setInput("CurrentGroupID", currentGroupId);
        message.setInput("OtherMembers", otherMemebers);
        message.setInput("CurrentURI", currentURI);
        message.setInput("CurrentURIMetaData", currentURIMetadata);
        message.setInput("SleepTimerState", sleepTimerState);
        message.setInput("AlarmState", alarmState);
        message.setInput("StreamRestartState", streamRestartState);
        message.setInput("CurrentAVTTrackList", currentAVTTrackList);
        message.setInput("CurrentQueueTrackList", currentQueueTrackList);
        message.setInput("CurrentSourceState", currentSourceState);
        message.setInput("ResumePlayback", resumePlayback);
        execute(message);
    }

    /**
     * Changes group coordinator from current coordinator ot newCoordinator.
     * 
     * @param currentCoordinator
     *            the current coordinator
     * @param newCoordinator
     *            the new coordinator
     * @param newTransportSettings
     *            the new transport settings
     *            @ * Signals that an I/O exception has occurred.
     */
    public void changeCoordinator(final String currentCoordinator, final String newCoordinator, final String newTransportSettings) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "ChangeCoordinator");
        message.setInput("InstanceID", "0");
        message.setInput("CurrentCoordinator", currentCoordinator);
        message.setInput("NewCoordinator", newCoordinator);
        message.setInput("NewTransportSettings", newTransportSettings);
        execute(message);
    }

    /**
     * Changes transport settings to the settings provided.
     * 
     * @param newTransportSettings
     *            the new transport settings
     *            @ * Signals that an I/O exception has occurred.
     */
    public void changeTransportSettings(final String newTransportSettings) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "ChangeTransportSettings");
        message.setInput("InstanceID", "0");
        message.setInput("NewTransportSettings", newTransportSettings);
        execute(message);
    }

    /**
     * Removes all entries from the queue.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void clearQueue() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "RemoveAllTracksFromQueue");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * Sets the new sleep timer duration.
     * 
     * @param newSleepTimerDuration
     *            the new sleep timer duration
     *            @ * Signals that an I/O exception has occurred.
     */
    public void configureSleepTimer(final int newSleepTimerDuration) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "ConfigureSleepTimer");
        message.setInput("InstanceID", "0");
        // ISO8601 Time - this uses joda time, but could use java.util.time.
        message.setInput("NewSleepTimerDuration", TimeUtilities.convertLongToDuration(newSleepTimerDuration));
        execute(message);
    }

    /**
     * Fire state changed.
     * 
     * @param name
     *            the name
     */
    private void fireStateChanged(final Set<AVTransportEventType> name) {
        synchronized (listeners) {
            for (AVTransportListener l : listeners) {
                l.valuesChanged(name, this);
            }
        }
    }

    /**
     * Gets the current transport actions.
     * 
     * @return A Collection of TransportActions representing the available
     *         actions.
     *         @ * Signals that an I/O exception has occurred.
     */
    public Collection<TransportAction> getCurrentTransportActions() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetCurrentTransportActions");
        message.setInput("InstanceID", "0");
        executeImmediate(message);
        String actionList = message.getOutputAsString("Actions");
        ArrayList<TransportAction> actions = new ArrayList<TransportAction>();
        for (TransportAction action : TransportAction.values()) {
            if (actionList.contains(action.toString())) {
                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * Gets the device capabilities.
     * 
     * @return a DeviceCapabilities object expressing the capabilities of this AVTransportService.
     *         @ * Signals that an I/O exception has occurred.
     */
    public DeviceCapabilities getDeviceCapabilities() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetDeviceCapabilities");
        message.setInput("InstanceID", "0");
        executeImmediate(message);
        return new DeviceCapabilities(message.getOutputAsString("PlayMedia"), message.getOutputAsString("RecMedia"),
                message.getOutputAsString("RecQualityModes"));
    }

    /**
     * Gets the media info.
     * 
     * @return Information about the currently playing media.
     *         @ * Signals that an I/O exception has occurred.
     * @throws SAXException
     *             the sAX exception
     */
    public MediaInfo getMediaInfo() throws SonosException {
        if (!(state.containsKey(AVTransportEventType.NumberOfTracks) && state.containsKey(AVTransportEventType.CurrentTrackDuration)
                && state.containsKey(AVTransportEventType.AVTransportURI) && state.containsKey(AVTransportEventType.AVTransportURIMetaData)
                && state.containsKey(AVTransportEventType.NextAVTransportURI) && state.containsKey(AVTransportEventType.NextAVTransportURIMetaData)
                && state.containsKey(AVTransportEventType.PlaybackStorageMedium) && state.containsKey(AVTransportEventType.RecordStorageMedium) && state
                    .containsKey(AVTransportEventType.RecordMediumWriteStatus))) {
            SonosActionInvocation message = messageFactory.getMessage(getService(), "GetMediaInfo");
            message.setInput("InstanceID", "0");
            executeImmediate(message);
            state.put(AVTransportEventType.NumberOfTracks, message.getOutputAsString("NrTracks"));
            state.put(AVTransportEventType.CurrentTrackDuration, message.getOutputAsString("MediaDuration"));
            state.put(AVTransportEventType.AVTransportURI, message.getOutputAsString("CurrentURI"));
            state.put(AVTransportEventType.AVTransportURIMetaData, message.getOutputAsString("CurrentURIMetaData"));
            state.put(AVTransportEventType.NextAVTransportURI, message.getOutputAsString("NextURI"));
            state.put(AVTransportEventType.NextAVTransportURIMetaData, message.getOutputAsString("NextURIMetaData"));
            state.put(AVTransportEventType.PlaybackStorageMedium, message.getOutputAsString("PlayMedium"));
            state.put(AVTransportEventType.RecordStorageMedium, message.getOutputAsString("RecordMedium"));
            state.put(AVTransportEventType.RecordMediumWriteStatus, message.getOutputAsString("WriteStatus"));
        }

        TrackMetaData trackMetaData = null;

        try {
            String metaDataString = state.get(AVTransportEventType.AVTransportURIMetaData);
            trackMetaData = metaDataString.length() == 0 ? null : ResultParser.parseTrackMetaData(metaDataString);
        } catch (NullPointerException e) {
        } catch (SAXException e) {
            throw new SonosException(e);
        }

        return new MediaInfo(state.get(AVTransportEventType.NumberOfTracks), TimeUtilities.convertDurationToLong(state
                .get(AVTransportEventType.CurrentTrackDuration)), state.get(AVTransportEventType.AVTransportURI), trackMetaData,
                state.get(AVTransportEventType.NextAVTransportURI), state.get(AVTransportEventType.NextAVTransportURIMetaData),
                state.get(AVTransportEventType.PlaybackStorageMedium), state.get(AVTransportEventType.RecordStorageMedium),
                state.get(AVTransportEventType.RecordMediumWriteStatus));
    }

    /**
     * Gets the position info.
     * 
     * @return information about the progress of the current media.
     *         @ * Signals that an I/O exception has occurred.
     */
    public PositionInfo getPositionInfo() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetPositionInfo");
        message.setInput("InstanceID", "0");
        executeImmediate(message);
        state.put(AVTransportEventType.CurrentTrack, message.getOutputAsString("Track"));
        state.put(AVTransportEventType.CurrentTrackDuration, message.getOutputAsString("TrackDuration"));
        state.put(AVTransportEventType.CurrentTrackMetaData, message.getOutputAsString("TrackMetaData"));
        state.put(AVTransportEventType.CurrentTrackURI, message.getOutputAsString("TrackURI"));

        TrackMetaData trackMetaData = null;

        try {
            String metaDataString = state.get(AVTransportEventType.CurrentTrackMetaData);
            trackMetaData = metaDataString.length() == 0 ? null : ResultParser.parseTrackMetaData(metaDataString);
        } catch (Exception e) {
        }

        return new PositionInfo(Integer.parseInt(state.get(AVTransportEventType.CurrentTrack)), TimeUtilities.convertDurationToLong(state
                .get(AVTransportEventType.CurrentTrackDuration)), trackMetaData, state.get(AVTransportEventType.CurrentTrackURI),
                TimeUtilities.convertDurationToLong(message.getOutputAsString("RelTime")), TimeUtilities.convertDurationToLong(message
                        .getOutputAsString("AbsTime")), Integer.parseInt(message.getOutputAsString("RelCount")), Integer.parseInt(message
                        .getOutputAsString("AbsCount")));
    }

    /**
     * Returns the number of milliseconds until the sleep timer goes off.
     * 
     * @return the remaining sleep timer duration
     *         @ * Signals that an I/O exception has occurred.
     */
    public long getRemainingSleepTimerDuration() {
        // TODO verify these strings.
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetRemainingSleepTimerDuration");
        message.setInput("InstanceID", "0");
        executeImmediate(message);
        // ISO8601 Time - difficult to implement with java.util.calendar
        return TimeUtilities.convertDurationToLong(message.getOutputAsString("RemainingSleepTimerDuration"));
    }

    /**
     * Gets the running alarm properties.
     * 
     * @return the properties of the alarm service.
     *         @ * Signals that an I/O exception has occurred.
     */
    public AlarmProperties getRunningAlarmProperties() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "RunAlarm");
        message.setInput("InstanceID", "0");
        executeImmediate(message);
        return new AlarmProperties(Integer.parseInt(message.getOutputAsString("AlarmID")), Integer.parseInt(message.getOutputAsString("GroupID")),
                TimeUtilities.convertISO8601DateToLong(message.getOutputAsString("LoggedStartTime")));
    }

    /**
     * Gets the transport info.
     * 
     * @return Information about the audio transport.
     *         @ * Signals that an I/O exception has occurred.
     */
    public TransportInfo getTransportInfo() {
        if (!(state.containsKey(AVTransportEventType.TransportState) && state.containsKey(AVTransportEventType.TransportStatus) && state
                .containsKey(AVTransportEventType.TransportPlaySpeed))) {
            SonosActionInvocation message = messageFactory.getMessage(getService(), "GetTransportInfo");
            message.setInput("InstanceID", "0");
            executeImmediate(message);
            state.put(AVTransportEventType.TransportState, message.getOutputAsString("CurrentTransportState"));
            state.put(AVTransportEventType.TransportStatus, message.getOutputAsString("CurrentTransportStatus"));
            state.put(AVTransportEventType.TransportPlaySpeed, message.getOutputAsString("CurrentSpeed"));
        }
        return new TransportInfo(state.get(AVTransportEventType.TransportState), state.get(AVTransportEventType.TransportStatus),
                state.get(AVTransportEventType.TransportPlaySpeed));
    }

    /**
     * Gets the transport settings.
     * 
     * @return the current transport settings for the service.
     *         @ * Signals that an I/O exception has occurred.
     */
    public TransportSettings getTransportSettings() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "GetTransportSettings");
        message.setInput("InstanceID", "0");
        executeImmediate(message);
        return new TransportSettings(message.getOutputAsString("PlayMode"), message.getOutputAsString("RecQualityMode"));
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
        LOGGER.debug("recieved AVTransport notification: " + varName + "=" + newValue);
        try {
            Map<AVTransportEventType, String> changes = ResultParser.parseAVTransportEvent(newValue);
            state.putAll(changes);
            // TODO only fire real changes - currently the changes variable contains everything.
            fireStateChanged(changes.keySet());
        } catch (SAXException e) {
            LOGGER.error("Could not parse change event: ", e);
        }
    }

    /**
     * Move to the next track.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void next() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "Next");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * Not too sure...
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void nextSection() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "NextSection");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * Pauses playback.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void pause() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "Pause");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * Starts the playback.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void play() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), PLAY_ACTION);
        message.setInput("InstanceID", "0");
        message.setInput("Speed", "1");
        execute(message);
    }

    /**
     * Move to the previous track.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void previous() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "Previous");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * not too sure...
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void previousSection() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "PreviousSection");
        message.setInput("InstanceID", "0");
        execute(message);
    }

    /**
     * unregisters the given listener: no further notifications shall be recieved.
     * 
     * @param l
     *            the l
     */
    public void removeAvTransportListener(final AVTransportListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    /**
     * Removes the given entry from the queue.
     * 
     * @param entry
     *            the entry
     *            @ * Signals that an I/O exception has occurred.
     */
    public void removeTrackFromQueue(final Entry entry) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "RemoveTrackFromQueue");
        message.setInput("InstanceID", "0");
        message.setInput("ObjectID", entry.getId());
        execute(message);
    }

    /**
     * Moves a selection of tracks in the queue.
     * 
     * @param startAt
     *            the index of the first track (zero-relative)
     * @param num
     *            the number of tracks to move
     * @param insertBefore
     *            the position to place the tracks (zero-relative)
     *            @ * Signals that an I/O exception has occurred.
     */
    public void reorderTracksInQueue(final int startAt, final int num, final int insertBefore) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "ReorderTracksInQueue");
        message.setInput("InstanceID", "0");
        message.setInput("StartingIndex", String.valueOf(startAt + 1));
        message.setInput("NumberOfTracks", String.valueOf(num));
        message.setInput("InsertBefore", String.valueOf(insertBefore + 1));
        execute(message);
    }

    /**
     * TODO what does this method do?.
     * 
     * @param alarmId
     *            the alarm id
     * @param loggedStartTime
     *            the logged start time
     * @param duration
     *            the duration
     * @param programUri
     *            the program uri
     * @param programMetadata
     *            the program metadata
     * @param playMode
     *            the play mode
     * @param volume
     *            the volume
     * @param includeLinkedZones
     *            the include linked zones
     *            @ * Signals that an I/O exception has occurred.
     */
    public void runAlarm(final int alarmId, final long loggedStartTime, final long duration, final String programUri, final String programMetadata,
            final PlayMode playMode, final int volume, final boolean includeLinkedZones) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "RunAlarm");
        message.setInput("InstanceID", "0");
        message.setInput("AlarmID", String.valueOf(alarmId));
        message.setInput("LoggedStartTime", TimeUtilities.convertLongToISO8601Date(loggedStartTime)); // TODO is this ISO9601 time?
        message.setInput("Duration", String.valueOf(duration));
        message.setInput("ProgramURI", programUri);
        message.setInput("ProgramMetaData", programMetadata);
        message.setInput("PlayMode", playMode);
        message.setInput("Volume", String.valueOf(volume));
        message.setInput("IncludeLinkedZones", includeLinkedZones ? "1" : "0");
        execute(message);
    }

    /**
     * Saves the given queue to the given title.
     * 
     * @param title
     *            the title
     * @param queue
     *            the object id of the playlist, or "" for the current queue
     * @return the new ObjectID to refer to the saved queue
     *         @ * Signals that an I/O exception has occurred.
     */
    public String saveQueue(final String title, final String queue) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SaveQueue");
        message.setInput("InstanceID", "0");
        message.setInput("Title", title);
        message.setInput("ObjectID", queue);
        executeImmediate(message);
        return message.getOutputAsString("AssignedObjectID");
    }

    /**
     * NOT IMPLEMENTED.
     * 
     * @param target
     *            the target
     *            @ * Signals that an I/O exception has occurred.
     * @see SeekTargetFactory
     */
    public void seek(final SeekTarget target) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "Seek");
        message.setInput("InstanceID", "0");
        message.setInput("Unit", target.getSeekMode());
        message.setInput("Target", target.getTarget());
        execute(message);
    }

    /**
     * Sets the item currently playing. Does not modify the queue.
     * 
     * @param entry
     *            an Entry whose res and metadata (id, parentId, title) are used to
     *            message the zoneplayer.
     *            @ * Signals that an I/O exception has occurred.
     */
    public void setAvTransportUri(final Entry entry) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), SET_AV_TRANSPORT_URI_ACTION);
        message.setInput("InstanceID", "0"); // no instance id required
        message.setInput("CurrentURI", entry.getRes());
        String metadata = compileMetadataString(entry);
        message.setInput("CurrentURIMetaData", metadata);
        LOGGER.debug("SetAvTransportURI(0," + entry.getRes() + "," + metadata + ")");
        execute(message);
        // ignore result.
    }

    /**
     * Sets the AV Transport URI to be the queue on the zone player indicated by <code>zoneId</code>.
     * 
     * @param zoneId
     *            the zone player whose queue we are going to play (see {@link ZonePlayer#getId()})
     *            @ * Signals that an I/O exception has occurred.
     */
    public void setAvTransportUriToQueue(final String zoneId) {
        setAvTransportUri(new Entry("", "", "", "", "", "", "", "x-rincon-queue:" + zoneId + "#0", ""));
    }

    /**
     * Applies the new play mode.
     * 
     * @param mode
     *            the new play mode
     *            @ * Signals that an I/O exception has occurred.
     */
    public void setPlayMode(final PlayMode mode) {

        // if (shuffle && repeat) {
        // mode = PlayMode.SHUFFLE;
        // } else if (shuffle) {
        // mode = PlayMode.SHUFFLE_NOREPEAT;
        // } else if (repeat) {
        // mode = PlayMode.REPEAT_ALL;
        // } else {
        // mode = PlayMode.NORMAL;
        // }

        SonosActionInvocation message = messageFactory.getMessage(getService(), "SetPlayMode");
        message.setInput("InstanceID", "0");
        message.setInput("NewPlayMode", mode.toString());
        execute(message);
    }

    /**
     * Delays alarm detonation by the given duration.
     * 
     * @param duration
     *            the duration
     *            @ * Signals that an I/O exception has occurred.
     */
    public void snoozeAlarm(final long duration) {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "SnoozeAlarm");
        message.setInput("InstanceID", "0");
        message.setInput("Duration", TimeUtilities.convertLongToISO8601Duration(duration));
        execute(message);
    }

    /**
     * Stops playback.
     * 
     * @ * Signals that an I/O exception has occurred.
     */
    public void stop() {
        SonosActionInvocation message = messageFactory.getMessage(getService(), "Stop");
        message.setInput("InstanceID", "0");
        execute(message);
    }

}
