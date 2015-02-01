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
package org.tensin.sonos.model;

/**
 * The possible seek modes. NOTE that only TRACK_NR is required to be supported
 * on all devices. 
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public enum SeekMode {
  
  /** The track nr. */
  TRACK_NR("TRACK_NR"), 
  
  /** The abs time. */
  ABS_TIME("ABS_TIME"), 
  
  /** The rel time. */
  REL_TIME("REL_TIME"), 
  
  /** The abs count. */
  ABS_COUNT("ABS_COUNT"), 
  
  /** The rel count. */
  REL_COUNT("REL_COUNT"), 
  
  /** The channel freq. */
  CHANNEL_FREQ("CHANNEL_FREQ"), 
  
  /** The tape index. */
  TAPE_INDEX("TAPE-INDEX"), 
  
  /** The frame. */
  FRAME("FRAME");
  
  /** The mode string. */
  private final String modeString;

  /**
   * Instantiates a new seek mode.
   *
   * @param modeString the mode string
   */
  private SeekMode(String modeString) {
    this.modeString = modeString;
  }
  
  /**
   * Gets the mode string.
   *
   * @return the mode string
   */
  public String getModeString() {
    return modeString;
  }
}
