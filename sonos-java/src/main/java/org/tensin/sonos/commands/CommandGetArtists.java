package org.tensin.sonos.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.BrowseHandle;
import org.tensin.sonos.control.EntryCallback;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.model.Entry;

/**
 * The Class CommandGetXPort.
 */
public class CommandGetArtists extends AbstractCommand implements IZoneCommand {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGetArtists.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        final long start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder();
        final List<Entry> allEntries = new ArrayList<Entry>();

        final BrowseHandle handle = sonos.getMediaServerDevice().getContentDirectoryService().getAllEntriesAsync(new EntryCallback() {

            @Override
            public void addEntries(final BrowseHandle handle, final Collection<Entry> entries) {
                allEntries.addAll(entries);
            }

            @Override
            public void retrievalComplete(final BrowseHandle handle, final boolean completedSuccessfully) {
                Collections.sort(allEntries);
                for (final Entry entry : allEntries) {
                    sb.append(" - ").append(entry.getTitle()).append(SonosConstants.NEWLINE);
                }
                LOGGER.info(sb.toString());
                System.out.println((System.currentTimeMillis() - start) + "ms");
            }

            @Override
            public void updateCount(final BrowseHandle handle, final int count) {
                // TODO Auto-generated method stub

            }

        }, "A:ARTIST");
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
        return "getartists";
    }

}
