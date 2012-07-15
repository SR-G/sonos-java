package org.tensin.sonos.web.vaadin;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.commands.CommandBrowse;
import org.tensin.sonos.commands.CommandTrack;
import org.tensin.sonos.control.BrowseHandle;
import org.tensin.sonos.control.EntryCallback;
import org.tensin.sonos.model.Entry;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

/**
 * The Class PanelPlaylist.
 */
public class PanelPlaylist extends AbstractVaadinPanel {

    /** The callback. */
    private final class SonosBrowseCallback implements EntryCallback {

        /** The playlist data. */
        private IndexedContainer playlistData;

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#addEntries(org.tensin.sonos.control.BrowseHandle, java.util.Collection)
         */
        @Override
        public void addEntries(final BrowseHandle handle, final Collection<Entry> entries) {
            for (final Entry entry : entries) {
                synchronized (playlistData) {
                    Object id = playlistData.addItem();
                    playlistData.getContainerProperty(id, "Title").setValue(entry.getTitle());
                    /*
                     * sonos_mkcontainer("", "object.container", "Artists", "A:ARTIST");
                     * sonos_mkcontainer("", "object.container", "Albums", "A:ALBUM");
                     * sonos_mkcontainer("", "object.container", "Genres", "A:GENRE");
                     * sonos_mkcontainer("", "object.container", "Composers", "A:COMPOSER");
                     * sonos_mkcontainer("", "object.container", "Imported Playlists", "A:PLAYLISTS");
                     * sonos_mkcontainer("", "object.container", "Folders", "S:");
                     * sonos_mkcontainer("", "object.container", "Radio", "R:");
                     * sonos_mkcontainer("", "object.container", "Line In", "AI:");
                     * sonos_mkcontainer("", "object.container", "Playlists", "SQ:");
                     */

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
            playList.setContainerDataSource(playlistData);
            playList.setPageLength(playlistData.size());
        }

        /**
         * Sets the playlist data.
         * 
         * @param playlistData
         *            the new playlist data
         */
        public void setPlaylistData(final IndexedContainer playlistData) {
            this.playlistData = playlistData;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.EntryCallback#updateCount(org.tensin.sonos.control.BrowseHandle, int)
         */
        @Override
        public void updateCount(final BrowseHandle handle, final int count) {
        }

    }

    /** The callback. */
    private final SonosBrowseCallback callback = new SonosBrowseCallback();;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelPlaylist.class);

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /** The play list. */
    private final Table playList = new Table();

    /**
     * Instantiates a new panel playlist.
     */
    public PanelPlaylist() {
        super("Playlist");
        setSizeFull();
    }

    /**
     * Fire event zone changed.
     */
    public void fireEventZoneChanged() {
        playList.setContainerDataSource(null);
        sonosState.setPlaylistData(null);
        callback.setPlaylistData(sonosState.getPlaylistData());

        CommandBrowse command = new CommandBrowse();
        command.setCallback(callback);
        command.setArgs("Q:0");

        sonosState.sendCommand(command);
    }

    /**
     * Inits the.
     * 
     * @param panelNowPlaying
     */
    public void init(final PanelNowPlaying panelNowPlaying) {
        playList.setWidth("100%");
        playList.setPageLength(10);
        playList.setImmediate(true);
        playList.setSelectable(true);
        playList.setPageLength(0);

        playList.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                Object id = playList.getValue();
                if (id != null) {
                    synchronized (sonosState.getPlaylistData()) {
                        Item s = sonosState.getPlaylistData().getItem(id);
                        String titleName = s.toString();
                        LOGGER.info("Track selected [" + id + "], title [" + titleName + "]");

                        final CommandTrack command = new CommandTrack();
                        command.setArgs(String.valueOf(id));
                        sonosState.sendCommand(command);

                        panelNowPlaying.fireEventZoneChanged();

                        // sonosState.setSelectedZone(zoneName);

                        // ICommand command = CommandFactory.createCommand("PLAY");
                        // ZoneCommandDispatcher.getInstance().dispatchCommand((IZoneCommand) command, s.toString());
                    }
                }

                // contactEditor.setItemDataSource(id == null ? null : zonesList.getItem(id));
                // contactRemovalButton.setVisible(id != null);
            }
        });

        addComponent(playList);
    }
}
