package org.tensin.sonos.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.itsnat.core.ItsNatDocument;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.ItsNatServletResponse;
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosWebConstants;

/**
 * The listener interface for receiving sonosItsNatServletRequest events.
 * The class that is interested in processing a sonosItsNatServletRequest
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSonosItsNatServletRequestListener<code> method. When
 * the sonosItsNatServletRequest event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SonosItsNatServletRequestEvent
 */
public final class SonosServletRequestListener implements ItsNatServletRequestListener {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosServletRequestListener.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.itsnat.core.event.ItsNatServletRequestListener#processRequest(org.itsnat.core.ItsNatServletRequest, org.itsnat.core.ItsNatServletResponse)
     */
    @Override
    public void processRequest(final ItsNatServletRequest request, final ItsNatServletResponse response) {
        final ItsNatDocument itsNatDoc = request.getItsNatDocument();
        if (itsNatDoc != null) {
            processStandardRequest(request, itsNatDoc);
        } else {
            processUnknownRequest(request, response);
        }
    }

    /**
     * Process standard request.
     * 
     * @param itsNatDoc
     *            the its nat doc
     */
    private void processStandardRequest(final ItsNatServletRequest request, final ItsNatDocument itsNatDoc) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request.getServletRequest();
        String docName = itsNatDoc.getItsNatDocumentTemplate().getName();
        LOGGER.info("Loading page [" + docName + "], request [" + httpServletRequest.getRequestURL() + "]");
    }

    /**
     * Process unknown request.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    private void processUnknownRequest(final ItsNatServletRequest request, final ItsNatServletResponse response) {
        final ServletRequest servletRequest = request.getServletRequest();
        String docName = servletRequest.getParameter(SonosWebConstants.DOC_NAME);

        if (StringUtils.isEmpty(docName)) {
            docName = (String) servletRequest.getAttribute(SonosWebConstants.DOC_NAME);
        }

        if (StringUtils.isNotEmpty(docName)) {
            processUnknowPage(docName, request, response);
        } else {
            processURLToPrettyfy(docName, request, response);
        }
    }

    /**
     * Process unknow page.
     * 
     * @param docName
     *            the doc name
     * @param request
     *            the request
     * @param response
     *            the response
     */
    private void processUnknowPage(final String docName, final ItsNatServletRequest request, final ItsNatServletResponse response) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request.getServletRequest();
        final ServletResponse servletResponse = response.getServletResponse();
        LOGGER.error("Specified page/url not registered [" + docName + "], redirecting to error page");
        request.getServletRequest().setAttribute(SonosWebConstants.DOC_NAME, SonosWebConstants.DEFAULT_ERROR_PAGE);
        request.getItsNatServlet().processRequest(httpServletRequest, servletResponse);
    }

    /**
     * Process url to prettyfy.
     * 
     * @param docName
     *            the doc name
     * @param request
     *            the request
     * @param response
     *            the response
     */
    private void processURLToPrettyfy(String docName, final ItsNatServletRequest request, final ItsNatServletResponse response) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request.getServletRequest();
        final ServletResponse servletResponse = response.getServletResponse();

        // Pretty URL case
        String pathInfo = httpServletRequest.getPathInfo();
        if (pathInfo == null) {
            throw new RuntimeException("Unexpected URL");
        }
        docName = pathInfo.substring(1); // Removes '/'
        docName = docName.replace('/', '.');

        if (docName.endsWith(".")) {
            docName.substring(0, docName.length() - 1);
        }

        if (StringUtils.isEmpty(docName)) {
            docName = SonosWebConstants.DEFAULT_INDEX_PAGE;
        }

        // => "name.name"
        request.getServletRequest().setAttribute(SonosWebConstants.DOC_NAME, docName);
        request.getItsNatServlet().processRequest(httpServletRequest, servletResponse);
    }
}