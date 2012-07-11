package org.tensin.sonos.web.vaadin;

import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * The Class HelpWindow.
 */
@SuppressWarnings("serial")
public class HelpWindow extends Window {

    /** The Constant HELP_HTML_SNIPPET. */
    private static final String HELP_HTML_SNIPPET = "This is " + "an application used to drive sonos boxes. Hopefully it doesn't need any real help.";

    /**
     * Instantiates a new help window.
     */
    public HelpWindow() {
        setCaption("SonosWeb help");
        addComponent(new Label(HELP_HTML_SNIPPET, Label.CONTENT_XHTML));
    }

}
