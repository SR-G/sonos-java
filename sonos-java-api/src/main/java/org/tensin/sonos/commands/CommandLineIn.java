package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;

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
    public void execute(final ZonePlayer sonos) throws SonosException {
        if (hasArgs()) {
            // getArgs().get(0)
            // sonos.getAudioInService().startTransmissionToGroup(sonos.);
            sonos.getMediaRendererDevice().getRenderingControlService().setMute(false);
            sonos.getMediaRendererDevice().getAvTransportService().play();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Activate line-in and starts playing it";
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

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.AbstractCommand#needArgs()
     */
    @Override
    public boolean needArgs() {
        return true;
    }

}
