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
package org.tensin.sonos.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.control.ZonePlayer;

/**
 * An immutable data transfer object representing an entry in a zone players
 * music library. eg. a queue entry, or a line in.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class Entry implements Serializable, Comparable<Entry> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Entry.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1987394879345l;

    /** The id. */
    private final String id;

    /** The title. */
    private final String title;

    /** The parent id. */
    private final String parentId;

    /** The upnp class. */
    private final String upnpClass;

    /** The res. */
    private final String res;

    /** The album. */
    private final String album;

    /** The album art uri. */
    private final String albumArtUri;

    /** The album art uri. */
    private final String albumArtist;

    /** The creator. */
    private final String creator;

    /** The original track number. */
    private final int originalTrackNumber;

    /**
     * Instantiates a new entry.
     * 
     * @param id
     *            the id
     * @param title
     *            the title
     * @param parentId
     *            the parent id
     * @param album
     *            the album
     * @param albumArtUri
     *            the album art uri
     * @param creator
     *            the creator
     * @param upnpClass
     *            the upnp class
     * @param res
     *            the res
     * @param originalTrackNumber
     *            the original track number
     */
    public Entry(final String id, final String title, final String parentId, final String album, final String albumArtUri, final String creator,
            final String upnpClass, final String res, final int originalTrackNumber, final String albumArtist) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.album = album;
        this.albumArtist = albumArtist;
        this.albumArtUri = albumArtUri;
        this.creator = creator;
        this.upnpClass = upnpClass;
        this.res = res;
        this.originalTrackNumber = originalTrackNumber;
    }

    /**
     * Instantiates a new entry.
     * 
     * @param id
     *            the id
     * @param title
     *            the title
     * @param parentId
     *            the parent id
     * @param album
     *            the album
     * @param albumArtUri
     *            the album art uri
     * @param creator
     *            the creator
     * @param upnpClass
     *            the upnp class
     * @param res
     *            the res
     */
    public Entry(final String id, final String title, final String parentId, final String album, final String albumArtUri, final String creator,
            final String upnpClass, final String res, final String albumArtist) {
        this(id, title, parentId, album, albumArtUri, creator, upnpClass, res, -1, albumArtist);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Entry o) {
        return new CompareToBuilder().append(getTitle(), o.getTitle()).append(getAlbum(), o.getAlbum()).append(getCreator(), o.getCreator())
                .append(getOriginalTrackNumber(), o.getOriginalTrackNumber()).append(getAlbumArtUri(), o.getAlbumArtUri()).build();
    }

    /**
     * Gets the album.
     * 
     * @return the name of the album.
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Gets the album artist.
     * 
     * @return the album artist
     */
    public String getAlbumArtist() {
        return albumArtist;
    }

    /**
     * Gets the album art uri.
     * 
     * @return the URI for the album art.
     */
    public String getAlbumArtUri() {
        return StringEscapeUtils.unescapeXml(albumArtUri);
    }

    /**
     * Gets the album art url.
     * 
     * @param zp
     *            the zone player from which to retrieve the album art.
     * @return the URL containing the album art image.
     * @throws MalformedURLException
     *             the malformed url exception
     */
    public URL getAlbumArtURL(final ZonePlayer zp) throws MalformedURLException {
        String uri = getAlbumArtUri();
        if (uri.startsWith("/getAA")) {
            // need to use mpath. what does this mean??
            LOGGER.info("uri = " + uri);
        }

        return zp.appendUrl(uri);
    }

    /**
     * Gets the creator.
     * 
     * @return the name of the artist who created the entry.
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Gets the id.
     * 
     * @return the unique identifier of this entry.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the original track number.
     * 
     * @return the original track number
     */
    public int getOriginalTrackNumber() {
        return originalTrackNumber;
    }

    /**
     * Gets the parent id.
     * 
     * @return the unique identifier of the parent of this entry.
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Gets the res.
     * 
     * @return a URI of this entry.
     */
    public String getRes() {
        return res;
    }

    /**
     * Gets the title.
     * 
     * @return the title of the entry.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the upnp class.
     * 
     * @return the UPnP classname for this entry.
     */
    public String getUpnpClass() {
        return upnpClass;
    }

    /**
     * To string.
     * 
     * @return the title of the entry.
     */
    @Override
    public String toString() {
        return title;
    }

}
