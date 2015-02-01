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

import org.tensin.sonos.control.ZonePlayer;

/**
 * The listener interface for receiving zoneListSelection events.
 * The class that is interested in processing a zoneListSelection
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addZoneListSelectionListener<code> method. When
 * the zoneListSelection event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ZoneListSelectionEvent
 */
public interface ZoneListSelectionListener {

  /**
   * Zone selection changed to.
   *
   * @param newSelection the new selection
   */
  public void zoneSelectionChangedTo(ZonePlayer newSelection);

}
