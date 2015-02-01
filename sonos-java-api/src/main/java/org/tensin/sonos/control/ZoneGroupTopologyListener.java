package org.tensin.sonos.control;

import org.tensin.sonos.model.ZoneGroupState;

/**
 * The listener interface for receiving zoneGroupTopology events.
 * The class that is interested in processing a zoneGroupTopology
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addZoneGroupTopologyListener<code> method. When
 * the zoneGroupTopology event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ZoneGroupTopologyEvent
 */
public interface ZoneGroupTopologyListener {
	  
  	/**
  	 * Zone group topology changed.
  	 *
  	 * @param zoneGroupState the zone group state
  	 */
	  public void zoneGroupTopologyChanged(ZoneGroupState zoneGroupState);
}
