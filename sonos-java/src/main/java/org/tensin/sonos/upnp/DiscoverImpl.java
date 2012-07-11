package org.tensin.sonos.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;

/**
 * The Class Discover.
 */
public class DiscoverImpl extends Thread implements IDiscover {

    /** The Constant SSDP_TIME_TO_LIVE. */
    private static final int SSDP_TIME_TO_LIVE = 32;

    /** The Constant SSDP_PORT. Can't be changed. */
    public static final int DEFAULT_SSDP_PORT = 1900;

    /** The Constant PACKET_SIZE. */
    private static final int BUFFER_SIZE = 8192;

    /** The Constant SSDP_ADDR. */
    private static final String SSDP_ADDR = "239.255.255.250";

    /** The socket. */
    private final Collection<MulticastSocket> ssdpSockets = new ArrayList<MulticastSocket>();

    /** The p location. */
    private Pattern patternLocation;

    /** The active. */
    volatile boolean active;

    /** The callback. */
    private ISonosZonesDiscoverListener callback;

    /** ssdpControlPort. */
    private int ssdpControlPort;

    /** The group address. */
    private static InetSocketAddress groupAddress;

    /** The lock. */
    private final Object lock = new Object();

    /** The list. */
    private final HashMap<String, String> list = new HashMap<String, String>();

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverImpl.class);

    /** The cb. */
    private ISonosZonesDiscoverListener cb = null;;

    /** HTTP_VERSION. */
    private static final String HTTP_VERSION = "1.1";

    /** NL. */
    private static final String NL = "\r\n";

    /** SSD_MX. */
    private static final String SSD_MX = "1";

    /** The Constant query. */
    private static final String query = "M-SEARCH * HTTP/" + HTTP_VERSION + NL + "HOST: " + SSDP_ADDR + ":" + DEFAULT_SSDP_PORT + NL + "MAN: \"ssdp:discover\""
            + NL + "MX: " + SSD_MX + NL + "ST: urn:schemas-upnp-org:service:AVTransport:1" + NL +
            // "ST: ssdp:all"+ NL +
            NL;

    /**
     * Gets the inet address.
     * 
     * @return the inet address
     * @throws SonosException
     *             the sonos exception
     */
    private static InetSocketAddress getInetSocketAddress() throws SonosException {
        if (groupAddress == null) {
            try {
                groupAddress = new InetSocketAddress(InetAddress.getByName(SSDP_ADDR), 0);
            } catch (UnknownHostException e) {
                throw new SonosException(e);
            }
        }
        return groupAddress;
    }

    /** The first_response. */
    boolean first_response = true;

    /**
     * Instantiates a new discover.
     */
    public DiscoverImpl() {
    }

    /**
     * Instantiates a new discover.
     * 
     * @param cb
     *            the cb
     * @param controlPort
     *            the control port
     */
    public DiscoverImpl(final ISonosZonesDiscoverListener cb, final Integer controlPort) {
        this.cb = cb;
        ssdpControlPort = controlPort.intValue();
    }

    /**
     * Creates the ssdp control socket.
     * 
     * @param inetAddress
     *            the inet address
     * @return the datagram socket
     * @throws SonosException
     *             the sonos exception
     */
    private DatagramSocket createSSDPControlSocket(final InetAddress inetAddress) throws SonosException {
        DatagramSocket controlSocket = null;
        try {
            controlSocket = new DatagramSocket(null);
            controlSocket.setReuseAddress(true);
            controlSocket.bind(new InetSocketAddress(inetAddress, ssdpControlPort));
        } catch (SocketException e) {
            throw new SonosException("Cannont create SSDP control socket on port [" + ssdpControlPort + "]", e);
        } finally {

        }
        return controlSocket;
    }

    /**
     * Creates the ssdp multicast socket.
     * 
     * @param networkInterface
     *            the network interface
     * @param inetAddress
     *            the inet address
     * @return the multicast socket
     * @throws SonosException
     *             the sonos exception
     */
    private MulticastSocket createSSDPMulticastSocket(final NetworkInterface networkInterface, final InetAddress inetAddress) throws SonosException {
        final InetSocketAddress addr = getInetSocketAddress();
        MulticastSocket ssdpSocket = null;
        try {
            ssdpSocket = new MulticastSocket(DEFAULT_SSDP_PORT);
            ssdpSockets.add(ssdpSocket);

            // Set up the socket configuration
            LOGGER.debug("Setting main SSDP socket configuration");
            ssdpSocket.setInterface(inetAddress);
            ssdpSocket.setReuseAddress(true);
            ssdpSocket.setNetworkInterface(networkInterface);
            ssdpSocket.setLoopbackMode(true);
            try {
                ssdpSocket.setTimeToLive(SSDP_TIME_TO_LIVE);
            } catch (Throwable e) {
                LOGGER.error("Unsupported setTimeToLive function", e);
            }
            ssdpSocket.joinGroup(addr, networkInterface);
        } catch (SocketException e) {
            throw new SonosException("Cannot create main SSDP socket on port [" + DEFAULT_SSDP_PORT + "]", e);
        } catch (IOException e) {
            throw new SonosException("Cannot create main SSDP socket on port [" + DEFAULT_SSDP_PORT + "]", e);
        } finally {

        }
        return ssdpSocket;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#done()
     */
    @Override
    public void done() {
        active = false;
        for (final MulticastSocket ssdpSocket : ssdpSockets) {
            if ((ssdpSocket != null) && !ssdpSocket.isClosed()) {
                ssdpSocket.close();
            }
        }
    }

    /**
     * Gets the inet addresses.
     * 
     * @param currentInterface
     *            the current interface
     * @return the inet addresses
     */
    private Collection<InetAddress> getInetAddresses(final NetworkInterface currentInterface) {
        final Collection<InetAddress> addresses = new ArrayList<InetAddress>();
        final Enumeration<InetAddress> ni_addresses = currentInterface.getInetAddresses();
        while (ni_addresses.hasMoreElements()) {
            final InetAddress currentNetworkAddress = ni_addresses.nextElement();
            addresses.add(currentNetworkAddress);
        }
        return addresses;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#getList()
     */
    @Override
    public String[] getList() {
        synchronized (lock) {
            Set<String> set = list.keySet();
            String[] out = new String[set.size()];
            int n = 0;
            for (String s : set) {
                out[n++] = s;
            }
            return out;
        }
    }

    /**
     * Gets the network interfaces.
     * 
     * @return the network interfaces
     * @throws SonosException
     *             the sonos exception
     */
    private Collection<NetworkInterface> getNetworkInterfaces() throws SonosException {
        Collection<NetworkInterface> interfaces = new ArrayList<NetworkInterface>();
        Enumeration<NetworkInterface> network_interfaces;
        try {
            network_interfaces = NetworkInterface.getNetworkInterfaces();
            while (network_interfaces.hasMoreElements()) {
                interfaces.add(network_interfaces.nextElement());
            }
        } catch (SocketException e) {
            throw new SonosException("Can't find network interfaces", e);
        }
        return interfaces;
    }

    /**
     * Gets the ssdp control port.
     * 
     * @return the ssdp control port
     */
    public int getSsdpControlPort() {
        return ssdpControlPort;
    }

    /**
     * Handle socket.
     * 
     * @param packet
     *            the packet
     * @param socket
     *            the socket
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void handleNotifyOnControlSocket(final DatagramPacket packet, final DatagramSocket socket) throws IOException {
        socket.receive(packet);
        receivePacket(packet);
    }

    /**
     * Handle_notify.
     * 
     * @param packet
     *            the packet
     * @param ssdpSocket
     *            the ssdp socket
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void handleNotifyOnSSDPSocket(final DatagramPacket packet, final MulticastSocket ssdpSocket) throws IOException {
        ssdpSocket.receive(packet);
        receivePacket(packet);
    }

    /**
     * Inits the.
     * 
     */
    void init() {
        setName("SONOS-THREAD-DISCOVER");
        active = true;
        callback = cb;
        patternLocation = Pattern.compile("^LOCATION:\\s*http://(.*):1400/xml/device_description.xml$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        start();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#launch()
     */
    @Override
    public void launch() {
        init();
    }

    /**
     * Receive packet.
     * 
     * @param packet
     *            the packet
     */
    private void receivePacket(final DatagramPacket packet) {
        String s = new String(packet.getData(), 0, packet.getLength());
        Matcher m = patternLocation.matcher(s);
        if (m.find(0)) {
            boolean notify = false;
            String a = m.group(1);
            synchronized (lock) {
                if (!list.containsKey(a)) {
                    list.put(a, a);
                    notify = true;
                }
            }
            if (notify && (callback != null)) {
                callback.found(a);
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#run()
     */
    @Override
    public void run() {
        try {
            Collection<NetworkInterface> interfaces = getNetworkInterfaces();
            startWorkingOnEveryInterface(interfaces);
        } catch (SonosException e) {
            LOGGER.error("Error while discovering Sonos UPNP devices through SSDP", e);
        }
        while (active) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                LOGGER.error("Error while running discovery", e);
            }
        }
    }

    /**
     * Send_query.
     * 
     * @param networkInterface
     *            the network interface
     * @param ssdpSocket
     *            the ssdp socket
     * @throws SonosException
     *             the sonos exception
     */
    void sendQuery(final NetworkInterface networkInterface, final MulticastSocket ssdpSocket) throws SonosException {
        MulticastSocket multicastSocket = null;
        try {
            LOGGER.info("Sending search query on network interface [" + networkInterface.getDisplayName() + "] (" + ssdpSocket.getInterface()
                    + ") with socket TTL [" + ssdpSocket.getTimeToLive() + "]");
            multicastSocket = new MulticastSocket(null);
            multicastSocket.setReuseAddress(true);
            try {
                multicastSocket.setTimeToLive(SSDP_TIME_TO_LIVE);
            } catch (Throwable e) {
            }
            multicastSocket.bind(new InetSocketAddress(ssdpControlPort));
            multicastSocket.setNetworkInterface(networkInterface);
            final DatagramPacket p = new DatagramPacket(query.getBytes(), query.length(), groupAddress.getAddress(), DEFAULT_SSDP_PORT);
            // double the sending
            multicastSocket.send(p);
            multicastSocket.send(p);
            // 3 sending maybe not needed ...
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {
            // LOGGER.error("Error while querying network through UPNP", e);
            // }
            // ssdpSocket.send(p);
        } catch (SocketException e) {
            throw new SonosException("Error while sending search query on interface [" + networkInterface.getDisplayName() + "], search query [" + query + "]");
        } catch (IOException e) {
            throw new SonosException("Error while sending search query on interface [" + networkInterface.getDisplayName() + "], search query [" + query + "]");
        } finally {
            if (!multicastSocket.isClosed()) {
                multicastSocket.close();
            }
        }
    }

    /**
     * Sets the ssdp control port.
     * 
     * @param ssdpControlPort
     *            the new ssdp control port
     */
    @Override
    public void setSsdpControlPort(final int ssdpControlPort) {
        this.ssdpControlPort = ssdpControlPort;
    }

    /**
     * Start working on every interface.
     * 
     * @param interfaces
     *            the interfaces
     * @throws SonosException
     *             the sonos exception
     */
    private void startWorkingOnEveryInterface(final Collection<NetworkInterface> interfaces) throws SonosException {
        for (final NetworkInterface currentInterface : interfaces) {
            LOGGER.debug("Now searching through interface [" + currentInterface.getName() + "]");
            startWorkingOnNetworkInterface(currentInterface);
        }
    }

    /**
     * Start working on inet address.
     * 
     * @param networkInterface
     *            the network interface
     * @throws SonosException
     *             the sonos exception
     */
    private void startWorkingOnNetworkInterface(final NetworkInterface networkInterface) throws SonosException {

        for (final InetAddress inetAddress : getInetAddresses(networkInterface)) {
            // force IPv4 address
            if (inetAddress.isLoopbackAddress()) {
                LOGGER.debug("Ignoring local address [" + inetAddress.getHostName() + "] / [" + inetAddress.getHostAddress() + "]");
            } else if (inetAddress instanceof Inet6Address) {
                LOGGER.debug("Ignoring IPv6 address [" + inetAddress.getHostName() + "] / [" + inetAddress.getHostAddress() + "]");
            } else {
                // Create main SSDP multicast socket
                final MulticastSocket ssdpSocket = createSSDPMulticastSocket(networkInterface, inetAddress);

                // Set up the shutdown hook for group leaving when the JVM ends
                LOGGER.debug("Registering shutdown hook for SSDP socket group leaving");
                Runtime.getRuntime().addShutdownHook(new Thread() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see java.lang.Thread#run()
                     */
                    @Override
                    public void run() {
                        setName("SONOS-THREAD-SSDP-DISCONNECTING");
                        try {
                            if (!ssdpSocket.isClosed()) {
                                LOGGER.info("Now disconnecting ssdpSocket from current group for networkInterface [" + networkInterface.getDisplayName() + "]");
                                ssdpSocket.leaveGroup(groupAddress, networkInterface);
                            }
                        } catch (Throwable e) {
                            LOGGER.error("Can't leave group for ssdp socket", e);
                        }
                    }
                });

                // Set up the main SSDP handle thread
                LOGGER.debug("Starting main SSDP handle thread on port [" + DEFAULT_SSDP_PORT + "]");
                final Thread handleThread = new Thread() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see java.lang.Thread#run()
                     */
                    @Override
                    public void run() {
                        setName("SONOS-THREAD-SSDP-HANDLE");
                        try {
                            while (active) {
                                final byte[] rxbuf = new byte[BUFFER_SIZE];
                                final DatagramPacket p = new DatagramPacket(rxbuf, rxbuf.length);
                                handleNotifyOnSSDPSocket(p, ssdpSocket);
                            }
                        } catch (IOException ioex) {
                            /* done causes an exception when it closes the socket */
                            if (active) {
                                LOGGER.error("IO Error on main ssdp handle thread on port [" + DEFAULT_SSDP_PORT + "]", ioex);
                            }
                        }
                    }
                };
                handleThread.setDaemon(true);
                handleThread.start();

                // Set up the control SSDP handle thread
                LOGGER.debug("Starting control handle thread on port [" + ssdpControlPort + "]");
                final DatagramSocket controlSocket = createSSDPControlSocket(inetAddress);
                final Thread controlThread = new Thread() {
                    @Override
                    public void run() {
                        setName("SONOS-THREAD-SSDP-CONTROL-HANDLE");
                        try {
                            while (active) {
                                final byte[] rxbuf = new byte[BUFFER_SIZE];
                                final DatagramPacket p = new DatagramPacket(rxbuf, rxbuf.length);
                                handleNotifyOnControlSocket(p, controlSocket);
                            }
                        } catch (IOException ioex) {
                            /* done causes an exception when it closes the socket */
                            if (active) {
                                LOGGER.error("IO Error on control handle thread on port [" + ssdpControlPort + "]", ioex);
                            }
                        }
                    }
                };
                controlThread.setDaemon(true);
                controlThread.start();

                sendQuery(networkInterface, ssdpSocket);
            }
        }
    }
}