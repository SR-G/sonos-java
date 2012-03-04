package org.tensin.sonos;

import org.tensin.sonos.upnp.SonosException;

public class CmdPauseAll {

    public static void main(final String[] args) throws SonosException {
        SonosCommander.main(new String[] { "--command", "pause", "--zone", "salon,chambre,bureau" });
    }

}
