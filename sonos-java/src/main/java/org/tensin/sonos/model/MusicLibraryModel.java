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
 * The Interface MusicLibraryModel.
 */
public interface MusicLibraryModel {

  /**
   * Adds the given listener to be notified of new entries, or size changes.
   *
   * @param listener the listener
   */
  public void addListener(MusicLibraryListener listener);

  /**
   * Disposes the MusicLibrary model. 
   */
  public void dispose();

  /**
   * Gets the entry at.
   *
   * @param index the index
   * @return the entry at the given index
   */
  public Entry getEntryAt(int index);

  /**
   * Gets the size.
   *
   * @return the total number of entries in the model, loaded or otherwise
   */
  public int getSize();

  /**
   * Checks for entry for.
   *
   * @param index the index
   * @return <code>true</code> if the entry corresponding to index has been loaded
   */
  public boolean hasEntryFor(int index);

  /**
   * Index of.
   *
   * @param entry the entry
   * @return the index of the given entry, if it exists (-1 otherwise)
   */
  public int indexOf(Entry entry);

  /**
   * Removes the listener.
   *
   * @param listener the listener
   */
  public void removeListener(MusicLibraryListener listener);

  /**
   * Removes all listeners.
   */
  public void removeListeners();

}