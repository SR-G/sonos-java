/*
   Copyright 2007 David Wheeler

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
package org.tensin.sonos.model;

/**
 * An immutable data transfer object containing information regarding the state,
 * status and speed of a transport.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class TransportInfo {
  
  /**
   * The Enum TransportState.
   */
  public enum TransportState {
    
    /** The stopped. */
    STOPPED, 
    
    /** The playing. */
    PLAYING, 
    
    /** The paused playback. */
    PAUSED_PLAYBACK, 
    
    /** The transitioning. */
    TRANSITIONING
  }
  
  /** The state. */
  private final TransportState state;
  
  /** The status. */
  private final String status;
  
  /** The speed. */
  private final int speed;

  /**
   * Instantiates a new transport info.
   *
   * @param state the state
   * @param status the status
   * @param speed the speed
   */
  public TransportInfo(String state, String status, String speed) {
    this.state = TransportState.valueOf(state);
    this.status = status;
    this.speed = speed.equals("NOT_IMPLEMENTED") ? -1 : Integer.parseInt(speed);
  }

  /**
   * Gets the speed.
   *
   * @return the speed of playback, or -1 if this functionality is not supported.
   */
  public int getSpeed() {
    return speed;
  }

  /**
   * Gets the state.
   *
   * @return the state
   */
  public TransportState getState() {
    return state;
  }

  /**
   * Gets the status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }
  
  
}
