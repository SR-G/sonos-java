package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CommandPoisonPill.
 */
public class CommandPoisonPill implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        // Nothing to do, won't be executed
    }

    @Override
    public String getName() {
        return "internal-stop-command";
    }
}
