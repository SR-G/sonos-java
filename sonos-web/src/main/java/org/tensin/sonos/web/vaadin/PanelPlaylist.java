package org.tensin.sonos.web.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.commands.CommandList;
import org.tensin.sonos.commands.CommandTrack;
import org.tensin.sonos.upnp.ISonosBrowseListener;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.web.SonosState;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

/**
 * The Class PanelPlaylist.
 */
public class PanelPlaylist extends Panel {

    /**
     * The listener interface for receiving sonosPlaylist events.
     * The class that is interested in processing a sonosPlaylist
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addSonosPlaylistListener<code> method. When
     * the sonosPlaylist event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see SonosPlaylistEvent
     */
    private final class SonosPlaylistListener implements ISonosBrowseListener {

        /** The playlist data. */
        private IndexedContainer playlistData;

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
         * @see org.tensin.sonos.upnp.ISonosBrowseListener#updateDone(java.lang.String)
         */
        @Override
        public void updateDone(final String parent) {
            playList.setContainerDataSource(playlistData);
            playList.setPageLength(playlistData.size());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosBrowseListener#updateItem(java.lang.String, int, org.tensin.sonos.upnp.SonosItem)
         */
        @Override
        public void updateItem(final String parent, final int index, final SonosItem item) {
            synchronized (playlistData) {
                Object id = playlistData.addItem();
                playlistData.getContainerProperty(id, "Title").setValue(item.title);
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

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelPlaylist.class);

    /** The playlist listener. */
    private final SonosPlaylistListener playlistListener = new SonosPlaylistListener();

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /** The play list. */
    private final Table playList = new Table();

    private PanelNowPlaying panelNowPlaying;

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
        SonosState.getInstance().setPlaylistData(null);
        playlistListener.setPlaylistData(SonosState.getInstance().getPlaylistData());

        CommandList command = new CommandList();
        command.setListener(playlistListener);
        command.setArgs("Q:0");

        SonosState.getInstance().sendCommand(command);
        // SonosState.getInstance().getSelectedSonos().browse("Q:0", playlistListener);
    }

    /**
     * Inits the.
     * 
     * @param panelNowPlaying
     */
    public void init() {
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
                    synchronized (SonosState.getInstance().getPlaylistData()) {
                        Item s = SonosState.getInstance().getPlaylistData().getItem(id);
                        String titleName = s.toString();
                        LOGGER.info("Track selected [" + id + "], title [" + titleName + "]");

                        final CommandTrack command = new CommandTrack();
                        command.setArgs(String.valueOf(id));
                        SonosState.getInstance().sendCommand(command);

                        panelNowPlaying.fireEventZoneChanged();

                        // SonosState.getInstance().setSelectedZone(zoneName);

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
