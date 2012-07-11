package org.tensin.sonos.commander;

import org.tensin.sonos.commands.IZoneCommand;

/**
 * The Class CommandExecution.
 */
public class CommandExecution {

    /** The command. */
    private IZoneCommand command;

    /** The future. */
    private ICommandFuture future;

    /**
     * Instantiates a new command execution.
     * 
     * @param command
     *            the command
     * @param future
     *            the future
     */
    public CommandExecution(final IZoneCommand command, final ICommandFuture future) {
        super();
        this.command = command;
        this.future = future;
    }

    /**
     * Gets the command.
     * 
     * @return the command
     */
    public IZoneCommand getCommand() {
        return command;
    }

    /**
     * Gets the future.
     * 
     * @return the future
     */
    public ICommandFuture getFuture() {
        return future;
    }

    /**
     * Sets the command.
     * 
     * @param command
     *            the new command
     */
    public void setCommand(final IZoneCommand command) {
        this.command = command;
    }

    /**
     * Sets the future.
     * 
     * @param future
     *            the new future
     */
    public void setFuture(final ICommandFuture future) {
        this.future = future;
    }

}
