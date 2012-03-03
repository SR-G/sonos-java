package org.tensin.sonos.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Discover.
 */
public class DiscoverImpl extends Thread implements IDiscover {

    /**
     * The Interface Listener.
     */
    public static interface Listener {

        /**
         * Found.
         * 
         * @param host
         *            the host
         */
        public void found(String host);
    }

    /** The Constant SSDP_PORT. */
    static final int SSDP_PORT = 1900;

    /** The Constant SSDP_ADDR. */
    static final String SSDP_ADDR = "239.255.255.250";

    /** The query. */
    static String query = "M-SEARCH * HTTP/1.1\r\n" + "HOST: " + SSDP_ADDR + ":" + SSDP_PORT + "\r\n" + "MAN: \"ssdp:discover\"\r\n" + "MX: 1\r\n"
            + "ST: urn:schemas-upnp-org:service:AVTransport:1\r\n" +
            // "ST: ssdp:all\r\n"+
            "\r\n";

    /** The addr. */
    InetAddress addr;

    /** The socket. */
    MulticastSocket socket;

    /** The p location. */
    Pattern pLocation;

    /** The active. */
    volatile boolean active;

    /** The callback. */
    DiscoverImpl.Listener callback;

    /** The lock. */
    Object lock;

    /** The list. */
    HashMap<String, String> list;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverImpl.class);

    /**
     * Instantiates a new discover.
     */
    public DiscoverImpl() {
        init(null);
    }

    /**
     * Instantiates a new discover.
     * 
     * @param cb
     *            the cb
     */
    public DiscoverImpl(final DiscoverImpl.Listener cb) {
        init(cb);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#done()
     */
    @Override
    public void done() {
        active = false;
        socket.close();
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
     * Handle_notify.
     * 
     * @param p
     *            the p
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void handle_notify(final DatagramPacket p) throws IOException {
        socket.receive(p);
        String s = new String(p.getData(), 0, p.getLength());
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
     * Inits the.
     * 
     * @param cb
     *            the cb
     */
    void init(final DiscoverImpl.Listener cb) {
        setName("SONOS-THREAD-DISCOVER");
        active = true;
        callback = cb;
        pLocation = Pattern.compile("^LOCATION:\\s*http://(.*):1400/xml/device_description.xml$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        start();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.upnp.IDiscover#run()
     */
    @Override
    public void run() {
        DatagramPacket p = new DatagramPacket(new byte[1540], 1540);
        list = new HashMap<String, String>();
        lock = new Object();
        try {
            addr = InetAddress.getByName(SSDP_ADDR);
            socket = new MulticastSocket(SSDP_PORT);
            socket.joinGroup(addr);
            send_query();
        } catch (IOException ioex) {
            LOGGER.error("Cannot create socket", ioex);
        }
        while (active) {
            try {
                handle_notify(p);
            } catch (IOException ioex) {
                /* done causes an exception when it closes the socket */
                if (active) {
                    LOGGER.error("IO Error", ioex);
                }
            }
        }
    }

    /**
     * Send_query.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void send_query() throws IOException {
        DatagramPacket p;
        p = new DatagramPacket(query.getBytes(), query.length(), addr, SSDP_PORT);
        socket.send(p);
        socket.send(p);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            LOGGER.error("Error while querying network through UPNP", e);
        }
        socket.send(p);
    }
}
