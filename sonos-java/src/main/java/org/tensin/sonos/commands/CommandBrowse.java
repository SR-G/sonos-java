package org.tensin.sonos.commands;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.BrowseHandle;
import org.tensin.sonos.control.EntryCallback;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.model.Entry;

/**
 * The Class CommandList.
 */
public class CommandBrowse extends AbstractCommand implements IZoneCommand {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBrowse.class);

    /** The callback. */
    private EntryCallback callback = new EntryCallback() {

        /** The sb. */
        private final StringBuilder sb = new StringBuilder();

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#addEntries(org.tensin.sonos.control.BrowseHandle, java.util.Collection)
         */
        @Override
        public void addEntries(final BrowseHandle handle, final Collection<Entry> entries) {
            for (final Entry entry : entries) {
                sb.append(" - ").append(entry.getTitle()).append(SonosConstants.NEWLINE);
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#retrievalComplete(org.tensin.sonos.control.BrowseHandle, boolean)
         */
        @Override
        public void retrievalComplete(final BrowseHandle handle, final boolean completedSuccessfully) {
            LOGGER.info(sb.toString());
            synchronized (handle) {
                handle.notify();
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#updateCount(org.tensin.sonos.control.BrowseHandle, int)
         */
        @Override
        public void updateCount(final BrowseHandle handle, final int count) {
        }

    };

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        final BrowseHandle handle = sonos.getMediaServerDevice().getContentDirectoryService().getAllEntriesAsync(callback, getArgs().get(0));
        try {
            synchronized (handle) {
                handle.wait();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Gets the callback.
     * 
     * @return the callback
     */
    public EntryCallback getCallback() {
        return callback;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "Browse the sonos informations";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "browse";
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

    /**
     * Sets the callback.
     * 
     * @param callback
     *            the new callback
     */
    public void setCallback(final EntryCallback callback) {
        this.callback = callback;
    }

}
