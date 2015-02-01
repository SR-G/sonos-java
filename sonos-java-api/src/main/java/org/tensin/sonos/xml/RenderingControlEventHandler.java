/*
   Copyright 2008 davidwheeler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.tensin.sonos.xml;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class RenderingControlEventHandler.
 */
public class RenderingControlEventHandler extends DefaultHandler {
  
  /**
   * The Enum RenderingControlEventType.
   */
  public static enum RenderingControlEventType {
    
    /** The volume master. */
    VOLUME_MASTER, 
    
    /** The volume rf. */
    VOLUME_RF, 
    
    /** The volume lf. */
    VOLUME_LF,
    
    /** The mute master. */
    MUTE_MASTER,
    
    /** The mute lf. */
    MUTE_LF,
    
    /** The mute rf. */
    MUTE_RF, 
    
    /** The bass. */
    BASS,
    
    /** The treble. */
    TREBLE,
    
    /** The loudness. */
    LOUDNESS,
    
    /** The output fixed. */
    OUTPUT_FIXED,
    
    /** The preset name. */
    PRESET_NAME;
  }

  /** The changes. */
  private final Map<RenderingControlEventHandler.RenderingControlEventType, String> changes = new HashMap<RenderingControlEventHandler.RenderingControlEventType, String>();

  /** The get preset name. */
  private boolean getPresetName=false;
  
  /** The preset name. */
  private String presetName;
  
  /** {@inheritDoc}
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (getPresetName) {
      presetName = new String(ch, start, length);
    }
  }

  /** {@inheritDoc}
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (getPresetName) {
      getPresetName = false;
      changes.put(RenderingControlEventType.PRESET_NAME, presetName);
    }
  }

  /**
   * Gets the changes.
   *
   * @return the changes
   */
  public Map<RenderingControlEventType, String> getChanges() {
    return changes;
  }

  /** {@inheritDoc}
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if ("Volume".equals(qName)) {
      if ("Master".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.VOLUME_MASTER, atts.getValue("val"));
      } else if ("LF".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.VOLUME_LF, atts.getValue("val"));
      } else if ("RF".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.VOLUME_RF, atts.getValue("val"));
      } // ignore other channels
    } else if ("Mute".equals(qName)) {
      if ("Master".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.MUTE_MASTER, atts.getValue("val"));
      } else if ("LF".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.MUTE_LF, atts.getValue("val"));
      } else if ("RF".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.MUTE_RF, atts.getValue("val"));
      } // ignore other channels
    } else if ("Bass".equals(qName)) {
      changes.put(RenderingControlEventType.BASS, atts.getValue("val"));
    } else if ("Treble".equals(qName)) {
      changes.put(RenderingControlEventType.TREBLE, atts.getValue("val"));
    } else if ("Loudness".equals(qName)) {
      if ("Master".equals(atts.getValue("channel"))) {
        changes.put(RenderingControlEventType.LOUDNESS, atts.getValue("val"));
      } // ignore other channels
    } else if ("OutputFixed".equals(qName)) {
      changes.put(RenderingControlEventType.OUTPUT_FIXED, atts.getValue("val"));
    } else if ("PresetNameList".equals(qName)) {
      getPresetName=true;
    }
  }

  
}
