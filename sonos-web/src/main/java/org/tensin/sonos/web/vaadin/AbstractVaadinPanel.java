package org.tensin.sonos.web.vaadin;

import org.tensin.sonos.web.ISonosState;
import org.tensin.sonos.web.SonosState;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;

/**
 * The Class AbstractVaadinPanel.
 */
public class AbstractVaadinPanel extends Panel {

    /** serialVersionUID. */
    private static final long serialVersionUID = -1934086079566322387L;

    /** The sonos state. */
    protected ISonosState sonosState = SonosState.getInstance();

    /**
     * Instantiates a new abstract vaadin panel.
     */
    public AbstractVaadinPanel() {
        super();
    }

    /**
     * Instantiates a new abstract vaadin panel.
     * 
     * @param content
     *            the content
     */
    public AbstractVaadinPanel(final ComponentContainer content) {
        super(content);
    }

    /**
     * Instantiates a new abstract vaadin panel.
     * 
     * @param caption
     *            the caption
     */
    public AbstractVaadinPanel(final String caption) {
        super(caption);
    }

    /**
     * Instantiates a new abstract vaadin panel.
     * 
     * @param caption
     *            the caption
     * @param content
     *            the content
     */
    public AbstractVaadinPanel(final String caption, final ComponentContainer content) {
        super(caption, content);
    }

}
