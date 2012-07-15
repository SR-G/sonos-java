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
 * An immutable data transfer object representing information about media.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class MediaInfo {

  /** The num tracks. */
  private final int numTracks;
  
  /** The media duration. */
  private final long mediaDuration;
  
  /** The current uri. */
  private final String currentURI;
  
  /** The current uri meta data. */
  private final TrackMetaData currentURIMetaData;
  
  /** The next uri. */
  private final String nextURI;
  
  /** The next uri meta data. */
  private final String nextURIMetaData;
  
  /** The play medium. */
  private final String playMedium;
  
  /** The record medium. */
  private final String recordMedium;
  
  /** The write status. */
  private final String writeStatus;

  /**
   * This only seems to be useful for num tracks and current URI - even
   * currentURIMetadata seems to return garbage.
   *
   * @param numTracks the num tracks
   * @param mediaDuration the media duration
   * @param currentURI the current uri
   * @param currentURIMetaData the current uri meta data
   * @param nextURI the next uri
   * @param nextURIMetaData the next uri meta data
   * @param playMedium the play medium
   * @param recordMedium the record medium
   * @param writeStatus the write status
   */
  public MediaInfo(String numTracks, long mediaDuration, 
      String currentURI, TrackMetaData currentURIMetaData, 
      String nextURI, String nextURIMetaData, 
      String playMedium, String recordMedium, 
      String writeStatus) {
    this.numTracks = Integer.parseInt(numTracks);
    this.mediaDuration = mediaDuration;
    this.currentURI = currentURI;
    this.nextURI = nextURI;
    this.currentURIMetaData = currentURIMetaData;
    this.nextURIMetaData = nextURIMetaData;
    this.playMedium = playMedium;
    this.recordMedium = recordMedium;
    this.writeStatus = writeStatus;
  }

  /**
   * Gets the current uri.
   *
   * @return the current uri
   */
  public String getCurrentURI() {
    return currentURI;
  }

  /**
   * Gets the current uri meta data.
   *
   * @return the current uri meta data
   */
  public TrackMetaData getCurrentURIMetaData() {
    return currentURIMetaData;
  }

  /**
   * Gets the media duration.
   *
   * @return the duration of the media, or -1 if this is not implemented.
   */
  public long getMediaDuration() {
    return mediaDuration;
  }

  /**
   * Gets the next uri.
   *
   * @return the next uri
   */
  public String getNextURI() {
    return nextURI;
  }

  /**
   * Gets the next uri meta data.
   *
   * @return the next uri meta data
   */
  public String getNextURIMetaData() {
    return nextURIMetaData;
  }

  /**
   * Gets the num tracks.
   *
   * @return the num tracks
   */
  public int getNumTracks() {
    return numTracks;
  }

  /**
   * Gets the play medium.
   *
   * @return the play medium
   */
  public String getPlayMedium() {
    return playMedium;
  }

  /**
   * Gets the record medium.
   *
   * @return the record medium
   */
  public String getRecordMedium() {
    return recordMedium;
  }

  /**
   * Gets the write status.
   *
   * @return the write status
   */
  public String getWriteStatus() {
    return writeStatus;
  }

}
