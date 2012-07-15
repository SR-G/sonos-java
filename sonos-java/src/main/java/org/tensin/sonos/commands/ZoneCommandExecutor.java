package org.tensin.sonos.commands;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commander.CommandExecution;
import org.tensin.sonos.commander.CommandFuture;
import org.tensin.sonos.commander.ICommandFuture;
import org.tensin.sonos.commander.CLIController;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Class CommandExecutor.
 */
public class ZoneCommandExecutor extends Thread {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CLIController.class);

    /** The sonos zone locker. */
    private final Object sonosZoneLocker = new Object();

    /** The sonos zone. */
    private ZonePlayer sonosZone;

    /** The zone name. */
    private final String zoneName;

    /** The commands queue. */
    private final LinkedBlockingQueue<CommandExecution> commandsQueue = new LinkedBlockingQueue<CommandExecution>();

    /** The active. */
    private boolean active = true;

    /** The running command. */
    private boolean runningCommand;

    /** The semaphore. */
    private final Object queueSemaphore = new Object();

    /** The queue commands. */
    private final Object queueCommands = new Object();

    /** The executed commands count. */
    private int executedCommandsCount = 0;

    /**
     * Instantiates a new zone command executor.
     * 
     * @param zoneName
     *            the zone name
     */
    public ZoneCommandExecutor(final String zoneName) {
        super();
        this.zoneName = zoneName;
        setName("SONOS-THREAD-ZONE-" + zoneName);
    }

    /**
     * Adds the command.
     * 
     * @param command
     *            the command
     */
    public void addCommand(final IZoneCommand command) {
        synchronized (queueSemaphore) {
            final CommandFuture future = new CommandFuture();
            final CommandExecution commandExecution = new CommandExecution(command, future);
            commandsQueue.offer(commandExecution);
        }
    }

    /**
     * Execute command.
     * 
     * @param command
     *            the command
     */
    private void executeCommand(final IZoneCommand command) {
        try {
            LOGGER.info("Executing command [" + command.getName() + "] on zone [" + zoneName + "]");
            executedCommandsCount++;
            command.execute(sonosZone);
        } catch (SonosException e) {
            LOGGER.error("Error while executing command [" + command.getName() + "] on zone [" + zoneName + "]");
        }
    }

    /**
     * Gets the command left count.
     * 
     * @return the command left count
     */
    public int getCommandLeftCount() {
        return commandsQueue.size();
    }

    /**
     * Gets the executed commands count.
     * 
     * @return the executed commands count
     */
    public int getExecutedCommandsCount() {
        return executedCommandsCount;
    }

    /**
     * Gets the sonos zone.
     * 
     * @return the sonos zone
     */
    public ZonePlayer getSonosZone() {
        return sonosZone;
    }

    /**
     * Gets the zone name.
     * 
     * @return the zone name
     */
    public String getZoneName() {
        return zoneName;
    }

    /**
     * Halt.
     */
    public void halt() {
        active = false;
    }

    /**
     * Checks for no command left.
     * 
     * @return true, if successful
     */
    public boolean hasNoCommandLeft() {
        synchronized (queueSemaphore) {
            return commandsQueue.size() == 0;
        }
    }

    /**
     * Checks for no running command.
     * 
     * @return true, if successful
     */
    public boolean hasRunningCommand() {
        synchronized (queueCommands) {
            return runningCommand;
        }
    }

    /**
     * Register zone as available.
     * 
     * @param sonos
     *            the sonos
     */
    public void registerZoneAsAvailable(final ZonePlayer sonos) {
        synchronized (sonosZoneLocker) {
            sonosZone = sonos;
            sonosZoneLocker.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        try {
            synchronized (sonosZoneLocker) {
                if (sonosZone == null) {
                    sonosZoneLocker.wait();
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted", e);
        }
        while (active) {
            try {
                final CommandExecution commandExecution = commandsQueue.take();
                final IZoneCommand command = commandExecution.getCommand();
                final ICommandFuture future = commandExecution.getFuture();
                synchronized (queueCommands) {
                    try {
                        runningCommand = true;
                        if (command != null) {
                            if (command instanceof CommandPoisonPill) {
                                active = false;
                            } else {
                                executeCommand(command);
                            }
                        }
                    } finally {
                        runningCommand = false;
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }
    }
}
