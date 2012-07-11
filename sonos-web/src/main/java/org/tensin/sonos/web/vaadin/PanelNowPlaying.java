package org.tensin.sonos.web.vaadin;

import com.vaadin.ui.Embedded;
import com.vaadin.ui.Panel;

public class PanelNowPlaying extends Panel {

    /** serialVersionUID */
    private static final long serialVersionUID = -8755064618320070798L;

    public PanelNowPlaying() {
        super("Now playing");
        setSizeFull();

        Embedded image = new Embedded("Artwork");
        addComponent(image);
    }

}
