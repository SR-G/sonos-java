package org.tensin.sonos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.upnp.SoapRPC;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.upnp.ISonosBrowseListener;
import org.tensin.sonos.upnp.XML;
import org.tensin.sonos.upnp.XML.Oops;
import org.tensin.sonos.upnp.XMLSequence;

/**
 * The Class Sonos.
 * (not thread-safe, not reentrant)
 * 
 */
public class SonosImpl implements ISonos {

    /**
     * The Enum PlayerState.
     */
    public enum PlayerState {

        /** The stopped. */
        STOPPED,
        /** The playing. */
        PLAYING,
        /** The paused playback. */
        PAUSED_PLAYBACK,
        /** The transitioning. */
        TRANSITIONING,
        /** The unknown. */
        UNKNOWN
    }

    /**
     * The Enum PlayMode.
     */
    public enum PlayMode {

        /** The shuffle norepeat. */
        SHUFFLE_NOREPEAT,
        /** The normal. */
        NORMAL,
        /** The repeat all. */
        REPEAT_ALL,
        /** The shuffle. */
        SHUFFLE
    }

    /** The trace_browse. */
    private boolean trace_browse;

    /** The xport. */
    private SoapRPC.Endpoint xport;

    /** The media. */
    private SoapRPC.Endpoint media;

    /** The render. */
    private SoapRPC.Endpoint render;

    /** The props. */
    private SoapRPC.Endpoint props;

    /** The rpc. */
    private SoapRPC rpc;

    /** The value. */
    private XMLSequence name, value;

    /** The item. */
    private SonosItem item;

    /** The host. */
    private final String host;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosImpl.class);

    /**
     * Instantiates a new sonos.
     * 
     * @param host
     *            the host
     */
    public SonosImpl(final String host) {
        this.host = host;
        init(host);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#add(java.lang.String)
     */
    @Override
    public void add(final String uri) {
        rpc.prepare(xport, "AddURIToQueue");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("EnqueuedURI", uri);
        rpc.simpleTag("EnqueuedURIMetaData", "");
        rpc.simpleTag("DesiredFirstTrackNumberEnqueued", 0);
        rpc.simpleTag("EnqueueAsNext", 0); // 0 = append, 1+ = insert
        rpc.invoke();
    }

    /* content service calls */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#browse(java.lang.String, org.tensin.sonos.upnp.ISonosBrowseListener)
     */
    @Override
    public void browse(final String _id, final ISonosBrowseListener cb) {
        int total, count, updateid;
        int n = 0;
        XML xml;

        do {
            rpc.prepare(media, "Browse");
            rpc.simpleTag("ObjectID", _id);
            rpc.simpleTag("BrowseFlag", "BrowseDirectChildren"); // BrowseMetadata
            rpc.simpleTag("Filter", "*");
            rpc.simpleTag("StartingIndex", n);
            rpc.simpleTag("RequestedCount", 100);
            rpc.simpleTag("SortCriteria", "");

            xml = rpc.invoke();
            try {
                xml.open("u:BrowseResponse");
                value.init(xml.read("Result"));

                // Eww, toString()? really? surely there's
                // a non-allocating Int parser somewhere
                // in the bloat that is java standard libraries?
                count = Integer.parseInt(xml.read("NumberReturned").toString());
                total = Integer.parseInt(xml.read("TotalMatches").toString());
                updateid = Integer.parseInt(xml.read("UpdateID").toString());

                /* descend in to the contained results */
                value.unescape();
                xml.init(value);
                n = processBrowseResults(xml, n, _id, cb);
            } catch (Oops e) {
                LOGGER.error("Error while browsing", e);
                break;
            }
        } while (n < total);
        cb.updateDone(_id);
    }

    /* content service calls */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#browse(java.lang.String, org.tensin.sonos.upnp.ISonosBrowseListener)
     */
    @Override
    public void browseMetadata(final String _id, final ISonosBrowseListener cb) {
        int total, count, updateid;
        int n = 0;
        XML xml;

        do {
            rpc.prepare(media, "Browse");
            rpc.simpleTag("ObjectID", _id);
            rpc.simpleTag("BrowseFlag", "BrowseMetadata"); // BrowseMetadata
            rpc.simpleTag("Filter", "*");
            rpc.simpleTag("StartingIndex", n);
            rpc.simpleTag("RequestedCount", 100);
            rpc.simpleTag("SortCriteria", "");

            xml = rpc.invoke();
            try {
                xml.open("u:BrowseResponse");
                value.init(xml.read("Result"));

                // Eww, toString()? really? surely there's
                // a non-allocating Int parser somewhere
                // in the bloat that is java standard libraries?
                count = Integer.parseInt(xml.read("NumberReturned").toString());
                total = Integer.parseInt(xml.read("TotalMatches").toString());
                updateid = Integer.parseInt(xml.read("UpdateID").toString());

                /* descend in to the contained results */
                value.unescape();
                xml.init(value);
                n = processBrowseResults(xml, n, _id, cb);
            } catch (Oops e) {
                LOGGER.error("Error while browsing", e);
                break;
            }
        } while (n < total);
        cb.updateDone(_id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#destroy(java.lang.String)
     */
    @Override
    public void destroy(final String id) {
        rpc.prepare(media, "DestroyObject");
        rpc.simpleTag("ObjectID", id);
        rpc.invoke();
    }

    /*
     * GetMediaInfo:
     * NrTracks and CurrentURI indicate active queue and size
     */
    /* CurrentTransportState STOPPED | PAUSED_PLAYBACK | PLAYING | */
    /* transport controls */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getPosition()
     */
    @Override
    public void getPosition() {
        rpc.prepare(xport, "GetMediaInfo");
        // rpc.prepare(xport,"GetPositionInfo");
        // rpc.prepare(xport,"GetTransportInfo");
        rpc.simpleTag("InstanceID", 0);
        XML xml = rpc.invoke();
        xml.print(System.out, 1024);
        xml.rewind();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getTransportURI()
     */
    @Override
    public String getTransportURI() {
        rpc.prepare(xport, "GetMediaInfo");
        rpc.simpleTag("InstanceID", 0);
        XML xml = rpc.invoke();
        try {
            xml.open("u:GetMediaInfoResponse");
            xml.read("NrTracks");
            xml.read("MediaDuration");
            return xml.read("CurrentURI").toString();
        } catch (XML.Oops x) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getZoneName()
     */
    @Override
    public String getZoneName() {
        try {
            rpc.prepare(props, "GetZoneAttributes");
            XML xml = rpc.invoke();
            xml.open("u:GetZoneAttributesResponse");
            return xml.read("CurrentZoneName").toString();
            // xml.read("CurrentIcon").toString();
        } catch (XML.Oops x) {
            return null;
        }
    }

    /**
     * Inits the.
     * 
     * @param host
     *            the host
     */
    void init(final String host) {
        name = new XMLSequence();
        value = new XMLSequence();
        item = new SonosItem();
        item.title = new XMLSequence();
        item.artist = new XMLSequence();
        item.album = new XMLSequence();
        item.idURI = new XMLSequence();
        item.playURI = new XMLSequence();

        rpc = new SoapRPC(host, 1400);

        xport = new SoapRPC.Endpoint("AVTransport:1", "/MediaRenderer/AVTransport/Control");
        media = new SoapRPC.Endpoint("ContentDirectory:1", "/MediaServer/ContentDirectory/Control");
        render = new SoapRPC.Endpoint("RenderingControl:1", "/MediaRenderer/RenderingControl/Control");
        props = new SoapRPC.Endpoint("DeviceProperties:1", "/DeviceProperties/Control");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#linein(java.lang.String)
     */
    @Override
    public void linein(final String line) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#move(int, int)
     */
    @Override
    public void move(final int from, final int to) {
        if ((from < 1) || (to < 1)) {
            return;
        }
        rpc.prepare(xport, "ReorderTracksInQueue");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("StartingIndex", from);
        rpc.simpleTag("NumberOfTracks", 1);
        rpc.simpleTag("InsertBefore", to);
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#next()
     */
    @Override
    public void next() {
        rpc.prepare(xport, "Next");
        rpc.simpleTag("InstanceID", 0);
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#pause()
     */
    @Override
    public void pause() {
        rpc.prepare(xport, "Pause");
        rpc.simpleTag("InstanceID", 0);
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#play()
     */
    @Override
    public void play() {
        rpc.prepare(xport, "Play");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("Speed", 1);
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#shuffle(boolean)
     */
    @Override
    public void playmode(final boolean shuffle, final boolean repeat) {
        PlayMode mode;
        if (shuffle && repeat) {
            mode = PlayMode.SHUFFLE;
        } else if (shuffle) {
            mode = PlayMode.SHUFFLE_NOREPEAT;
        } else if (repeat) {
            mode = PlayMode.REPEAT_ALL;
        } else {
            mode = PlayMode.NORMAL;
        }

        rpc.prepare(xport, "SetPlayMode");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("NewPlayMode", mode.name());
        rpc.invoke();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#prev()
     */
    @Override
    public void prev() {
        rpc.prepare(xport, "Previous");
        rpc.simpleTag("InstanceID", 0);
        rpc.invoke();
    }

    /**
     * Process browse results.
     * 
     * @param result
     *            the result
     * @param n
     *            the n
     * @param _id
     *            the _id
     * @param cb
     *            the cb
     * @return the int
     * @throws Oops
     *             the oops
     */
    int processBrowseResults(final XML result, int n, final String _id, final ISonosBrowseListener cb) throws XML.Oops {
        SonosItem item = this.item;
        if (trace_browse) {
            LOGGER.info("List : \n");
            result.print(System.out, 1024);
            result.rewind();
        }
        result.open("DIDL-Lite");
        while (result.more()) {
            String thing;
            n++;
            item.reset();
            item.idURI.init(result.getAttr("id"));
            try {
                result.open("item");
                thing = "item";
            } catch (XML.Oops x) {
                result.open("container"); // yuck!
                thing = "container";
            }
            while (result.tryRead(name, value)) {
                if (name.eq("dc:title")) {
                    item.title.init(value.unescape());
                    continue;
                }
                if (name.eq("dc:creator")) {
                    item.artist.init(value.unescape());
                    continue;
                }
                if (name.eq("upnp:album")) {
                    item.album.init(value.unescape());
                    continue;
                }
                if (name.eq("res")) {
                    item.playURI.init(value.unescape());
                    continue;
                }
                if (name.eq("upnp:class")) {
                    /* object.item.... vs object.container... */
                    if (value.charAt(7) == 'i') {
                        item.flags |= item.SONG;
                    }
                }
            }
            cb.updateItem(_id, n, item);
            result.close(thing);
        }
        return n;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#refreshZoneAttributes()
     */
    @Override
    public void refreshZoneAttributes() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#remove(java.lang.String)
     */
    @Override
    public void remove(final String id) {
        rpc.prepare(xport, "RemoveTrackFromQueue");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("ObjectID", id);
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#removeAll()
     */
    @Override
    public void removeAll() {
        rpc.prepare(xport, "RemoveAllTracksFromQueue");
        rpc.simpleTag("InstanceID", 0);
        rpc.invoke();
    }

    /* queue management */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#save(java.lang.String, java.lang.String)
     */
    @Override
    public void save(final String name, final String uri) {
        rpc.prepare(xport, "SaveQueue");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("Title", name); /* not unique */
        rpc.simpleTag("ObjectID", uri); /* "" for new */
        XML xml = rpc.invoke();
        /* saved queues are named SQ:# */
        xml.print(System.out, 1024);
        xml.rewind();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#seekTrack(int)
     */
    @Override
    public void seekTrack(final int nr) {
        if (nr < 1) {
            return;
        }
        rpc.prepare(xport, "Seek");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("Unit", "TRACK_NR");
        rpc.simpleTag("Target", nr);
        rpc.invoke();
        // does not start playing if not already in playback mode
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setCrossfade(boolean)
     */
    @Override
    public void setCrossfade(final boolean b) {
        rpc.prepare(render, "SetCrossfadeMode");
        rpc.simpleTag("InstanceID", 0);
        // rpc.simpleTag("Channel", "Master");
        if (b) {
            rpc.simpleTag("CrossfadeMode", 1);
        } else {
            rpc.simpleTag("CrossfadeMode", 0);
        }
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setMute(boolean)
     */
    @Override
    public void setMute(final boolean mute) {
        rpc.prepare(render, "SetMute");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("Channel", "Master");
        if (mute) {
            rpc.simpleTag("DesiredMute", 1);
        } else {
            rpc.simpleTag("DesiredMute", 0);
        }
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setTransportURI(java.lang.String)
     */
    @Override
    public void setTransportURI(final String uri) {
        rpc.prepare(xport, "SetAVTransportURI");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("CurrentURI", uri);
        rpc.simpleTag("CurrentURIMetaData", "");
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#stop()
     */
    @Override
    public void stop() {
        rpc.prepare(xport, "Stop");
        rpc.simpleTag("InstanceID", 0);
        rpc.invoke();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_browse(boolean)
     */
    @Override
    public void trace_browse(final boolean x) {
        trace_browse = x;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_io(boolean)
     */
    @Override
    public void trace_io(final boolean x) {
        rpc.trace_io = x;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_reply(boolean)
     */
    @Override
    public void trace_reply(final boolean x) {
        rpc.trace_reply = x;
    }

    /* volume controls */
    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#volume()
     */
    @Override
    public int volume() {
        int n;
        rpc.prepare(render, "GetVolume");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("Channel", "Master"); // Master | LF | RF
        XML xml = rpc.invoke();
        try {
            xml.open("u:GetVolumeResponse");
            n = Integer.parseInt(xml.read("CurrentVolume").toString());
            if (n < 0) {
                n = 0;
            }
            if (n > 100) {
                n = 100;
            }
            return n;
        } catch (XML.Oops x) {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#volume(int)
     */
    @Override
    public void volume(final int vol) { // 0-100
        if ((vol < 0) || (vol > 100)) {
            return;
        }
        rpc.prepare(render, "SetVolume");
        rpc.simpleTag("InstanceID", 0);
        rpc.simpleTag("Channel", "Master");
        rpc.simpleTag("DesiredVolume", vol);
        rpc.invoke();
    }
}
