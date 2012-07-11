package org.tensin.sonos.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.ISonosZonesDiscoverListener;
import org.tensin.sonos.web.SonosState;

/**
 * The Class SonosEngineServlet.
 */
public class SonosEngineServlet extends HttpServlet {

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
    class ZonesDiscoveredListener implements ISonosZonesDiscoverListener {

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosZonesDiscoverListener#found(java.lang.String)
         */
        @Override
        public void found(final String host) {
            try {
                ISonos sonos = SonosFactory.build(host);
                sonos.refreshZoneAttributes();
                String name = sonos.getZoneName();

                if (StringUtils.isNotEmpty(name)) {
                    LOGGER.info("New zone found [" + name + "]");
                    ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(sonos, name);
                    synchronized (SonosState.getInstance().getZonesData()) {
                        Object id = SonosState.getInstance().getZonesData().addItem();
                        SonosState.getInstance().getZonesData().getContainerProperty(id, "Name").setValue(name);
                    }
                }
            } catch (SonosException e) {
                LOGGER.error("Internal error while working on new found host [" + host + "]", e);
            }
        }
    }

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(ZoneCommandDispatcher.class);

    /** serialVersionUID. */
    private static final long serialVersionUID = 4326937703373289862L;

    /** The discover. */
    private IDiscover discover;

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("Now stopping the Sonos Web Engine servlet");
        if (discover != null) {
            discover.done();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        LOGGER.info("Now starting the Sonos Web Engine servlet");
        try {
            initSonosCommander();
        } catch (SonosException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Inits the sonos commander.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    private void initSonosCommander() throws SonosException {
        LOGGER.info("Starting UPNP discovery");
        final ZonesDiscoveredListener zonesDiscoveredListener = new ZonesDiscoveredListener();
        discover = DiscoverFactory.build(zonesDiscoveredListener);
        discover.launch();
    }
}
