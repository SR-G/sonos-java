package org.tensin.sonos.upnp;

/**
 * The Class SonosItem.
 */
public class SonosItem {

    public String host;

    /** The title. */
    public XMLSequence title;

    /** The album. */
    public XMLSequence album;

    /** The artist. */
    public XMLSequence artist;

    /** The track number. */
    public XMLSequence trackNumber;

    /** The play uri. */
    public XMLSequence albumArtURI;

    /** The play uri. */
    public XMLSequence playURI; /* to enqueue */

    /** The id uri. */
    public XMLSequence idURI; /* for browse/list */

    /** The flags. */
    public int flags;

    /** The Constant SONG. */
    public static final int SONG = 1;

    /** The Constant PLAYLIST. */
    public static final int PLAYLIST = 2;

    public SonosItem() {
        title = new XMLSequence();
        artist = new XMLSequence();
        album = new XMLSequence();
        idURI = new XMLSequence();
        playURI = new XMLSequence();
        trackNumber = new XMLSequence();
        albumArtURI = new XMLSequence();
        ;
    }

    public SonosItem(final String host) {
        this();
        this.host = host;
    }

    /**
     * Reset.
     */
    public void reset() {
        title.adjust(0, 0);
        album.adjust(0, 0);
        artist.adjust(0, 0);
        playURI.adjust(0, 0);
        idURI.adjust(0, 0);
        trackNumber.adjust(0, 0);
        albumArtURI.adjust(0, 0);
        flags = 0;
    }
}
