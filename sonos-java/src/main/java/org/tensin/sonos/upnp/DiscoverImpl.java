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

// TODO: Auto-generated Javadoc
/**
 * The Class Discover.
 */
public class DiscoverImpl extends Thread implements IDiscover {

    /** The Constant SSDP_TIME_TO_LIVE. */
    private static final int SSDP_TIME_TO_LIVE = 200;

    /** The Constant SSDP_PORT. */
    public static final int DEFAULT_SSDP_PORT = 1900;

    /** The Constant SSDP_CONTROL_PORT. */
    public static final int SSDP_CONTROL_PORT = 8009;

    /** The Constant PACKET_SIZE. */
    protected static final int BUFFER_SIZE = 8192;

    /** The Constant SSDP_ADDR. */
    static final String SSDP_ADDR = "239.255.255.250";

    /** The socket. */
    private final Collection<MulticastSocket> ssdpSockets = new ArrayList<MulticastSocket>();

    /** The p location. */
    private Pattern pLocation;

    /** The active. */
    volatile boolean active;

    /** The callback. */
    private Listener callback;

    /** The group address. */
    private static InetSocketAddress groupAddress;

    /** The lock. */
    private final Object lock = new Object();

    /** The list. */
    private final HashMap<String, String> list = new HashMap<String, String>();;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverImpl.class);

    /** The discovered network interface. */
    private final Collection<NetworkInterface> discoveredNetworkInterfaces = new ArrayList<NetworkInterface>();

    /** The cb. */
    private Listener cb = null;

    private static final String HTTP_VERSION = "1.1";
    private static final String NL = "\r\n";
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
     */
    public DiscoverImpl(final Listener cb) {
        this.cb = cb;
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
            ssdpSocket.close();
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
     * Handle_notify.
     * 
     * @param p
     *            the p
     * @param ssdpSocket
     *            the ssdp socket
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void handle_notify(final DatagramPacket packet, final MulticastSocket ssdpSocket) throws IOException {
        ssdpSocket.receive(packet);
        receivePacket(packet);
    }

    /**
     * Handle socket.
     * 
     * @param network_interface
     *            the network_interface
     * @param local_address
     *            the local_address
     * @param socket
     *            the socket
     */
    private void handleSocket(final NetworkInterface network_interface, final InetAddress local_address, final DatagramSocket socket) {
        long successful_accepts = 0;
        long failed_accepts = 0;
        int port = socket.getLocalPort();
        while (true) {
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                successful_accepts++;
                receivePacket(packet);
            } catch (Throwable e) {
                failed_accepts++;
                LOGGER.info("SSDP: receive failed on port " + port, e);
                if ((failed_accepts > 100) && (successful_accepts == 0)) {
                    // LOGGER.logUnrepeatableAlertUsingResource(LGLogger.AT_ERROR, "Network.alert.acceptfail", new String[] { "" + port, "UDP" });
                    break;
                }
            }
        }
    }

    /**
     * Inits the.
     * 
     */
    void init() {
        setName("SONOS-THREAD-DISCOVER");
        active = true;
        callback = cb;
        pLocation = Pattern.compile("^LOCATION:\\s*http://(.*):1400/xml/device_description.xml$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
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

    private void receivePacket(final DatagramPacket packet) {
        String s = new String(packet.getData(), 0, packet.getLength());
        Matcher m = pLocation.matcher(s);
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
     * @param ssdpSocket
     *            the ssdp socket
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void send_query(final MulticastSocket ssdpSocket) throws IOException {
        // DatagramPacket p = new DatagramPacket(query.getBytes(), query.length(), groupAddress.getAddress(), DEFAULT_SSDP_PORT);
        // ssdpSocket.send(p);
        // ssdpSocket.send(p);
        // try {
        // Thread.sleep(100);
        // } catch (InterruptedException e) {
        // LOGGER.error("Error while querying network through UPNP", e);
        // }
        // ssdpSocket.send(p);

        for (final NetworkInterface networkInterface : discoveredNetworkInterfaces) {
            try {
                MulticastSocket mc_sock = new MulticastSocket(null);
                mc_sock.setReuseAddress(true);
                try {
                    mc_sock.setTimeToLive(SSDP_TIME_TO_LIVE);
                } catch (Throwable e) {
                }
                mc_sock.bind(new InetSocketAddress(SSDP_CONTROL_PORT));
                mc_sock.setNetworkInterface(networkInterface);
                DatagramPacket p = new DatagramPacket(query.getBytes(), query.length(), groupAddress.getAddress(), DEFAULT_SSDP_PORT);
                mc_sock.send(p);
                mc_sock.send(p);
                mc_sock.close();
            } catch (Throwable e) {
            }
        }
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
        final byte[] rxbuf = new byte[BUFFER_SIZE];
        final DatagramPacket p = new DatagramPacket(rxbuf, rxbuf.length);
        try {
            final MulticastSocket ssdpSocket = new MulticastSocket(DEFAULT_SSDP_PORT);

            for (final InetAddress inetAddress : getInetAddresses(networkInterface)) {
                // force IPv4 address
                if (inetAddress.isLoopbackAddress()) {
                    LOGGER.debug("Ignoring local address [" + inetAddress.getHostName() + "] / [" + inetAddress.getHostAddress() + "]");
                } else if (inetAddress instanceof Inet6Address) {
                    LOGGER.debug("Ignoring IPv6 address [" + inetAddress.getHostName() + "] / [" + inetAddress.getHostAddress() + "]");
                } else {
                    if (!discoveredNetworkInterfaces.contains(networkInterface)) {
                        discoveredNetworkInterfaces.add(networkInterface);
                        ssdpSocket.setInterface(inetAddress);

                        final InetSocketAddress addr = getInetSocketAddress();
                        ssdpSockets.add(ssdpSocket);
                        ssdpSocket.setReuseAddress(true);
                        try {
                            ssdpSocket.setTimeToLive(SSDP_TIME_TO_LIVE);
                        } catch (Throwable e) {
                            LOGGER.error("Unsupported setTimeToLive function", e);
                        }

                        ssdpSocket.setNetworkInterface(networkInterface);
                        ssdpSocket.setLoopbackMode(true);
                        ssdpSocket.joinGroup(addr, networkInterface);
                        Runtime.getRuntime().addShutdownHook(new Thread() {

                            /**
                             * {@inheritDoc}
                             * 
                             * @see java.lang.Thread#run()
                             */
                            @Override
                            public void run() {
                                try {
                                    if (!ssdpSocket.isClosed()) {
                                        LOGGER.info("Now disconnecting ssdpSocket from current group for networkInterface ["
                                                + networkInterface.getDisplayName() + "]");
                                        ssdpSocket.leaveGroup(groupAddress, networkInterface);
                                    }
                                } catch (Throwable e) {
                                    LOGGER.error("Can't leave group for ssdp socket", e);
                                }
                            }
                        });

                        final Thread handleThread = new Thread() {

                            /**
                             * {@inheritDoc}
                             * 
                             * @see java.lang.Thread#run()
                             */
                            @Override
                            public void run() {
                                try {
                                    while (active) {
                                        handle_notify(p, ssdpSocket);
                                    }
                                } catch (IOException ioex) {
                                    /* done causes an exception when it closes the socket */
                                    if (active) {
                                        LOGGER.error("IO Error", ioex);
                                    }
                                }
                            }
                        };
                        handleThread.setDaemon(true);
                        handleThread.start();

                        final DatagramSocket control_socket = new DatagramSocket(null);
                        control_socket.setReuseAddress(true);
                        control_socket.bind(new InetSocketAddress(inetAddress, SSDP_CONTROL_PORT));
                        new Thread() {
                            @Override
                            public void run() {
                                handleSocket(networkInterface, inetAddress, control_socket);
                            }
                        }.start();

                        LOGGER.info("Setting multicast network interface: " + networkInterface.getDisplayName());
                        LOGGER.info("Sending message from multicast socket on network interface: " + ssdpSocket.getNetworkInterface());
                        LOGGER.info("Multicast socket is on interface: " + ssdpSocket.getInterface());
                        LOGGER.info("Socket TTL: " + ssdpSocket.getTimeToLive());

                        send_query(ssdpSocket);
                    }
                }
            }

        } catch (IOException ioex) {
            LOGGER.error("Cannot create socket", ioex);
        }
    }
}