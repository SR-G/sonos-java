/*
 * Copyright 2008 davidwheeler
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
package org.tensin.sonos.xml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class AVTransportEventHandler.
 */
public class AVTransportEventHandler extends DefaultHandler {

    /**
     * The Enum AVTransportEventType.
     */
    public static enum AVTransportEventType {
        
        /** The Transport state. */
        TransportState, 
 /** The Current play mode. */
 CurrentPlayMode, 
 /** The Current crossfade mode. */
 CurrentCrossfadeMode, 
 /** The Number of tracks. */
 NumberOfTracks, 
 /** The Current track. */
 CurrentTrack, 
 /** The Current section. */
 CurrentSection, 
 /** The Current track uri. */
 CurrentTrackURI, 
 /** The Current track duration. */
 CurrentTrackDuration, 
 /** The Current track meta data. */
 CurrentTrackMetaData, 
 /** The Playback storage medium. */
 PlaybackStorageMedium, 
 /** The AV transport uri. */
 AVTransportURI, 
 /** The AV transport uri meta data. */
 AVTransportURIMetaData, 
 /** The Current transport actions. */
 CurrentTransportActions, 
 /** The Transport status. */
 TransportStatus, 
 /** The Sleep timer generation. */
 SleepTimerGeneration, 
 /** The Alarm running. */
 AlarmRunning, 
 /** The Snooze running. */
 SnoozeRunning, 
 /** The Restart pending. */
 RestartPending, 
 /** The Transport play speed. */
 TransportPlaySpeed, 
 /** The Current media duration. */
 CurrentMediaDuration, 
 /** The Record storage medium. */
 RecordStorageMedium, 
 /** The Possible playback storage media. */
 PossiblePlaybackStorageMedia, 
 /** The Possible record storage media. */
 PossibleRecordStorageMedia, 
 /** The Record medium write status. */
 RecordMediumWriteStatus, 
 /** The Current record quality mode. */
 CurrentRecordQualityMode, 
 /** The Possible record quality modes. */
 PossibleRecordQualityModes, 
 /** The Next av transport uri. */
 NextAVTransportURI, 
 /** The Next av transport uri meta data. */
 NextAVTransportURIMetaData, 
 /** The Event. */
 Event, 
 /** The Instance id. */
 InstanceID, 
 /** The Next track uri. */
 NextTrackURI, 
 /** The Next track meta data. */
 NextTrackMetaData, 
 /** The Enqueued transport uri. */
 EnqueuedTransportURI, 
 /** The Enqueued transport uri meta data. */
 EnqueuedTransportURIMetaData;
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AVTransportEventHandler.class);
    /*
     * <Event xmlns="urn:schemas-upnp-org:metadata-1-0/AVT/" xmlns:r="urn:schemas-rinconnetworks-com:metadata-1-0/">
     * <InstanceID val="0">
     * <TransportState val="PLAYING"/>
     * <CurrentPlayMode val="NORMAL"/>
     * <CurrentPlayMode val="0"/>
     * <NumberOfTracks val="29"/>
     * <CurrentTrack val="12"/>
     * <CurrentSection val="0"/>
     * <CurrentTrackURI val="x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2012%20-%20Broken%20Box.wma"/>
     * <CurrentTrackDuration val="0:03:02"/>
     * <CurrentTrackMetaData val=
     * "&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;-1&quot; parentID=&quot;-1&quot; restricted=&quot;true&quot;&gt;&lt;res protocolInfo=&quot;x-file-cifs:*:audio/x-ms-wma:*&quot; duration=&quot;0:03:02&quot;&gt;x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2012%20-%20Broken%20Box.wma&lt;/res&gt;&lt;r:streamContent&gt;&lt;/r:streamContent&gt;&lt;dc:title&gt;Broken Box&lt;/dc:title&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:creator&gt;Queens Of The Stone Age&lt;/dc:creator&gt;&lt;upnp:album&gt;Lullabies To Paralyze&lt;/upnp:album&gt;&lt;r:albumArtist&gt;Queens Of The Stone Age&lt;/r:albumArtist&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;"
     * /><r:NextTrackURI val=
     * "x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2013%20-%20&apos;&apos;You%20Got%20A%20Killer%20Scene%20There,%20Man...&apos;&apos;.wma"
     * /><r:NextTrackMetaData val=
     * "&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;-1&quot; parentID=&quot;-1&quot; restricted=&quot;true&quot;&gt;&lt;res protocolInfo=&quot;x-file-cifs:*:audio/x-ms-wma:*&quot; duration=&quot;0:04:56&quot;&gt;x-file-cifs://192.168.1.1/Storage4/Sonos%20Music/Queens%20Of%20The%20Stone%20Age/Lullabies%20To%20Paralyze/Queens%20Of%20The%20Stone%20Age%20-%20Lullabies%20To%20Paralyze%20-%2013%20-%20&amp;apos;&amp;apos;You%20Got%20A%20Killer%20Scene%20There,%20Man...&amp;apos;&amp;apos;.wma&lt;/res&gt;&lt;dc:title&gt;&amp;apos;&amp;apos;You Got A Killer Scene There, Man...&amp;apos;&amp;apos;&lt;/dc:title&gt;&lt;upnp:class&gt;object.item.audioItem.musicTrack&lt;/upnp:class&gt;&lt;dc:creator&gt;Queens Of The Stone Age&lt;/dc:creator&gt;&lt;upnp:album&gt;Lullabies To Paralyze&lt;/upnp:album&gt;&lt;r:albumArtist&gt;Queens Of The Stone Age&lt;/r:albumArtist&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;"
     * /><r:EnqueuedTransportURI val="x-rincon-playlist:RINCON_000E582126EE01400#A:ALBUMARTIST/Queens%20Of%20The%20Stone%20Age"/><r:EnqueuedTransportURIMetaData val=
     * "&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;A:ALBUMARTIST/Queens%20Of%20The%20Stone%20Age&quot; parentID=&quot;A:ALBUMARTIST&quot; restricted=&quot;true&quot;&gt;&lt;dc:title&gt;Queens Of The Stone Age&lt;/dc:title&gt;&lt;upnp:class&gt;object.container&lt;/upnp:class&gt;&lt;desc id=&quot;cdudn&quot; nameSpace=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot;&gt;RINCON_AssociatedZPUDN&lt;/desc&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;"
     * />
     * <PlaybackStorageMedium val="NETWORK"/>
     * <AVTransportURI val="x-rincon-queue:RINCON_000E5812BC1801400#0"/>
     * <AVTransportURIMetaData val=""/>
     * <CurrentTransportActions val="Play, Stop, Pause, Seek, Next, Previous"/>
     * <TransportStatus val="OK"/>
     * <r:SleepTimerGeneration val="0"/>
     * <r:AlarmRunning val="0"/>
     * <r:SnoozeRunning val="0"/>
     * <r:RestartPending val="0"/>
     * <TransportPlaySpeed val="NOT_IMPLEMENTED"/>
     * <CurrentMediaDuration val="NOT_IMPLEMENTED"/>
     * <RecordStorageMedium val="NOT_IMPLEMENTED"/>
     * <PossiblePlaybackStorageMedia val="NONE, NETWORK"/>
     * <PossibleRecordStorageMedia val="NOT_IMPLEMENTED"/>
     * <RecordMediumWriteStatus val="NOT_IMPLEMENTED"/>
     * <CurrentRecordQualityMode val="NOT_IMPLEMENTED"/>
     * <PossibleRecordQualityModes val="NOT_IMPLEMENTED"/>
     * <NextAVTransportURI val="NOT_IMPLEMENTED"/>
     * <NextAVTransportURIMetaData val="NOT_IMPLEMENTED"/>
     * </InstanceID>
     * </Event>
     */

    /** The changes. */
    private final Map<AVTransportEventType, String> changes = new HashMap<AVTransportEventType, String>();

    /**
     * Gets the changes.
     *
     * @return the changes
     */
    public Map<AVTransportEventType, String> getChanges() {
        return changes;
    }

    /** {@inheritDoc}
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        /*
         * The events are all of the form <qName val="value"/> so we can get all
         * the info we need from here.
         */
        try {
            AVTransportEventType type = AVTransportEventType.valueOf(localName);
            changes.put(type, atts.getValue("val"));
        } catch (IllegalArgumentException e) {
            // this means that localName isn't defined in EventType, which is expected for some elements
            LOGGER.info(localName + " is not defined in EventType. ");
        }
    }

}
