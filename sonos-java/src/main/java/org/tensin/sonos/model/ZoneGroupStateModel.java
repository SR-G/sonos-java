package org.tensin.sonos.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ZoneGroupStateModel.
 */
public class ZoneGroupStateModel {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneGroupStateModel.class);
    
    /** The old group state. */
    private ZoneGroupState oldGroupState = new ZoneGroupState(new ArrayList<ZoneGroup>());
    
    /** The listeners. */
    private final List<ZoneGroupStateModelListener> listeners = new ArrayList<ZoneGroupStateModelListener>();

    /**
     * Instantiates a new zone group state model.
     */
    public ZoneGroupStateModel() {
    }

    /**
     * Adds the listener.
     *
     * @param l the l
     */
    public void addListener(final ZoneGroupStateModelListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    /**
     * Fire group added.
     *
     * @param group the group
     */
    protected void fireGroupAdded(final ZoneGroup group) {
        LOGGER.info("ADDING GROUP: " + group.getId());
        for (ZoneGroupStateModelListener l : listeners) {
            l.zoneGroupAdded(group, this);
        }
    }

    /**
     * Fire group membership changed.
     *
     * @param group the group
     */
    protected void fireGroupMembershipChanged(final ZoneGroup group) {
        LOGGER.info("CHANGING GROUP: " + group.getId());
        for (ZoneGroupStateModelListener l : listeners) {
            l.zoneGroupMembersChanged(group, this);
        }
    }

    /**
     * Fire group removed.
     *
     * @param group the group
     */
    protected void fireGroupRemoved(final ZoneGroup group) {
        LOGGER.info("REMOVING GROUP: " + group.getId());
        for (ZoneGroupStateModelListener l : listeners) {
            l.zoneGroupRemoved(group, this);
        }
    }

    /**
     * Handle group update.
     *
     * @param newGroupState the new group state
     */
    public void handleGroupUpdate(final ZoneGroupState newGroupState) {

        synchronized (listeners) {
            // 1 Save the old group state
            ZoneGroupState ozgstate = oldGroupState;

            // 2 update the group state;
            oldGroupState = newGroupState;

            // 3 fire changes

            // First, look for groups that have been removed. These
            // are defined as groups which are in oldGroupState but not
            // in newGroupState
            for (ZoneGroup oldGroup : ozgstate.getGroups()) {
                if (!newGroupState.getGroups().contains(oldGroup)) {
                    fireGroupRemoved(oldGroup);
                }
            }

            // Now, Look for groups that have been added. These are defined
            // as groups who are present in newGroupState but not in
            // oldGroupState
            for (ZoneGroup newGroup : newGroupState.getGroups()) {
                if (!ozgstate.getGroups().contains(newGroup)) {
                    fireGroupAdded(newGroup);
                }
            }

            // Now, look for groups who have had group membership changes. These are
            // defined as groups which are present on both lists but whose elements
            // are not equal
            for (ZoneGroup newGroup : newGroupState.getGroups()) {
                for (ZoneGroup oldGroup : ozgstate.getGroups()) {
                    if (oldGroup.equals(newGroup)) {
                        if ((!oldGroup.getMembers().containsAll(newGroup.getMembers())) || (!newGroup.getMembers().containsAll(oldGroup.getMembers()))) {
                            fireGroupMembershipChanged(newGroup);
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes the listener.
     *
     * @param l the l
     */
    public void removeListener(final ZoneGroupStateModelListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
}
