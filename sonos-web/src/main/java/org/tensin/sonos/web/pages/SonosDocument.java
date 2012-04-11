package org.tensin.sonos.web.pages;

import org.itsnat.core.ItsNatDocument;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.event.ItsNatEvent;
import org.itsnat.core.html.ItsNatHTMLDocument;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;

/**
 * The Class SonosDocument.
 */
public class SonosDocument implements EventListener {

    /** The its nat doc. */
    protected ItsNatHTMLDocument itsNatDoc;

    /** The click elem1. */
    protected Element clickElem1;

    /** The click elem2. */
    protected Element clickElem2;

    /**
     * Instantiates a new core example document.
     * 
     * @param itsNatDoc
     *            the its nat doc
     */
    public SonosDocument(final ItsNatHTMLDocument itsNatDoc) {
        this.itsNatDoc = itsNatDoc;
        load();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.w3c.dom.events.EventListener#handleEvent(org.w3c.dom.events.Event)
     */
    @Override
    public void handleEvent(final Event evt) {
        EventTarget currTarget = evt.getCurrentTarget();
        if (currTarget == clickElem1) {
            removeClickable(clickElem1);
            setAsClickable(clickElem2);
        } else {
            setAsClickable(clickElem1);
            removeClickable(clickElem2);
        }

        ItsNatEvent itsNatEvt = (ItsNatEvent) evt;
        ItsNatServletRequest itsNatReq = itsNatEvt.getItsNatServletRequest();
        ItsNatDocument itsNatDoc = itsNatReq.getItsNatDocument();
        HTMLDocument doc = (HTMLDocument) itsNatDoc.getDocument();
        Element noteElem = doc.createElement("p");
        noteElem.appendChild(doc.createTextNode("Clicked " + ((Element) currTarget).getAttribute("id")));
        doc.getBody().appendChild(noteElem);

        for (final String zoneName : ZoneCommandDispatcher.getInstance().getZonesNames()) {
            Element zone = doc.createElement("p");
            noteElem.appendChild(doc.createTextNode("Zone : " + zoneName));
            doc.getBody().appendChild(zone);
        }

    }

    /**
     * Load.
     */
    public void load() {
        HTMLDocument doc = itsNatDoc.getHTMLDocument();
        clickElem1 = doc.getElementById("clickableId1");
        clickElem2 = doc.getElementById("clickableId2");

        clickElem1.setAttribute("style", "color:red;");
        Text text1 = (Text) clickElem1.getFirstChild();
        text1.setData("Click Me!");

        Text text2 = (Text) clickElem2.getFirstChild();
        text2.setData("Cannot be clicked");

        Element noteElem = doc.createElement("p");
        noteElem.appendChild(doc.createTextNode("Ready to receive clicks..."));
        doc.getBody().appendChild(noteElem);

        ((EventTarget) clickElem1).addEventListener("click", this, false);
    }

    /**
     * Removes the clickable.
     * 
     * @param elem
     *            the elem
     */
    public void removeClickable(final Element elem) {
        elem.removeAttribute("style");
        Text text = (Text) elem.getFirstChild();
        text.setData("Cannot be clicked");
        ((EventTarget) elem).removeEventListener("click", this, false);
    }

    /**
     * Sets the as clickable.
     * 
     * @param elem
     *            the new as clickable
     */
    public void setAsClickable(final Element elem) {
        elem.setAttribute("style", "color:red;");
        Text text = (Text) elem.getFirstChild();
        text.setData("Click Me!");
        ((EventTarget) elem).addEventListener("click", this, false);
    }
}
