package org.tensin.sonos.commands;

/**
 * The Class CommandStop.
 */
public class CommandStop extends CommandPause implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.CommandPause#getName()
     */
    @Override
    public String getName() {
        return "stop";
    }

}
