package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.EntryHelper;
import org.tensin.sonos.model.Entry;

/**
 * The Class CommandRemove.
 */
public class CommandRemove extends AbstractCommand implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        if (hasArgs()) {
            final Entry entry = EntryHelper.createEntryForUrl(getArgs().get(0));
            sonos.getMediaRendererDevice().getAvTransportService().removeTrackFromQueue(entry);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Remove the specified song number from the current playlist";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "remove";
    }

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
