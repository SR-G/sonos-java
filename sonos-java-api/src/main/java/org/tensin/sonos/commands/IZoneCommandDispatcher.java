package org.tensin.sonos.commands;

import java.util.Collection;
import java.util.Map;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Interface IZoneCommandDispatcher.
 */
public interface IZoneCommandDispatcher {

    /**
     * Dispatch command.
     * 
     * @param command
     *            the command
     * @param zoneName
     *            the zone name
     */
    public abstract void dispatchCommand(final IZoneCommand command, final String zoneName);

    /**
     * Gets the executors.
     * 
     * @return the executors
     */
    public abstract Map<String, ZoneCommandExecutor> getExecutors();

    /**
     * Gets the zone command executor.
     * 
     * @param zoneName
     *            the zone name
     * @return the zone command executor
     */
    public abstract ZoneCommandExecutor getZoneCommandExecutor(final String zoneName);

    /**
     * Gets the zones names.
     * 
     * @return the zones names
     */
    public abstract Collection<String> getZonesNames();

    /**
     * Log summary.
     */
    public abstract void logSummary();

    /**
     * Register zone as available.
     * 
     * @param sonos
     *            the sonos
     * @param zoneName
     *            the zone name
     */
    public abstract void registerZoneAsAvailable(final ZonePlayer sonos, final String zoneName);

    /**
     * Register zone executor.
     * 
     * @param zoneName
     *            the zone name
     * @return the zone command executor
     */
    public abstract ZoneCommandExecutor registerZoneExecutor(final String zoneName);

    /**
     * Reset instance.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public abstract void resetInstance() throws SonosException;

    /**
     * Stop.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public abstract void stopExecutors() throws SonosException;

    /**
     * Wait end execution.
     * 
     * @param delay
     *            the delay
     * @param checkEmptyQueues
     *            the check empty queues
     * @throws SonosException
     *             the sonos exception
     */
    public abstract void waitEndExecution(final int delay, final boolean checkEmptyQueues) throws SonosException;

}