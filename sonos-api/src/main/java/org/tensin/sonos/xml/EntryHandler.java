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
package org.tensin.sonos.xml;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.model.Entry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class EntryHandler.
 */
public class EntryHandler extends DefaultHandler {

    /**
     * The Enum Element.
     */
    private enum Element {

        /** The title. */
        TITLE,
        /** The class. */
        CLASS,
        /** The album. */
        ALBUM, ALBUM_ARTIST,
        /** The album art uri. */
        ALBUM_ART_URI,
        /** The creator. */
        CREATOR,
        /** The res. */
        RES,
        /** The track number. */
        TRACK_NUMBER
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntryHandler.class);

    // Maintain a set of elements about which it is unuseful to complain about.
    // This list will be initialized on the first failure case
    /** The ignore. */
    private static List<String> ignore = null;

    /** The id. */
    private String id;

    /** The parent id. */
    private String parentId;

    /** The upnp class. */
    private StringBuilder upnpClass = new StringBuilder();

    /** The res. */
    private StringBuilder res = new StringBuilder();

    /** The title. */
    private StringBuilder title = new StringBuilder();

    /** The album. */
    private StringBuilder album = new StringBuilder();

    /** The album. */
    private StringBuilder albumArtist = new StringBuilder();

    /** The album art uri. */
    private StringBuilder albumArtUri = new StringBuilder();

    /** The creator. */
    private StringBuilder creator = new StringBuilder();

    /** The track number. */
    private StringBuilder trackNumber = new StringBuilder();

    /** The element. */
    private Element element = null;

    /** The artists. */
    private final List<Entry> artists = new ArrayList<Entry>();

    /**
     * Instantiates a new entry handler.
     */
    EntryHandler() {
        // shouldn't be used outside of this package.
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (element == null) {
            return;
        }
        switch (element) {
        case TITLE:
            title.append(ch, start, length);
            break;
        case CLASS:
            upnpClass.append(ch, start, length);
            break;
        case RES:
            res.append(ch, start, length);
            break;
        case ALBUM:
            album.append(ch, start, length);
            break;
        case ALBUM_ARTIST:
            albumArtist.append(ch, start, length);
            break;
        case ALBUM_ART_URI:
            albumArtUri.append(ch, start, length);
            break;
        case CREATOR:
            creator.append(ch, start, length);
            break;
        case TRACK_NUMBER:
            trackNumber.append(ch, start, length);
            break;
        // no default
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equals("container") || qName.equals("item")) {
            element = null;

            int trackNumberVal = 0;
            try {
                trackNumberVal = Integer.parseInt(trackNumber.toString());
            } catch (Exception e) {
            }

            artists.add(new Entry(id, title.toString(), parentId, album.toString(), albumArtUri.toString(), creator.toString(), upnpClass.toString(), res
                    .toString(), trackNumberVal, albumArtist.toString()));
            title = new StringBuilder();
            upnpClass = new StringBuilder();
            res = new StringBuilder();
            album = new StringBuilder();
            albumArtUri = new StringBuilder();
            creator = new StringBuilder();
            trackNumber = new StringBuilder();
            albumArtist = new StringBuilder();
        }
    }

    /**
     * Gets the artists.
     * 
     * @return the artists
     */
    public List<Entry> getArtists() {
        return artists;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (qName.equals("container") || qName.equals("item")) {
            id = attributes.getValue("id");
            parentId = attributes.getValue("parentID");
        } else if (qName.equals("res")) {
            element = Element.RES;
        } else if (qName.equals("dc:title")) {
            element = Element.TITLE;
        } else if (qName.equals("upnp:class")) {
            element = Element.CLASS;
        } else if (qName.equals("dc:creator")) {
            element = Element.CREATOR;
        } else if (qName.equals("upnp:album")) {
            element = Element.ALBUM;
        } else if (qName.equals("r:albumArtist")) {
            element = Element.ALBUM_ARTIST;
        } else if (qName.equals("upnp:albumArtURI")) {
            element = Element.ALBUM_ART_URI;
        } else if (qName.equals("upnp:originalTrackNumber")) {
            element = Element.TRACK_NUMBER;
        } else {
            if (ignore == null) {
                ignore = new ArrayList<String>();
                ignore.add("DIDL-Lite");
            }

            if (!ignore.contains(localName)) {
                LOGGER.warn("did not recognise element named [" + localName + "], qname [" + qName + "]");
            }
            element = null;
        }
    }
}