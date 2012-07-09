package org.tensin.sonos.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;

/**
 * The Class CommandGetXPort.
 */
public class CommandGetXPort extends AbstractCommand implements IZoneCommand {

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(CommandGetXPort.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ISonos sonos) throws SonosException {
        String x = sonos.getTransportURI();
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
        return "getxport";
    }

}
