package org.tensin.sonos;

import org.tensin.sonos.upnp.ISonosBrowseListener;

/**
 * The Interface ISonos.
 */
public interface ISonos {

    /**
     * Adds the.
     * 
     * @param uri
     *            the uri
     */
    public abstract void add(final String uri);

    /* content service calls */
    /**
     * Browse.
     * 
     * @param _id
     *            the _id
     * @param cb
     *            the cb
     */
    public abstract void browse(final String _id, final ISonosBrowseListener cb);

    public abstract void browseMetadata(final String _id, final ISonosBrowseListener cb);

    /**
     * Destroy.
     * can be used to delete saved queues (SQ:*)
     * 
     * @param id
     *            the id
     */
    public abstract void destroy(final String id);

    /*
     * GetMediaInfo:
     * NrTracks and CurrentURI indicate active queue and size
     */
    /* CurrentTransportState STOPPED | PAUSED_PLAYBACK | PLAYING | */
    /* transport controls */
    /**
     * Gets the position.
     * 
     * @return the position
     */
    public abstract void getPosition();

    /**
     * Gets the transport uri.
     * 
     * @return the transport uri
     */
    public abstract String getTransportURI();

    /**
     * Gets the zone name.
     * 
     * @return the zone name
     */
    public abstract String getZoneName();

    /**
     * Linein.
     * 
     * @param line
     *            the line
     */
    public abstract void linein(final String line);

    /**
     * Move.
     * 
     * @param from
     *            the from
     * @param to
     *            the to
     */
    public abstract void move(final int from, final int to);

    /**
     * Next.
     */
    public abstract void next();

    /**
     * Pause.
     */
    public abstract void pause();

    /**
     * Play.
     */
    public abstract void play();

    /**
     * Shuffle.
     * 
     * @param shuffle
     *            the shuffle
     * @param repeat
     *            the repeat
     */
    public abstract void playmode(final boolean shuffle, final boolean repeat);

    /**
     * Prev.
     */
    public abstract void prev();

    /**
     * Refresh zone attributes.
     */
    public void refreshZoneAttributes();

    /**
     * Removes the.
     * 
     * @param id
     *            the id
     */
    public abstract void remove(final String id);

    /**
     * Removes the all.
     */
    public abstract void removeAll();

    /* queue management */
    /**
     * Save.
     * 
     * @param name
     *            the name
     * @param uri
     *            the uri
     */
    public abstract void save(final String name, final String uri);

    /**
     * Seek track.
     * 
     * @param nr
     *            the nr
     */
    public abstract void seekTrack(final int nr);

    /**
     * Sets the crossfade.
     * 
     * @param b
     *            the new crossfade
     */
    public abstract void setCrossfade(final boolean b);

    /**
     * Sets the mute.
     * 
     * @param mute
     *            the new mute
     */
    public abstract void setMute(final boolean mute);

    /**
     * Sets the transport uri.
     * 
     * @param uri
     *            the new transport uri
     */
    public abstract void setTransportURI(final String uri);

    /**
     * Stop.
     */
    public abstract void stop();

    /**
     * Trace_browse.
     * 
     * @param x
     *            the x
     */
    public abstract void trace_browse(final boolean x);

    /**
     * Trace_io.
     * 
     * @param x
     *            the x
     */
    public abstract void trace_io(final boolean x);

    /**
     * Trace_reply.
     * 
     * @param x
     *            the x
     */
    public abstract void trace_reply(final boolean x);

    /* volume controls */
    /**
     * Volume.
     * 
     * @return the int
     */
    public abstract int volume();

    /**
     * Volume.
     * 
     * @param vol
     *            the vol
     */
    public abstract void volume(final int vol);
}