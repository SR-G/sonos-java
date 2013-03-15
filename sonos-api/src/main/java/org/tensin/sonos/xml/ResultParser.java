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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.model.Entry;
import org.tensin.sonos.model.TrackMetaData;
import org.tensin.sonos.model.ZoneGroupState;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parses the String result from a zone player into a more useable type.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ResultParser {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultParser.class);

    /**
     * Gets the entries from string result.
     * 
     * @param xml
     *            the xml
     * @return a list of Entrys from the given xml string.
     * @throws SAXException
     *             the sAX exception
     */
    public static List<Entry> getEntriesFromStringResult(final String xml) throws SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        EntryHandler handler = new EntryHandler();
        reader.setContentHandler(handler);
        try {
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            // This should never happen - we're not performing I/O!
            LOGGER.error("Could not parse entries: ", e);
        }
        return handler.getArtists();
    }

    /**
     * Gets the group state from result.
     * 
     * @param xml
     *            the xml
     * @return zone group state from the given xml
     * @throws SAXException
     *             the sAX exception
     */
    public static ZoneGroupState getGroupStateFromResult(final String xml) throws SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        ZoneGroupStateHandler handler = new ZoneGroupStateHandler();
        reader.setContentHandler(handler);
        try {
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            // This should never happen - we're not performing I/O!
            LOGGER.error("Could not parse group state: ", e);
        }

        return new ZoneGroupState(handler.getGroups());

    }

    /**
     * Parses the av transport event.
     * 
     * @param xml
     *            the xml
     * @return the map
     * @throws SAXException
     *             the sAX exception
     */
    public static Map<AVTransportEventHandler.AVTransportEventType, String> parseAVTransportEvent(final String xml) throws SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        AVTransportEventHandler handler = new AVTransportEventHandler();
        reader.setContentHandler(handler);
        try {
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            // This should never happen - we're not performing I/O!
            LOGGER.error("Could not parse AV Transport Event: ", e);
        }
        return handler.getChanges();
    }

    /**
     * Parses the rendering control event.
     * 
     * @param xml
     *            the xml
     * @return the map
     * @throws SAXException
     *             the sAX exception
     */
    public static Map<RenderingControlEventHandler.RenderingControlEventType, String> parseRenderingControlEvent(final String xml) throws SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        RenderingControlEventHandler handler = new RenderingControlEventHandler();
        reader.setContentHandler(handler);
        try {
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            // This should never happen - we're not performing I/O!
            LOGGER.error("Could not parse Rendering Control event: ", e);
        }
        return handler.getChanges();
    }

    /**
     * Parses the track meta data.
     * 
     * @param xml
     *            the xml
     * @return the track meta data
     * @throws SAXException
     *             the sAX exception
     */
    public static TrackMetaData parseTrackMetaData(final String xml) throws SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        TrackMetaDataHandler handler = new TrackMetaDataHandler();
        reader.setContentHandler(handler);
        try {
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            // This should never happen - we're not performing I/O!
            LOGGER.error("Could not parse AV Transport Event: ", e);
        }
        return handler.getMetaData();
    }
}
