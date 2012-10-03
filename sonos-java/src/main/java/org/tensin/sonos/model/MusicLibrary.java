/*
 * Copyright 2007 David Wheeler
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.ISonosIndexer;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosIndexer;
import org.tensin.sonos.control.BrowseHandle;
import org.tensin.sonos.control.EntryCallback;
import org.tensin.sonos.control.ZonePlayer;

/**
 * A table model for a list of entries. eg queue.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class MusicLibrary implements MusicLibraryModel {

    /**
     * The Class MusicLibraryEntryCallback.
     */
    public class MusicLibraryEntryCallback implements EntryCallback {

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#addEntries(org.tensin.sonos.control.BrowseHandle, java.util.Collection)
         */
        @Override
        public void addEntries(final BrowseHandle handle, final Collection<Entry> entries) {
            MusicLibrary.this.addEntries(entries);
            LOGGER.info("Indexing [" + entries.size() + "] entries");
            for (final Entry entry : entries) {
                try {
                    sonosIndexer.index(entry);
                } catch (final SonosException ex) {
                    LOGGER.error("Can't index element", ex);
                }
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#retrievalComplete(org.tensin.sonos.control.BrowseHandle, boolean)
         */
        @Override
        public void retrievalComplete(final BrowseHandle handle, final boolean completedSuccessfully) {
            if (handle == browser) {
                browser = null;
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#updateCount(org.tensin.sonos.control.BrowseHandle, int)
         */
        @Override
        public void updateCount(final BrowseHandle handle, final int count) {
            setReportedSize(count);
        }

    }

    /** The sonos indexer. */
    private ISonosIndexer sonosIndexer = SonosIndexer.getInstance();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicLibrary.class);

    /** The entries. */
    protected final List<Entry> entries = new ArrayList<Entry>();

    /** The reported size. */
    private int reportedSize;

    /** The listeners. */
    private final List<MusicLibraryListener> listeners = new ArrayList<MusicLibraryListener>();

    /** The browser. */
    private BrowseHandle browser;

    /**
     * Instantiates a new music library.
     * 
     * @param zone
     *            the zone
     */
    public MusicLibrary(final ZonePlayer zone) {
        this(zone, "A:");
    }

    /**
     * Instantiates a new music library.
     * 
     * @param zone
     *            the zone
     * @param entry
     *            the entry
     */
    public MusicLibrary(final ZonePlayer zone, final Entry entry) {
        String id;
        if (entry != null) {
            id = entry.getId();
        } else {
            id = "A:";
        }
        browser = loadEntries(zone, id);
    }

    /**
     * Instantiates a new music library.
     * 
     * @param zone
     *            the zone
     * @param type
     *            the type
     */
    public MusicLibrary(final ZonePlayer zone, final String type) {
        browser = loadEntries(zone, type);
    }

    /**
     * Adds the entries.
     * 
     * @param newEntries
     *            the new entries
     */
    protected void addEntries(final Collection<Entry> newEntries) {
        final int oldSize = entries.size();
        entries.addAll(newEntries);
        fireEntriesAdded(oldSize, entries.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final MusicLibraryListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (browser != null) {
            browser.cancel();
        }
        removeListeners();
    }

    /**
     * Fire entries added.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     */
    protected void fireEntriesAdded(final int start, final int end) {
        for (final MusicLibraryListener listener : listeners) {
            listener.entriesAdded(start, end);
        }
    }

    /**
     * Fire size changed.
     */
    protected void fireSizeChanged() {
        for (final MusicLibraryListener listener : listeners) {
            listener.sizeChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry getEntryAt(final int index) {
        return entries.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return Math.max(entries.size(), reportedSize);
    }

    /**
     * Gets the sonos indexer.
     * 
     * @return the sonos indexer
     */
    public ISonosIndexer getSonosIndexer() {
        return sonosIndexer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.model.MusicLibraryModel#hasEntryFor(int)
     */
    @Override
    public boolean hasEntryFor(final int index) {
        return index < entries.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.model.MusicLibraryModel#indexOf(org.tensin.sonos.model.Entry)
     */
    @Override
    public int indexOf(final Entry entry) {
        return entries.indexOf(entry);
    }

    /**
     * Begins the process of loading the entries for this library. This method is
     * called from the constructor, and thus overriding implementers cannot assume
     * existance of even final class fields
     * 
     * @param zone
     *            the zone
     * @param type
     *            the type
     * @return the handle for the search
     */
    protected BrowseHandle loadEntries(final ZonePlayer zone, final String type) {
        return zone.getMediaServerDevice().getContentDirectoryService().getAllEntriesAsync(new MusicLibraryEntryCallback(), type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final MusicLibraryListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListeners() {
        listeners.clear();
    }

    /**
     * Sets the reported size.
     * 
     * @param count
     *            the new reported size
     */
    protected void setReportedSize(final int count) {
        reportedSize = count;
        fireSizeChanged();
    }

    /**
     * Sets the sonos indexer.
     * 
     * @param sonosIndexer
     *            the new sonos indexer
     */
    public void setSonosIndexer(final ISonosIndexer sonosIndexer) {
        this.sonosIndexer = sonosIndexer;
    }

}
