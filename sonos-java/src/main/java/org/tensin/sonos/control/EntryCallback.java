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
package org.tensin.sonos.control;

import java.util.Collection;

import org.tensin.sonos.model.Entry;


/**
 * A callback for an asyncronous browse. 
 * 
 * @see ContentDirectoryService#getAllEntriesAsync(EntryCallback, String)
 * @author David WHEELER
 * @author Serge SIMON
 *
 */
public interface EntryCallback {

  /**
   * Called repeatedly with batches of results.
   *
   * @param handle the handle of the search instance
   * @param entries the results
   */
  public void addEntries(BrowseHandle handle, Collection<Entry> entries);

  /**
   * Called when the search is complete.
   *
   * @param handle the handle of the search instance
   * @param completedSuccessfully <code>true</code> if there were no errors and the search was not cancelled
   */
  public void retrievalComplete(BrowseHandle handle, boolean completedSuccessfully);

  /**
   * Called when the total number of matches is known.
   * @param handle the handle of the search instance
   * @param count the total number of matches
   */
  public void updateCount(BrowseHandle handle, int count);
}
