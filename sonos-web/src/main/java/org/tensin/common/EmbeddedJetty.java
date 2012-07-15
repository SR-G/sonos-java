package org.tensin.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Level;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;

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
                LOGGER.info("Stopping jetty embedded server");
                JETTY.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    accept.close();
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("Error while closing server", e);
                }
            }
        }
    }

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedJetty.class);

    /** LOCALHOST. */
    public static final String DEFAULT_LOCALHOST = "127.0.0.1";

    /** The Constant DEFAULT_PORT. */
    public static final int DEFAULT_PORT = 8080;

    /** JETTY. */
    protected static final Server JETTY = new Server();

    /** monitor. */
    private static Thread monitor;

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

    /** port */
    private int port = DEFAULT_PORT;

    /** console. */
    protected final PrintStream standardConsoleOut = System.out;

    /** standardConsoleIn. */
    protected final PrintStream standardConsoleErr = System.err;

    /** initLogger. */
    private boolean initLogger;

    /**
     * Builds the error handler.
     * 
     * @return the object
     */
    protected ErrorHandler buildErrorHandler() {
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        return errorHandler;
    }

    /**
     * Builds the handlers.
     * 
     * @param applicationHandler
     *            the application handler
     * @return the handler[]
     */
    protected Handler[] buildHandlers(final WebAppContext applicationHandler) {
        return new Handler[] { applicationHandler };
    }

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

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
     * @see http://docs.codehaus.org/display/JETTY/Temporary+Directories
     * @param temp
     *            the temp
     * @throws SonosException
     */
    private void purgeJettyCache() throws SonosException {
        final File jettyWorkDir = new File("work/"); // Jetty stocke par défaut son cache .jsp, war, etc. dans ${jetty.home}/work si work/ existe http://docs.codehaus.org/display/JETTY/Temporary+Directories
        final String jettyPattern = ".*jetty-.*"; // ex. jetty-0.0.0.0-8080-web-_-any-
        // final IOFileFilter nameFilter = new RegexFileFilter(jettyPattern);
        // final Collection<File> tempFiles = FileUtils.listFiles(jettyWorkDir, TrueFileFilter.INSTANCE, nameFilter);

        final File[] tempFiles = jettyWorkDir.listFiles(new FileFilter() {

            /**
             * {@inheritDoc}
             * 
             * @see java.io.FileFilter#accept(java.io.File)
             */
            @Override
            public boolean accept(final File pathname) {
                return pathname.getName().matches(jettyPattern);
            }

        });

        if (ArrayUtils.isNotEmpty(tempFiles)) {
            for (final File tempFile : tempFiles) {
                try {
                    FileUtils.deleteDirectory(tempFile);
                } catch (final IOException e) {
                    throw new SonosException("Can't purge temporary Jetty directory [" + tempFile.getAbsolutePath() + "]", e);
                }
            }
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
        final String moduleRenamed = module.replaceAll("/", ".");
        // TODO
        // final Logger logger = Logger.getLogger(moduleRenamed);
        // if (logger != null) {
        // logger.setLevel(level);
        // }
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(final int port) {
        this.port = port;
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
            // We explicitly use the SocketConnector because the
            // SelectChannelConnector locks files
            final Connector connector = new SelectChannelConnector();
            connector.setPort(getPort());
            connector.setMaxIdleTime(1000 * 60 * 60 * 4); // 4 heures

            final WebAppContext applicationHandler = new WebAppContext();
            applicationHandler.setServer(JETTY);
            applicationHandler.setWar(webContentRelativePath);
            applicationHandler.setContextPath(webContext);
            applicationHandler.setCompactPath(true);
            if (CollectionUtils.isNotEmpty(resourcesPath)) {
                ResourceCollection resourceCollection = new ResourceCollection();
                for (String url : resourcesPath) {
                    resourceCollection.addPath(url);
                }
                applicationHandler.setBaseResource(resourceCollection);
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
            applicationHandler.setConfigurationClasses(new String[] { WebInfConfiguration.class.getName(), WebXmlConfiguration.class.getName() });
            applicationHandler.setParentLoaderPriority(true);
            applicationHandler.setErrorHandler(buildErrorHandler());

            JETTY.setConnectors(new Connector[] { connector });

            final HandlerCollection handlers = new HandlerCollection();
            handlers.setHandlers(buildHandlers(applicationHandler));
            JETTY.setHandler(handlers);

            JETTY.setAttribute("org.mortbay.jetty.Request.maxFormContentSize", 0);
            // purgeJettyCache();
            startJetty();
        } catch (MalformedURLException e) {
            throw new SonosException(e);
        } catch (IOException e) {
            throw new SonosException(e);
        }
        LOGGER.info("Jetty internal web server started on [http://" + DEFAULT_LOCALHOST + ":" + getPort() + "]");
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
