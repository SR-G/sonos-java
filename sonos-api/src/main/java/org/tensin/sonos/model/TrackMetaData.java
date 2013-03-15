/*
 * Copyright 2008 David Wheeler
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

import java.net.MalformedURLException;
import java.net.URL;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Class TrackMetaData.
 */
public class TrackMetaData {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackMetaData.class);
    
    /** The id. */
    private final String id;
    
    /** The parent id. */
    private final String parentId;
    
    /** The resource. */
    private final String resource;
    
    /** The stream content. */
    private final String streamContent;
    
    /** The album art uri. */
    private final String albumArtUri;
    
    /** The title. */
    private final String title;
    
    /** The upnp class. */
    private final String upnpClass;
    
    /** The creator. */
    private final String creator;
    
    /** The album. */
    private final String album;
    
    /** The album artist. */
    private final String albumArtist;

    /**
     * Instantiates a new track meta data.
     *
     * @param id the id
     * @param parentId the parent id
     * @param res the res
     * @param streamContent the stream content
     * @param albumArtUri the album art uri
     * @param title the title
     * @param upnpClass the upnp class
     * @param creator the creator
     * @param album the album
     * @param albumArtist the album artist
     */
    public TrackMetaData(final String id, final String parentId, final String res, final String streamContent, final String albumArtUri, final String title,
            final String upnpClass, final String creator, final String album, final String albumArtist) {
        this.id = id;
        this.parentId = parentId;
        resource = res;
        this.streamContent = streamContent;
        this.albumArtUri = albumArtUri;
        this.title = title;
        this.upnpClass = upnpClass;
        this.creator = creator;
        this.album = album;
        this.albumArtist = albumArtist;
    }

    /**
     * Gets the album.
     *
     * @return the album
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
     * @return the album art uri
     */
    public String getAlbumArtUri() {
        return albumArtUri;
    }

    /**
     * Gets the album art url.
     *
     * @param zp the zp
     * @return the album art url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getAlbumArtUrl(final ZonePlayer zp) throws MalformedURLException {
        String uri = getAlbumArtUri();
        if (uri.startsWith("/getAA")) {
            // need to use mpath. what does this mean??
            LOGGER.info("uri = " + uri);
        }

        return uri.length() == 0 ? null : new URL("http", zp.getIP().getHostAddress(), zp.getPort(), uri);
    }

    /**
     * Gets the creator.
     *
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the parent id.
     *
     * @return the parent id
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Gets the resource.
     *
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * Gets the stream content.
     *
     * @return the stream content
     */
    public String getStreamContent() {
        return streamContent;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the upnp class.
     *
     * @return the upnp class
     */
    public String getUpnpClass() {
        return upnpClass;
    }

}
