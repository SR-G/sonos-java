package org.tensin.sonos;

import org.tensin.sonos.upnp.SonosException;

public class CmdGetXPort {

    public static void main(final String[] args) throws SonosException {
        SonosCommander.main(new String[] { "--command", "getxport", "--zone", "salon" });
    }
}
