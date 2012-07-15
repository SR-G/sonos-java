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
package org.tensin.sonos.control;

import java.util.Set;

import org.tensin.sonos.xml.AVTransportEventHandler.AVTransportEventType;


/**
 * The listener interface for receiving AVTransport events.
 * The class that is interested in processing a AVTransport
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addAVTransportListener<code> method. When
 * the AVTransport event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AVTransportEvent
 */
public interface AVTransportListener {

  /**
   * Values changed.
   *
   * @param events the events
   * @param source the source
   */
  public void valuesChanged(Set<AVTransportEventType> events, AVTransportService source);
}
