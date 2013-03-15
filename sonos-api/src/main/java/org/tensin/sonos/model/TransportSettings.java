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
 * An immutable data transfer object containing a set of transport settings.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class TransportSettings {
  
  /** The play mode. */
  private String playMode;
  
  /** The rec quality mode. */
  private String recQualityMode;
  
  /**
   * Instantiates a new transport settings.
   *
   * @param playMode the play mode
   * @param recQualityMode the rec quality mode
   */
  public TransportSettings(String playMode, String recQualityMode) {
    this.playMode = playMode;
    this.recQualityMode = recQualityMode;
  }

  /**
   * Gets the play mode.
   *
   * @return the play mode
   */
  public String getPlayMode() {
    return playMode;
  }

  /**
   * Gets the rec quality mode.
   *
   * @return the rec quality mode
   */
  public String getRecQualityMode() {
    return recQualityMode;
  }

}
