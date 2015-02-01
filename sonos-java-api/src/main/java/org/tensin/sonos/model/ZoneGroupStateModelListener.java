package org.tensin.sonos.model;

/**
 * The listener interface for receiving zoneGroupStateModel events.
 * The class that is interested in processing a zoneGroupStateModel
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addZoneGroupStateModelListener<code> method. When
 * the zoneGroupStateModel event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ZoneGroupStateModelEvent
 */
public interface ZoneGroupStateModelListener {
	
	/**
	 * Zone group added.
	 *
	 * @param group the group
	 * @param source the source
	 */
	public void zoneGroupAdded(ZoneGroup group, ZoneGroupStateModel source);
	
	/**
	 * Zone group members changed.
	 *
	 * @param group the group
	 * @param source the source
	 */
	public void zoneGroupMembersChanged(ZoneGroup group, ZoneGroupStateModel source);
	
	/**
	 * Zone group removed.
	 *
	 * @param group the group
	 * @param source the source
	 */
	public void zoneGroupRemoved(ZoneGroup group, ZoneGroupStateModel source);
}
