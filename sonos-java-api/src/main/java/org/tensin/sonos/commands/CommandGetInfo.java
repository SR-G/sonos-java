package org.tensin.sonos.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.RemoteDeviceHelper;

/**
 * The Class CommandGetXPort.
 */
public class CommandGetInfo extends AbstractCommand implements IZoneCommand {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        String x = RemoteDeviceHelper.dumpRemoteDevice(sonos.getRootDevice());
        LOGGER.info(x);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "getinfo";
    }

}
