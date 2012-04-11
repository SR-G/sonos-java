package org.tensin.sonos.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SonosServlet.
 */
public class SonosServlet extends SonosServletWrapper {

    /** serialVersionUID */
    private static final long serialVersionUID = 505039976568993474L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosServlet.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.SonosServletWrapper#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        LOGGER.info("Starting servlet");
    }
}
