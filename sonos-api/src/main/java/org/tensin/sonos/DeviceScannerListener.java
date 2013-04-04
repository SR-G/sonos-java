package org.tensin.sonos;

import org.tensin.sonos.control.ZonePlayer;

/**
 * Interface for listeners of DeviceScanner class
 * 
 * @author Mikkel Wendt-Larsen<phylock@gmail.com>
 */
public interface DeviceScannerListener {
    /**
     * DeviceScanner invokes this method after the ZonePlayer is
     * parsed and registered in the system.
     *
     * @param player the new player
     */
    void zonePlayerAdded(final ZonePlayer player);

    /**
     * DeviceScanner invokes this method after the ZonePlayer is
     * removed from the system right before it is disposed.
     *
     * @param player the deleted player, only cached parameters is available
     */
    void zonePlayerRemoved(final ZonePlayer player);
}
