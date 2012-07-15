package org.tensin.sonos.web.vaadin;

import java.util.Collection;

import org.tensin.sonos.control.BrowseHandle;
import org.tensin.sonos.control.EntryCallback;
import org.tensin.sonos.model.Entry;
import org.tensin.sonos.model.PositionInfo;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Embedded;

/**
 * The Class PanelNowPlaying.
 */
public class PanelNowPlaying extends AbstractVaadinPanel {

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
    private final class SonosNowPlayingCallback implements EntryCallback {

        @Override
        public void addEntries(final BrowseHandle handle, final Collection<Entry> entries) {
            for (final Entry entry : entries) {
                // String url = "http://" + item.host + ":" + SonosConstants.SONOS_DEFAULT_RPC_PORT;
                // if (!item.albumArtURI.toString().startsWith("/")) {
                // url += "/";
                // }
                // url += item.albumArtURI.toString();
                //
                final String url = entry.getAlbumArtUri();
                System.out.println(">>>>>>>>>" + url);

                Resource resource = new ExternalResource(url);
                image.setSource(resource);
                // sonosState.get
            }
        }

        @Override
        public void retrievalComplete(final BrowseHandle handle, final boolean completedSuccessfully) {
        }

        @Override
        public void updateCount(final BrowseHandle handle, final int count) {
        }
    }

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /** The now playing listener. */
    private final SonosNowPlayingCallback nowPlayingCallback = new SonosNowPlayingCallback();

    /** The image. */
    private Embedded image;

    /**
     * Instantiates a new panel now playing.
     */
    public PanelNowPlaying() {
        super("Now playing");
        setSizeFull();
    }

    /**
     * Fire event zone changed.
     */
    public void fireEventZoneChanged() {
        // playList.setContainerDataSource(null);
        // sonosState.setPlaylistData(null);
        // playlistListener.setPlaylistData(sonosState.getPlaylistData());

        // CommandBrowse command = new CommandBrowse();
        // command.setCallback(nowPlayingCallback);
        // command.setArgs("AI:");
        // sonosState.sendCommand(command);

        PositionInfo mediaInfo;
        mediaInfo = sonosState.getSelectedSonos().getMediaRendererDevice().getAvTransportService().getPositionInfo();
        String url = "http://" + sonosState.getSelectedSonos().getIP().getHostName() + ":" + sonosState.getSelectedSonos().getPort();
        final String albumArtURI = mediaInfo.getTrackMetaData().getAlbumArtUri();
        if (!albumArtURI.startsWith("/")) {
            url += "/";
        }
        url += albumArtURI;

        Resource resource = new ExternalResource(url);
        image.setSource(resource);
    }

    /**
     * Inits the.
     * 
     */
    public void init() {
        image = new Embedded("Artwork");
        image.setType(Embedded.TYPE_IMAGE);
        image.setImmediate(true);
        image.setWidth("100%");
        addComponent(image);
    }
}
