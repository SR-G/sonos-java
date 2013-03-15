/*
 * Copyright 2007 David Wheeler
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
 * An immutable data transfer object containing information about the
 * capabilities of the Sonos device.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class DeviceCapabilities {
  
  /** The play media. */
  private String playMedia;
  
  /** The rec media. */
  private String recMedia;
  
  /** The rec quality modes. */
  private String recQualityModes;
  
  /**
   * Instantiates a new device capabilities.
   *
   * @param playMedia TODO
   * @param recMedia the rec media
   * @param recQualityModes the rec quality modes
   */
  public DeviceCapabilities(String playMedia, String recMedia, String recQualityModes) {
    this.playMedia = playMedia;
    this.recMedia = recMedia;
    this.recQualityModes = recQualityModes;
  }

  /**
   * Gets the play media.
   *
   * @return the play media
   */
  public String getPlayMedia() {
    return playMedia;
  }

  /**
   * Gets the rec media.
   *
   * @return the rec media
   */
  public String getRecMedia() {
    return recMedia;
  }

  /**
   * Gets the rec quality modes.
   *
   * @return the rec quality modes
   */
  public String getRecQualityModes() {
    return recQualityModes;
  }

}
