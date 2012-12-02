package org.tensin.sonos.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.helpers.TimeUtilities;

/**
 * The Class CommandDiscover.
 */
public class CommandDiscover implements IStandardCommand {

    /** The zone command dispatcher. */
    private final IZoneCommandDispatcher zoneCommandDispatcher = ZoneCommandDispatcher.getInstance();

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDiscover.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IStandardCommand#execute()
     */
    @Override
    public void execute() throws SonosException {
        TimeUtilities.waitMilliSeconds(SonosConstants.MAX_DISCOVER_TIME_IN_MILLISECONDS);
        final Map<String, ZoneCommandExecutor> executors = zoneCommandDispatcher.getExecutors();
        final int count = executors.size();
        for (final Entry<String, ZoneCommandExecutor> entry : executors.entrySet()) {
            LOGGER.info(" - Zone [" + entry.getKey() + "]");
        }
        LOGGER.info(count + " Sonos zones found on Network in " + SonosConstants.MAX_DISCOVER_TIME_IN_MILLISECONDS + "ms.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Discover every Sonos box on the network";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "discover";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#needArgs()
     */
    @Override
    public boolean needArgs() {
        return false;
    }
}
