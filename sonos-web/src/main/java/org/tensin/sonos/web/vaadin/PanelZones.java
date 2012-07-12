package org.tensin.sonos.web.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.web.SonosState;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

/**
 * The Class PanelZones.
 */
public class PanelZones extends Panel {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelZones.class);

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /** The contact list. */
    private final Table zonesList = new Table();

    /**
     * Instantiates a new panel zones.
     */
    public PanelZones() {
        super("Zones");
        setSizeFull();
    }

    /**
     * Inits the address list.
     * 
     * @param panelPlaylist
     *            the panel playlist
     */
    public void init(final PanelPlaylist panelPlaylist) {
        zonesList.setWidth("100%");
        zonesList.setPageLength(0);
        zonesList.setHeight("80%");
        zonesList.setSelectable(true);
        zonesList.setImmediate(true);
        // zonesList.setSizeFull();

        Object[] properties = { "Name" };
        boolean[] ordering = { true };
        zonesList.sort(properties, ordering);

        zonesList.setContainerDataSource(SonosState.getInstance().getZonesData());
        zonesList.setVisibleColumns(new String[] { "Name" });
        zonesList.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                Object id = zonesList.getValue();
                if (id != null) {
                    synchronized (SonosState.getInstance().getZonesData()) {
                        Item s = SonosState.getInstance().getZonesData().getItem(id);
                        String zoneName = s.toString();
                        LOGGER.info("Zone selected [" + id + "], zone [" + zoneName + "]");
                        SonosState.getInstance().setSelectedZone(zoneName);

                        panelPlaylist.fireEventZoneChanged();

                        // ICommand command = CommandFactory.createCommand("PLAY");
                        // ZoneCommandDispatcher.getInstance().dispatchCommand((IZoneCommand) command, s.toString());
                    }
                }

                // contactEditor.setItemDataSource(id == null ? null : zonesList.getItem(id));
                // contactRemovalButton.setVisible(id != null);
            }
        });
        addComponent(zonesList);
    }
}
