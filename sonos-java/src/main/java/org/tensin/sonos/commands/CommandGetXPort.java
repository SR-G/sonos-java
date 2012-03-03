package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

public class CommandGetXPort extends AbstractCommand implements IZoneCommand {

    @Override
    public void execute(final ISonos sonos) throws SonosException {
        String x = sonos.getTransportURI();
        System.out.println(x);
    }

    @Override
    public String getName() {
        return "getxport";
    }

}
