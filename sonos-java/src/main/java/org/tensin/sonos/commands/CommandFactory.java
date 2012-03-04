package org.tensin.sonos.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.upnp.SonosException;

/**
 * A factory for creating Command objects.
 */
public class CommandFactory {

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(CommandFactory.class);

    /** The found commands. */
    private static Collection<ICommand> foundCommands;

    /**
     * Creates a new Command object.
     * 
     * @param commandName
     *            the command name
     * @return the i command
     */
    public static ICommand createCommand(final String commandName) {
        final Collection<ICommand> availableCommands = getAvailableCommands(ICommand.class);
        for (final ICommand command : availableCommands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Creates a new Command object.
     * Ex. command line : --command volume +5
     * First parameter is always the command, other ones are additionnal parameters
     * 
     * @param commandLine
     *            the command line
     * @return the collection< i command>
     */
    public static Collection<? extends ICommand> createCommandStack(final Collection<String> commands, final Class<? extends ICommand> clazz)
            throws SonosException {
        final Collection<ICommand> commandStack = new ArrayList<ICommand>();
        ICommand foundCommand;
        Iterator<String> itr = commands.iterator();
        while (itr.hasNext()) {
            String s = itr.next();
            foundCommand = createCommand(s);
            if (foundCommand != null) {
                if (clazz.isAssignableFrom(foundCommand.getClass())) {
                    commandStack.add(foundCommand);
                    itr.remove();
                }
            }
        }
        return commandStack;
    }

    /**
     * Gets the available commands.
     * 
     * @param clazz
     *            the clazz
     * @return the available commands
     */
    public static Collection<ICommand> getAvailableCommands(final Class<ICommand> clazz) {
        if (foundCommands == null) {
            foundCommands = new ArrayList<ICommand>();
            foundCommands.add(new CommandAdd());
            foundCommands.add(new CommandDiscover());
            foundCommands.add(new CommandGetXPort());
            foundCommands.add(new CommandList());
            foundCommands.add(new CommandMove());
            foundCommands.add(new CommandMuteOn());
            foundCommands.add(new CommandNext());
            foundCommands.add(new CommandPause());
            foundCommands.add(new CommandPlay());
            foundCommands.add(new CommandPrev());
            foundCommands.add(new CommandRemove());
            foundCommands.add(new CommandRemoveAll());
            foundCommands.add(new CommandSave());
            foundCommands.add(new CommandSetXPort());
            foundCommands.add(new CommandShuffleOn());
            foundCommands.add(new CommandTrack());
            foundCommands.add(new CommandMuteOff());
            foundCommands.add(new CommandShuffleOff());
            foundCommands.add(new CommandVolume());
            foundCommands.add(new CommandVolumeDown());
            foundCommands.add(new CommandVolumeUp());
        }
        return foundCommands;
    }

    /**
     * Extract additionnal parameters.
     * 
     * @param commandLine
     *            the command line
     * @return the string[]
     */
    // private static String[] extractAdditionnalParameters(
    // List<String> commandLine) {
    // if (commandLine.size() > 1 ) {
    // final String[] result = new String[commandLine.size() - 1];
    // for ( int i = 1; i < commandLine.size() ; i++) {
    // result[i-1] = commandLine.get(i);
    // }
    // }
    // return new String[] {};
    // }

}
