package org.tensin.sonos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.common.EmbeddedJetty;

/**
 * The Class SonosJetty.
 * 
 * - Error Handling (404, ...) handled by Jetty
 * - Pages error handling by the ItsNat framework itself (if a /pages/something is not found)
 * - RedirectHandling from / to default pages /pages/sonos by Jetty (redirect handler)
 * 
 */
public class SonosJetty extends EmbeddedJetty {

    /**
     * The Class SonosErrorPageErrorHandler.
     */
    public static final class SonosErrorPageErrorHandler extends ErrorPageErrorHandler {

        /** The silent ressources pattern. */
        private static Collection<String> silentRessourcesPattern;

        /** Logger. */
        private static final Logger LOGGER = LoggerFactory.getLogger(SonosJetty.SonosErrorPageErrorHandler.class);

        /**
         * Gets the silent ressources patterns.
         * 
         * @return the silent ressources patterns
         */
        private static Collection<String> getSilentRessourcesPatterns() {
            if (silentRessourcesPattern == null) {
                silentRessourcesPattern = new ArrayList<String>();
                silentRessourcesPattern.add(".*\\.js");
                silentRessourcesPattern.add(".*\\.jsp");
                silentRessourcesPattern.add(".*\\.gif");
                silentRessourcesPattern.add(".*\\.jpg");
                silentRessourcesPattern.add(".*\\.jpeg");
                silentRessourcesPattern.add(".*\\.bmp");
                silentRessourcesPattern.add(".*\\.png");
                silentRessourcesPattern.add(".*\\.css");
                silentRessourcesPattern.add(".*\\.inc");
            }
            return silentRessourcesPattern;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jetty.servlet.ErrorPageErrorHandler#handle(java.lang.String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        @Override
        public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
                throws IOException {
            LOGGER.error("Unknown request [" + request.getRequestURI() + "]");
            if (!isSilentErrorRessources(request.getRequestURI())) {
                super.handle(target, baseRequest, request, response);
            }
        }

        /**
         * Checks if is silent error ressources.
         * 
         * @param requestURI
         *            the request uri
         * @return true, if is silent error ressources
         */
        private boolean isSilentErrorRessources(final String requestURI) {
            for (final String regexp : getSilentRessourcesPatterns()) {
                if (requestURI.matches(regexp)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.EmbeddedJetty#buildErrorHandler()
     */
    @Override
    protected ErrorHandler buildErrorHandler() {
        ErrorPageErrorHandler errorHandler = new SonosErrorPageErrorHandler();
        errorHandler.setShowStacks(true);
        errorHandler.addErrorPage(404, "/pages/error");
        errorHandler.addErrorPage(500, "/pages/error");
        return errorHandler;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.common.EmbeddedJetty#buildHandlers(org.eclipse.jetty.webapp.WebAppContext)
     */
    @Override
    protected Handler[] buildHandlers(final WebAppContext applicationHandler) {
        return super.buildHandlers(applicationHandler);

        // final RewriteHandler rewriteHandler = new RewriteHandler();
        // rewriteHandler.setRewriteRequestURI(true);
        // rewriteHandler.setRewritePathInfo(false);
        // rewriteHandler.setOriginalPathAttribute("requestedPath");

        /*
         * // This one work fine
         * final RedirectRegexRule redirect = new RedirectRegexRule();
         * redirect.setRegex("^/$");
         * redirect.setReplacement("/pages/sonos");
         * // redirect.setTerminating(false);
         * // redirect.setHandling(false);
         * rewriteHandler.addRule(redirect);
         */

        // final RewritePatternRule oldToNew = new RewritePatternRule();
        // oldToNew.setPattern("/");
        // oldToNew.setReplacement("/pages/sonos");
        // rewriteHandler.addRule(oldToNew);

        // RewriteRegexRule reverse = new RewriteRegexRule();
        // reverse.setRegex("/reverse/([^/]*)/(.*)");
        // reverse.setReplacement("/reverse/$2/$1");
        // rewriteHandler.addRule(reverse);

        // return new Handler[] { rewriteHandler, applicationHandler };
    }

}
