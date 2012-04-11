package org.tensin.sonos.web.pages;

import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.ItsNatServletResponse;
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.html.ItsNatHTMLDocument;

/**
 * The listener interface for receiving coreExampleLoad events.
 * The class that is interested in processing a coreExampleLoad
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addCoreExampleLoadListener<code> method. When
 * the coreExampleLoad event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see CoreExampleLoadEvent
 */
public class SonosLoadListener implements ItsNatServletRequestListener {

    /**
     * Instantiates a new core example load listener.
     */
    public SonosLoadListener() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.itsnat.core.event.ItsNatServletRequestListener#processRequest(org.itsnat.core.ItsNatServletRequest, org.itsnat.core.ItsNatServletResponse)
     */
    @Override
    public void processRequest(final ItsNatServletRequest request, final ItsNatServletResponse response) {
        final ItsNatHTMLDocument itsNatDoc = (ItsNatHTMLDocument) request.getItsNatDocument();
        new SonosDocument(itsNatDoc);
    }

}
