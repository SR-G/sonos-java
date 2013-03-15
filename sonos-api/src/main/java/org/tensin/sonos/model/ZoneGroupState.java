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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An immutable data transfer object containing each of the known zone groups.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ZoneGroupState {

  /** The zone groups. */
  private final List<ZoneGroup> zoneGroups;
  
  /**
   * Instantiates a new zone group state.
   *
   * @param groups the groups
   */
  public ZoneGroupState(Collection<ZoneGroup> groups) {
	  
    this.zoneGroups = new CopyOnWriteArrayList<ZoneGroup>(groups);
  }
  
  /**
   * Gets the groups.
   *
   * @return the groups
   */
  public synchronized List<ZoneGroup> getGroups() {
    return zoneGroups;
  }
  
}
