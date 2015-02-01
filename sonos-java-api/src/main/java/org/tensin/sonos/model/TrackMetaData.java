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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.bean.BeanField;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Class TrackMetaData.
 */
public class TrackMetaData extends AbstractInfo implements IInfo {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();
    
    /** The id. */
    @BeanField
    private final String id;
    
    /** The parent id. */
    @BeanField
    private final String parentId;
    
    /** The resource. */
    @BeanField
    private final String resource;
    
    /** The stream content. */
    @BeanField
    private final String streamContent;
    
    /** The album art uri. */
    @BeanField
    private final String albumArtUri;
    
    /** The title. */
    @BeanField
    private final String title;
    
    /** The upnp class. */
    @BeanField
    private final String upnpClass;
    
    /** The creator. */
    @BeanField
    private final String creator;
    
    /** The album. */
    @BeanField
    private final String album;
    
    /** The album artist. */
    @BeanField
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
