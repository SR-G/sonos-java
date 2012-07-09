package org.tensin.sonos.web;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.itsnat.core.CommMode;
import org.itsnat.core.ItsNatBoot;
import org.itsnat.core.ItsNatServletConfig;
import org.itsnat.core.UseGZip;
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.http.ItsNatHttpServlet;
import org.itsnat.core.tmpl.ItsNatDocumentTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.SonosWebConstants;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.Listener;
import org.tensin.sonos.web.pages.SonosLoadListener;

/**
 * The Class HttpServletWrapper.
 */
public abstract class SonosServletWrapper extends HttpServlet {

    /**
     * The listener interface for receiving zonesDiscovered events. The class
     * that is interested in processing a zonesDiscovered event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addZonesDiscoveredListener<code> method. When
     * the zonesDiscovered event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see ZonesDiscoveredEvent
     */
    class ZonesDiscoveredListener implements Listener {

        /** The debug. */
        private boolean debug;

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.Listener#found(java.lang.String)
         */
        @Override
        public void found(final String host) {
            try {
                ISonos sonos = SonosFactory.build(host);
                sonos.refreshZoneAttributes();
                String name = sonos.getZoneName();
                if (StringUtils.isNotEmpty(name)) {
                    LOGGER.info("New zone found [" + name + "]");
                    if (isDebug()) {
                        sonos.trace_io(true);
                        sonos.trace_reply(true);
                        sonos.trace_browse(true);
                    }
                    ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(sonos, name);
                }
            } catch (SonosException e) {
                LOGGER.error("Internal error while working on new found host [" + host + "]", e);
            }
        }

        /**
         * Checks if is debug.
         * 
         * @return true, if is debug
         */
        public boolean isDebug() {
            return debug;
        }

        /**
         * Sets the debug.
         * 
         * @param debug
         *            the new debug
         */
        public void setDebug(final boolean debug) {
            this.debug = debug;
        }
    }

    /** serialVersionUID. */
    private static final long serialVersionUID = -971635274831875112L;

    /** The its nat servlet. */
    protected ItsNatHttpServlet itsNatServlet;

    /** The template name. */
    private final String templateName = SonosWebConstants.DEFAULT_TEMPLATE_NAME;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosServletWrapper.class);

    /** The discover. */
    private IDiscover discover;

    /**
     * Instantiates a new http servlet wrapper.
     */
    public SonosServletWrapper() {
    }

    /**
     * Dump its nat configuration.
     * 
     * @param itsNatConfig
     *            the its nat config
     * @return the string
     */
    // private String dumpItsNatConfiguration(final ItsNatServletConfig itsNatConfig) {
    // return new ToStringBuilder(itsNatConfig, ToStringStyle.MULTI_LINE_STYLE).append("clientErrorMode", itsNatConfig.getClientErrorMode())
    // .append("commMode", itsNatConfig.getCommMode())
    // .append("defaultEncoding", itsNatConfig.getDefaultEncoding())
    // .append("defaultEncoding", itsNatConfig.get)
    // .append("defaultDateFormat", itsNatConfig.getDefaultDateFormat().toString()).toString();
    // }

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns the ItsNat servlet wrapping this servlet.
     * 
     * @return the ItsNat servlet.
     * @see ItsNatHttpServlet#getHttpServlet()
     */
    public ItsNatHttpServlet getItsNatHttpServlet() {
        return itsNatServlet;
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return the servlet info
     */
    @Override
    public String getServletInfo() {
        return "Sonos Servlet";
    }

    /**
     * Initializes the ItsNat servlet wrapping this servlet. Overload this method
     * to initialize the ItsNat servlet (setup properties, register templates etc).
     * 
     * @param config
     *            the config
     * @throws ServletException
     *             the servlet exception
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        try {
            itsNatServlet = (ItsNatHttpServlet) ItsNatBoot.get().createItsNatServlet(this);
            initSonosDiscovery();
            initConfiguration(itsNatServlet);
            initTemplates(itsNatServlet);
            initListeners(itsNatServlet);
        } catch (SonosException e) {
            LOGGER.error("Error while initializing servlet", e);
        }
    }

    /**
     * Inits the configuration.
     * 
     * @param itsNatServlet2
     *            the its nat servlet2
     */
    private void initConfiguration(final ItsNatHttpServlet itsNatServlet2) {
        final ItsNatServletConfig itsNatConfig = itsNatServlet.getItsNatServletConfig();
        itsNatConfig.setDebugMode(true);
        itsNatConfig.setFastLoadMode(true);
        itsNatConfig.setCommMode(CommMode.XHR_ASYNC_HOLD);
        itsNatConfig.setEventTimeout(-1);
        itsNatConfig.setOnLoadCacheStaticNodes("text/html", true);
        itsNatConfig.setOnLoadCacheStaticNodes("text/xml", false);
        itsNatConfig.setDefaultEncoding(SonosWebConstants.DEFAULT_ENCODING);
        itsNatConfig.setUseGZip(UseGZip.SCRIPT);
        itsNatConfig.setDefaultDateFormat(DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.US));
        itsNatConfig.setEventDispatcherMaxWait(0);
        itsNatConfig.setEventsEnabled(true);
        itsNatConfig.setScriptingEnabled(true);
        itsNatConfig.setAutoCleanEventListeners(true);
        itsNatConfig.setUseXHRSyncOnUnloadEvent(true);
        // ItsNatServletContext itsNatCtx = itsNatConfig.getItsNatServletContext();
        // itsNatCtx.setMaxOpenClientsByDocument(5);
        // itsNatCtx.setMaxOpenDocumentsBySession(10)
    }

    /**
     * Inits the listeners.
     * 
     * @param itsNatServlet2
     *            the its nat servlet2
     */
    private void initListeners(final ItsNatHttpServlet itsNatServlet2) {
        ItsNatServletRequestListener listener = new SonosServletRequestListener();
        itsNatServlet.addItsNatServletRequestListener(listener);
    }

    /**
     * Inits the sonos discovery.
     * 
     * @throws SonosException
     */
    private void initSonosDiscovery() throws SonosException {
        final ZonesDiscoveredListener zonesDiscoveredListener = new ZonesDiscoveredListener();
        zonesDiscoveredListener.setDebug(true);
        discover = DiscoverFactory.build(zonesDiscoveredListener);
        discover.launch();
    }

    /**
     * Inits the templates.
     * 
     * @param itsNatServlet2
     *            the its nat servlet2
     */
    private void initTemplates(final ItsNatHttpServlet itsNatServlet2) {
        String pathPrefix = getServletContext().getRealPath("/");
        pathPrefix += "/WEB-INF/templates/" + templateName + "/";

        final ItsNatDocumentTemplate docTemplate = itsNatServlet.registerItsNatDocumentTemplate(SonosWebConstants.DEFAULT_INDEX_PAGE, "text/html", pathPrefix
                + "index.xhtml");
        final ItsNatDocumentTemplate docTemplateError = itsNatServlet.registerItsNatDocumentTemplate(SonosWebConstants.DEFAULT_ERROR_PAGE, "text/html",
                pathPrefix + "error.xhtml");
        docTemplate.addItsNatServletRequestListener(new SonosLoadListener());
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods
     * and forwards them to the {@link ItsNatHttpServlet}.
     * 
     * @param request
     *            servlet request object.
     * @param response
     *            servlet response object.
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        getItsNatHttpServlet().processRequest(request, response);
    }
}
