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
package org.tensin.sonos.control;

/**
 * The listener interface for receiving contentDirectory events.
 * The class that is interested in processing a contentDirectory
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addContentDirectoryListener<code> method. When
 * the contentDirectory event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ContentDirectoryEvent
 */
public interface ContentDirectoryListener {

  /**
   * Expected Event Variables:
   * SystemUpdateID
   * ContainerUpdateID
   * ShareListRefreshState [NOTRUN|RUNNING|DONE]
   * ShareIndexInProgress
   * ShareIndexLastError
   * UserRadioUpdateID
   * MasterRadioUpdateID
   * SavedQueuesUpdateID
   * ShareListUpdateID.
   */
  // TODO implement event
  public void handleEvent();
}
