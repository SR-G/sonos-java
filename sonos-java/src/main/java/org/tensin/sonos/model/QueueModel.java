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
package org.tensin.sonos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A model for the queue list.
 *
 * @author David WHEELER
 * @author Serge SIMON
 */
public class QueueModel {
  
  /** The list of entries in the queue. */
  private List<Entry> entries = new ArrayList<Entry>();
  
  /** The index of the entry that is currently playing. */
  private int nowPlaying = -1;

  /** The list of listeners. */
  private List<QueueModelListener> listeners = new ArrayList<QueueModelListener>();;
  
  /**
   * Adds a listener to be notified when the list of entries changes or the now
   * playing entry changes.
   *
   * @param listener the listener
   */
  public void addQueueModelListener(QueueModelListener listener) {
    this.listeners.add(listener);
  }
  
  /**
   * Notifies all listeners that the value of nowPlaying has changed.
   */
  private void fireEntriesChanged() {
    for (QueueModelListener listener : listeners) {
      listener.entriesChanged(this);
    }
  }
  
  /**
   * Notifies all listeners that the value of nowPlaying has changed.
   */
  private void fireNowPlayingChanged() {
    for (QueueModelListener listener : listeners) {
      listener.nowPlayingChanged(this);
    }
  }

  /**
   * Gets the entry at.
   *
   * @param index the index
   * @return the entry at the given index
   */
  public Entry getEntryAt(int index) {
    return entries.get(index);
  }
  
  /**
   * Gets the now playing.
   *
   * @return the index of the entry that is currently playing
   */
  public int getNowPlaying() {
    return nowPlaying;
  }

  /**
   * Gets the size.
   *
   * @return the number of entries in the queue
   */
  public int getSize() {
    return entries.size();
  }
  
  /**
   * Gets the title.
   *
   * @param index the index of the entry to get the title from
   * @return a String of the title of the entry at index
   */
  public String getTitle(int index) {
    return entries.get(index).getTitle();
  }

  /**
   * Index of.
   *
   * @param target the target
   * @return the index of the entry matching the given Entry
   */
  public int indexOf(Entry target) {
    for (int i=0; i<getSize(); i++) {
      Entry entry = getEntryAt(i);
      if (entry.getId().equals(target)) {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * Checks if is now playing.
   *
   * @param index the index
   * @return true if the given index matches the index marked as now playing
   */
  public boolean isNowPlaying(int index) {
    return index == nowPlaying;
  }
  
  /**
   * Removes the listener from the notification list.
   *
   * @param listener the listener
   */
  public void removeQueueModelListener(QueueModelListener listener) {
    this.listeners.remove(listener);
  }

  /**
   * Replaces the list of entries with the one provided.
   *
   * @param entries the new entries
   */
  public void setEntries(List<Entry> entries) {
    this.entries = entries;
    if (nowPlaying >= this.entries.size() -1) {
      setNowPlaying(-1);
    }
    fireEntriesChanged();
  }

  /**
   * Sets the index of the entry now playing.
   *
   * @param i the new index
   */
  public void setNowPlaying(int i) {
    nowPlaying = i;
    fireNowPlayingChanged();
  }
}