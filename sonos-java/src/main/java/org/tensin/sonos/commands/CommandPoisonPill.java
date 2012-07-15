package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

/**
 * The Class CommandPoisonPill.
 */
public class CommandPoisonPill implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        // Nothing to do, won't be executed
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Internal stop command. Not supposed to be used.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "internal-stop-command";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#needArgs()
     */
    @Override
    public boolean needArgs() {
        return false;
    }
}
