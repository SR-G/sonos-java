package org.tensin.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.tensin.sonos.upnp.SonosException;

/**
 * Lancement Jetty. Dépendances Maven =
 * 
 * <pre>
 *                 <dependency>
 *                         <groupId>org.eclipse.jetty</groupId>
 *                         <artifactId>jetty-server</artifactId>
 *                 </dependency>
 *                 <dependency>
 *                         <groupId>org.eclipse.jetty</groupId>
 *                         <artifactId>jetty-webapp</artifactId>
 *                 </dependency>
 *                 <dependency>
 *                         <groupId>org.eclipse.jetty</groupId>
 *                         <artifactId>jetty-jsp-2.1</artifactId>
 *                 </dependency>
 *                 <dependency>
 *                         <groupId>org.eclipse.jetty</groupId>
 *                         <artifactId>jetty-servlet</artifactId>
 *                 </dependency>
 *                 <dependency>
 *                         <groupId>org.apache.tomcat</groupId>
 *                         <artifactId>jasper</artifactId>
 *                 </dependency>
 * 
 * </pre>
 * 
 * @author u248663
 * @version $Revision: 1.16 $
 * @since 25 nov. 2010 15:36:32
 */
public class EmbeddedJetty {

    /**
     * Thread d'arrêt de Jetty (en externe).
     * 
     * @author u248663
     * @version $Revision: 1.16 $
     * @since 10 févr. 2011 20:23:35
     * 
     */
    private static class MonitorThread extends Thread {

        /** socket. */
        private ServerSocket socket;

        /**
         * Constructor.
         */
        public MonitorThread() {
            setDaemon(true);
            setName("THREAD-JETTY-MONITOR");
            try {
                socket = new ServerSocket(8079, 1, InetAddress.getByName(DEFAULT_LOCALHOST));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Run.
         * 
         * {@inheritDoc}
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            Socket accept = null;
            try {
                accept = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                reader.readLine();
                log.info("Stopping jetty embedded server");
                JETTY.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    accept.close();
                    socket.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }

    /** LOCALHOST. */
    public static final String DEFAULT_LOCALHOST = "127.0.0.1";

    /** The Constant DEFAULT_PORT. */
    public static final int DEFAULT_PORT = 8080;

    /**
     * Méthode de test.
     * 
     * @param args
     *            the arguments
     * @throws Exception
     *             the exception
     */
    public static void main(final String[] args) throws Exception {
        EmbeddedJetty j = new EmbeddedJetty();
        j.start("/", ".");
    }

    /**
     * Méthode d'arrêt d'un serveur déjà démarré.
     * 
     * @param port
     *            the port
     * @throws SonosException
     *             the pyramide exception
     */
    public static void stopRunningJetty(final int port) throws SonosException {
        OutputStream out = null;
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName(DEFAULT_LOCALHOST), port);
            out = s.getOutputStream();
            out.write(("\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** console. */
    protected final PrintStream standardConsoleOut = System.out;

    /** standardConsoleIn. */
    protected final PrintStream standardConsoleErr = System.err;

    /** initLogger. */
    private boolean initLogger;

    /** JETTY. */
    private static final Server JETTY = new Server();

    /** monitor. */
    private static Thread monitor;

    /** Logger. */
    private static final Log log = LogFactory.getLog(EmbeddedJetty.class);

    /**
     * Méthode initLogger. void
     */
    public void initLogger() {
        if (!initLogger) {
            setLoggerLevel("org.mortbay", Level.INFO);
            setLoggerLevel("org.eclipse.jetty", Level.INFO);
            setLoggerLevel("org.tomcat", Level.INFO);
            initLogger = true;
        }
    }

    /**
     * Method.
     * 
     * @param temp
     *            the temp
     */
    private void purgeJettyCache(final File temp) {
        if ((temp != null) && temp.exists() && temp.isDirectory()) {
            log.info("Purge du répertoire temporaire JETTY [" + temp.getAbsolutePath() + "]");
            temp.delete();
        }
    }

    /**
     * Restore I/O.
     */
    protected void restoreIO() {
        System.setOut(standardConsoleOut);
        System.setErr(standardConsoleErr);
    }

    /**
     * Méthode setLoggerLevel.
     * 
     * @param module
     *            the module
     * @param level
     *            void
     */
    public void setLoggerLevel(final String module, final Level level) {
        String moduleRenamed = module.replaceAll("/", ".");
        Logger logger = Logger.getLogger(moduleRenamed);
        if (logger != null) {
            logger.setLevel(level);
        }
    }

    /**
     * Méthode start.
     * 
     * @param webContentRelativePath
     *            the web content relative path
     * @param webContext
     *            the web context
     * @throws SonosException
     *             Erreur technique
     * @throws IOException
     * @throws MalformedURLException
     */
    public void start(final String webContentRelativePath, final String webContext) throws SonosException {
        start(webContentRelativePath, webContext, null);
    }

    /**
     * Method.
     * 
     * @param webContentRelativePath
     * @param webContext
     * @param resourcesPath
     * @throws SonosException
     * @throws MalformedURLException
     * @throws IOException
     */
    public void start(final String webContentRelativePath, final String webContext, final Collection<String> resourcesPath) throws SonosException {
        initLogger();
        try {

            /*
             * SelectChannelConnector connector = new SelectChannelConnector ();
             * connector.setPort (8080);
             */
            // We explicitly use the SocketConnector because the
            // SelectChannelConnector locks files
            Connector connector = new SocketConnector();
            connector.setPort(DEFAULT_PORT);
            connector.setMaxIdleTime(1000 * 60 * 60 * 4); // 4 heures

            WebAppContext app = new WebAppContext();
            app.setServer(JETTY);
            app.setWar(webContentRelativePath);
            app.setContextPath(webContext);
            app.setCompactPath(true);
            if (CollectionUtils.isNotEmpty(resourcesPath)) {
                ResourceCollection resourceCollection = new ResourceCollection();
                for (String url : resourcesPath) {
                    resourceCollection.addPath(url);
                }
                app.setBaseResource(resourceCollection);
            }

            /*
             * app.setDescriptor(webapp+"/WEB-INF/web.xml");
             * app.setResourceBase("../test-jetty-webapp/src/main/webapp");
             */

            /*
             * app.setResourceBase(new
             * ClassPathResource("webapp").getURI().toString());
             */

            // Avoid the taglib configuration because its a PITA if you don't have a net connection
            app.setConfigurationClasses(new String[] { WebInfConfiguration.class.getName(), WebXmlConfiguration.class.getName() });
            app.setParentLoaderPriority(true);

            JETTY.setConnectors(new Connector[] { connector });
            JETTY.setHandler(app);
            JETTY.setAttribute("org.mortbay.jetty.Request.maxFormContentSize", 0);
            // JETTY.setStopAtShutdown(true);

            startJetty();
            purgeJettyCache(app.getTempDirectory());
        } catch (MalformedURLException e) {
            throw new SonosException(e);
        } catch (IOException e) {
            throw new SonosException(e);
        }
    }

    /**
     * Method.
     * 
     * @throws SonosException
     *             the pyramide exception
     */
    private void startJetty() throws SonosException {
        // ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        // ByteArrayOutputStream bytesErr = new ByteArrayOutputStream();
        try {
            // System.setOut(new PrintStream(bytesOut));
            // System.setErr(new PrintStream(bytesErr));

            monitor = new MonitorThread();
            monitor.start();

            JETTY.start();
        } catch (Exception e) {
            throw new SonosException(e);
        } finally {
            /*
             * restoreIO(); if (!StringUtils.isEmpty(bytesOut.toString())) {
             * log.info(bytesOut.toString().trim()); } if
             * (!StringUtils.isEmpty(bytesErr.toString())) {
             * log.error(bytesErr.toString().trim()); }
             */
        }
    }

    /**
     * Méthode stop.
     * 
     * @throws SonosException
     *             Erreur technique
     */
    public void stop() throws SonosException {
        try {
            if (monitor != null) {
                monitor.interrupt();
            }
            JETTY.stop();
        } catch (Exception e) {
            throw new SonosException(e);
        }
    }
}
