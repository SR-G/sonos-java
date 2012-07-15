package org.tensin.sonos.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.commands.ZoneCommandDispatcher;

/**
 * The Class SonosEngineServlet.
 */
public class SonosEngineServlet extends HttpServlet {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneCommandDispatcher.class);

    /** serialVersionUID. */
    private static final long serialVersionUID = 4326937703373289862L;

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("Now stopping the Sonos Web Engine servlet");
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
    }
}
