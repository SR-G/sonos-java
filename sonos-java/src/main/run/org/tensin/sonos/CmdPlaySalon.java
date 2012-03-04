package org.tensin.sonos;

import org.tensin.sonos.upnp.SonosException;

public class CmdPlaySalon {

    public static void main(final String[] args) throws SonosException {
        SonosCommander.main(new String[] { "--command", "play", "--zone", "salon" });
    }
}
