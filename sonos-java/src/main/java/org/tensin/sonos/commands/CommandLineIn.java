package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CommandLineIn.
 */
public class CommandLineIn extends AbstractCommand implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ISonos sonos) throws SonosException {
        if (hasArgs()) {
            sonos.linein(getArgs().get(0));
            sonos.play();
            sonos.setMute(false);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "line";
    }
    /*
     * Action a = avtransportService.getAction("SetAVTransportURI");
     * a.setArgumentValue("InstanceID", "0");
     * a.setArgumentValue("CurrentURI", info.getEncodedUri());
     * a.setArgumentValue("CurrentURIMetaData", info.getURIMetadata());
     * a.postControlAction();
     * if (!isPlaying()) {
     * play();
     * }
     */
}
