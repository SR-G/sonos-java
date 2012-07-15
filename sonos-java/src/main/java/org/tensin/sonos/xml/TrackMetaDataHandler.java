/*
 * Copyright 2008 David Wheeler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.xml;


import org.tensin.sonos.model.TrackMetaData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * TODO this is almost the same as entry handler. surely I could use one or the other?
 * 
 * @author David WHEELER
 * @author Serge SIMON
 *
 */
public class TrackMetaDataHandler extends DefaultHandler {
  
  /**
   * The Enum CurrentElement.
   */
  private enum CurrentElement {
    
    /** The item. */
    item,
    
    /** The res. */
    res,
    
    /** The stream content. */
    streamContent,
    
    /** The album art uri. */
    albumArtURI,
    
    /** The title. */
    title,
    
    /** The upnp class. */
    upnpClass,
    
    /** The creator. */
    creator,
    
    /** The album. */
    album,
    
    /** The album artist. */
    albumArtist;
  }
  
  /** The current element. */
  private CurrentElement currentElement = null;
  
  /** The id. */
  private String id = "-1";
  
  /** The parent id. */
  private String parentId = "-1";
  
  /** The resource. */
  private StringBuilder resource = new StringBuilder();
  
  /** The stream content. */
  private StringBuilder streamContent = new StringBuilder();
  
  /** The album art uri. */
  private StringBuilder albumArtUri = new StringBuilder();
  
  /** The title. */
  private StringBuilder title = new StringBuilder();
  
  /** The upnp class. */
  private StringBuilder upnpClass = new StringBuilder();
  
  /** The creator. */
  private StringBuilder creator = new StringBuilder();
  
  /** The album. */
  private StringBuilder album = new StringBuilder();
  
  /** The album artist. */
  private StringBuilder albumArtist = new StringBuilder();

  /** {@inheritDoc}
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (currentElement != null) {
      switch (currentElement) {
        case item: 
          break; 
        case res: resource.append(ch, start, length);
          break;
        case streamContent: streamContent.append(ch, start, length);
          break;
        case albumArtURI: albumArtUri.append(ch, start, length);
          break;
        case title: title.append(ch, start, length);
          break;
        case upnpClass: upnpClass.append(ch, start, length);
          break;
        case creator: creator.append(ch, start, length);
          break;
        case album: album.append(ch, start, length);
          break;
        case albumArtist: albumArtist.append(ch, start, length);
          break;
      }
    }
  }

  /**
   * Gets the meta data.
   *
   * @return the meta data
   */
  public TrackMetaData getMetaData() {
    return new TrackMetaData(id, parentId, resource.toString(), 
        streamContent.toString(), albumArtUri.toString(), 
        title.toString(), upnpClass.toString(), creator.toString(),
        album.toString(), albumArtist.toString());
  }

  /** {@inheritDoc}
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("item".equals(localName)) {
      currentElement = CurrentElement.item;
      id = atts.getValue("id");
      parentId = atts.getValue("parentID");
    } else if ("res".equals(localName)) {
      currentElement = CurrentElement.res;
    } else if ("streamContent".equals(localName)) {
      currentElement = CurrentElement.streamContent;
    } else if ("albumArtURI".equals(localName)) {
      currentElement = CurrentElement.albumArtURI;
    } else if ("title".equals(localName)) {
      currentElement = CurrentElement.title;
    } else if ("class".equals(localName)) {
      currentElement = CurrentElement.upnpClass;
    } else if ("creator".equals(localName)) {
      currentElement = CurrentElement.creator;
    } else if ("album".equals(localName)) {
      currentElement = CurrentElement.album;
    } else if ("albumArtist".equals(localName)) {
      currentElement = CurrentElement.albumArtist;
    } else {
      // unknown element
      currentElement = null;
    }
  }

}
