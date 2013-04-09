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
import java.util.Collection;
import java.util.List;

/**
 * An immutable data transfer object containing the members and controller of a
 * zone group.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ZoneGroup {

	/** The members. */
	private final List<String> members;
	
	/** The coordinator. */
	private final String coordinator;
	
	/** The id. */
	private final String id;

	/**
	 * Instantiates a new zone group.
	 *
	 * @param id the id
	 * @param coordinator the coordinator
	 * @param members the members
	 */
	public ZoneGroup(String id, String coordinator, Collection<String> members) {
		this.members= new ArrayList<String>(members);
		if (!this.members.contains(coordinator)) {
			this.members.add(coordinator);
		}
		this.coordinator = coordinator;
		this.id = id;
	}

	/** {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof ZoneGroup) {
			ZoneGroup group = (ZoneGroup) obj;
			return group.getId().equals(getId());
		}
		return false;
	}

	/**
	 * Gets the coordinator.
	 *
	 * @return the coordinator
	 */
	public String getCoordinator() {
		return coordinator;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the members.
	 *
	 * @return the members
	 */
	public List<String> getMembers() {
		return members;
	}
	
	/** {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	  return id.hashCode();
	}
}
