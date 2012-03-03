package org.tensin.sonos.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.upnp.Sonos;

/**
 * The Class ZoneCommandDispatcher. Send the right command to the right zone executor, and keeps a map linking zone name to the corresponding executor.
 * ZoneCommandExecutor are threaded and have a queue containing every command to process. The thread only wakes up when a new command is available.
 * ZoneCommandExecutor can only works if the Sonos box has been found by discovery on the network : this event has to be fired by the registerZoneAsAvailable method.
 */
public class ZoneCommandDispatcher {

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(ZoneCommandDispatcher.class);

    /** The INSTANCE. */
    private static ZoneCommandDispatcher INSTANCE;

    /** The Constant executors. */
    private static final Map<String, ZoneCommandExecutor> executors = new HashMap<String, ZoneCommandExecutor>();

    /**
     * Gets the single instance of ZoneCommandDispatcher.
     * 
     * @return single instance of ZoneCommandDispatcher
     */
    public static ZoneCommandDispatcher getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZoneCommandDispatcher();
        }
        return INSTANCE;
    }

    /**
     * Dispatch command.
     * 
     * @param command
     *            the command
     * @param zoneName
     *            the zone name
     */
    public void dispatchCommand(final IZoneCommand command, final String zoneName) {
        LOGGER.info("Dispatching [" + command.getName() + "]");
        final ZoneCommandExecutor executor = registerZoneExecutor(zoneName);
        executor.addCommand(command);
    }

    /**
     * Register zone as available.
     * 
     * @param sonos
     *            the sonos
     * @param zoneName
     *            the zone name
     */
    public void registerZoneAsAvailable(final Sonos sonos, final String zoneName) {
        ZoneCommandExecutor executor = registerZoneExecutor(zoneName);
        executor.registerZoneAsAvailable(sonos);
    }

    /**
     * Register zone executor.
     * 
     * @param zoneName
     *            the zone name
     * @return the zone command executor
     */
    public ZoneCommandExecutor registerZoneExecutor(final String zoneName) {
        final String zoneNameUppercase = zoneName.toUpperCase();
        ZoneCommandExecutor executor;
        synchronized (executors) {
            if (!executors.containsKey(zoneNameUppercase)) {
                executor = new ZoneCommandExecutor(zoneNameUppercase);
                executor.start();
                executors.put(zoneNameUppercase, executor);
            } else {
                executor = executors.get(zoneNameUppercase);
            }
        }
        return executor;
    }

    /**
     * Wait end execution.
     * 
     * @param delay
     *            the delay
     * @param checkEmptyQueues
     *            the check empty queues
     */
    public void waitEndExecution(final int delay, final boolean checkEmptyQueues) {
        final long start = System.currentTimeMillis();
        long current;
        boolean active = true;
        boolean firstPass = true;
        while (active) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            current = System.currentTimeMillis();
            if (current > (start + delay)) {
                active = false;
            } else {
                if (checkEmptyQueues) {
                    // We can end the awaiting if all Zone Executor command queues are empty
                    synchronized (executors) {
                        boolean allEnded = true;
                        for (Entry<String, ZoneCommandExecutor> entry : executors.entrySet()) {
                            ZoneCommandExecutor executor = entry.getValue();
                            if (!executor.hasNoCommandLeft()) {
                                allEnded = false;
                                break;
                            }
                        }
                        if (allEnded) {
                            active = false;
                        }
                    }
                } else {
                    if (firstPass) {
                        LOGGER.info("As no zone has been specified to work on, we wait a bit to be sure that every zone has been discovered on the network ...");
                        firstPass = false;
                    }
                }
            }
        }
    }
}