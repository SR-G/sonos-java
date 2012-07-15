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

import java.util.ArrayList;
import java.util.List;

import org.tensin.sonos.control.ZonePlayer;


/**
 * The Class ZonePlayerModel.
 */
public class ZonePlayerModel {

	/** The zone players. */
	private final List<ZonePlayer> zonePlayers = new ArrayList<ZonePlayer>();
	
	/** The listeners. */
	private final List<ZonePlayerModelListener> listeners = new ArrayList<ZonePlayerModelListener>();

	/**
	 * Instantiates a new zone player model.
	 */
	public ZonePlayerModel() {

	}

	/**
	 * Adds the given zone player to the list in a sorted order.
	 *
	 * @param zp the zp
	 */
	public void addZonePlayer(ZonePlayer zp) {
		synchronized(zonePlayers) {
			zonePlayers.add(zp);
		}
		synchronized(listeners) {
			for (ZonePlayerModelListener l : listeners ) {
				l.zonePlayerAdded(zp, this);
			}
		}
	}

	/**
	 * Adds the zone player model listener.
	 *
	 * @param l the l
	 */
	public void addZonePlayerModelListener(ZonePlayerModelListener l) {
		synchronized(listeners) {
			listeners.add(l);
		}
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the zone player at the given index.
	 */
	public ZonePlayer get(int index) {
		synchronized(zonePlayers) {
			try {
				return zonePlayers.get(index);
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * Gets the all zones.
	 *
	 * @return a List of all the zone players
	 */
	public List<ZonePlayer> getAllZones() {
		synchronized(zonePlayers) {
			return zonePlayers;
		}
	}

	/**
	 * Gets the by id.
	 *
	 * @param id the ID of a zone player (excluding "UUID:").
	 * @return a zone player matching that id, or null if one could not be found.
	 */
	public ZonePlayer getById(String id) {
		synchronized(zonePlayers) {
			for (ZonePlayer zp : zonePlayers) {
				if (zp.getId().equals(id)) {
					return zp;
				}
			}
			return null;
		}
	}

	/**
	 * Gets the index of.
	 *
	 * @param zp the zp
	 * @return the index of the given zone player, or -1 if it is not in the list.
	 */
	public int getIndexOf(ZonePlayer zp) {
		synchronized(zonePlayers) {
			return zonePlayers.indexOf(zp);
		}
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		synchronized(zonePlayers) {
			return zonePlayers.size();
		}
	}

	/**
	 * Removes the given zone player from the list.
	 *
	 * @param zp the zp
	 */
	public void remove(ZonePlayer zp) {
		synchronized(zonePlayers) {
			this.zonePlayers.remove(zp);
		}
		synchronized(listeners) {
			for (ZonePlayerModelListener l : listeners) {
				l.zonePlayerRemoved(zp, this);
			}
		}
	}

	/**
	 * Removes the zone player model listener.
	 *
	 * @param l the l
	 */
	public void removeZonePlayerModelListener(ZonePlayerModelListener l) {
		synchronized(listeners) {
			listeners.remove(l);
		}
	}
}
