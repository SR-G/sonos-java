package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandAdd extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.add(getArgs().get(0));
        }
    }

    @Override
    public String getName() {
        return "add";
    }

}
