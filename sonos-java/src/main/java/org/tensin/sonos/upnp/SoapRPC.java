package org.tensin.sonos.upnp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class SoapRPC.
 */
public class SoapRPC {

    /**
     * The Class Endpoint.
     */
    public static class Endpoint {

        /** The path. */
        String service, path;

        /**
         * Instantiates a new endpoint.
         * 
         * @param service
         *            the service
         * @param path
         *            the path
         */
        public Endpoint(final String service, final String path) {
            this.service = service;
            this.path = path;
        }
    }

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(SoapRPC.class);

    /** The cs. */
    static Charset cs = Charset.forName("UTF-8");

    /** The Constant MAXIO. */
    static final int MAXIO = 256 * 1024;

    /** The trace_io. */
    public boolean trace_io;

    /** The trace_reply. */
    public boolean trace_reply;
    /* actual host to communicate with */
    /** The addr. */
    InetAddress addr;

    /** The port. */
    int port;

    /* XML object for reply */
    /** The xml. */
    XML xml;

    /* io buffer */
    /** The bb. */
    ByteBuffer bb;
    /* assembly buffers for rpc header and message */
    /** The hdr. */
    StringBuilder hdr;

    /** The msg. */
    StringBuilder msg;

    /** The encoder. */
    CharsetEncoder encoder;

    /* hold remote information while assembling message */
    /** The xpath. */
    String xmethod, xservice, xpath;

    /**
     * Instantiates a new soap rpc.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     */
    public SoapRPC(final String host, final int port) {
        init(host, port);
    }

    /**
     * Call.
     */
    void call() {
        CharBuffer hdrbuf;
        CharBuffer msgbuf;
        CoderResult cr;
        int off, r;
        byte[] buf;
        try {
            Socket s = new Socket(addr, port);
            OutputStream out = s.getOutputStream();
            InputStream in = s.getInputStream();

            if (trace_io) {
                LOGGER.debug("Emitting :\n" + hdr + "\n" + msg);
            }

            buf = bb.array();

            /* to keep things simple, the headers must fit in one pass */
            bb.clear();
            encoder.reset();
            hdrbuf = CharBuffer.wrap(hdr);
            cr = encoder.encode(hdrbuf, bb, false);
            if (cr != CoderResult.UNDERFLOW) {
                throw new Exception("encoder failed (1)");
            }

            msgbuf = CharBuffer.wrap(msg);
            do {
                cr = encoder.encode(msgbuf, bb, true);
                if (cr.isError()) {
                    throw new Exception("encoder failed (2)");
                }
                out.write(buf, 0, bb.position());
                bb.clear();
            } while (cr == CoderResult.OVERFLOW);

            /* read reply */
            for (off = 0;; off += r) {
                r = in.read(buf, off, buf.length - off);
                if (r <= 0) {
                    break;
                }
            }
            bb.limit(off);

            s.close();
            if (trace_io) {
                LOGGER.info("Received : \n" + new String(buf, 0, off));
            }
        } catch (Exception e) {
            LOGGER.error("Error while sending message: " + e.getMessage(), e);
        }
    }

    /**
     * Close tag.
     * 
     * @param name
     *            the name
     * @return the soap rpc
     */
    public SoapRPC closeTag(final String name) {
        msg.append('<');
        msg.append('/');
        msg.append(name);
        msg.append('>');
        return this;
    }

    /**
     * Encode.
     * 
     * @param csq
     *            the csq
     * @return the soap rpc
     */
    public SoapRPC encode(final CharSequence csq) {
        int n, max = csq.length();
        char c;
        for (n = 0; n < max; n++) {
            switch ((c = csq.charAt(n))) {
            case '<':
                msg.append("&lt;");
                break;
            case '>':
                msg.append("&gt;");
                break;
            case '&':
                msg.append("&amp;");
                break;
            case '"':
                msg.append("&quot;");
                break;
            case '\'':
                msg.append("&apos;");
                break;
            default:
                msg.append(c);
            }
        }
        return this;
    }

    /**
     * Inits the.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     */
    void init(final String host, final int port) {
        try {
            addr = InetAddress.getByName(host);
        } catch (Exception x) {
        }
        this.port = port;

        encoder = cs.newEncoder();

        bb = ByteBuffer.wrap(new byte[MAXIO]);
        xml = new XML(MAXIO);
        hdr = new StringBuilder(2048);
        msg = new StringBuilder(8192);
    }

    /**
     * Invoke.
     * 
     * @return the xML
     */
    public XML invoke() {
        if (xmethod == null) {
            throw new RuntimeException("cannot invoke before prepare");
        }

        /* close the envelope */
        msg.append("</u:");
        msg.append(xmethod);
        msg.append("></s:Body></s:Envelope>\n");

        /* build HTTP headers */
        hdr.append("POST ");
        hdr.append(xpath);
        hdr.append(" HTTP/1.0\r\n" + "CONNECTION: close\r\n" + "Content-Type: text/xml; charset=\"utf-8\"\r\n" + "Content-Length: ");
        hdr.append(msg.length());
        hdr.append("\r\n" + "SOAPACTION: \"urn:schemas-upnp-org:service:");
        hdr.append(xservice);
        hdr.append("#");
        hdr.append(xmethod);
        hdr.append("\"\r\n\r\n");

        xmethod = null;

        call();

        xml.init(bb);
        try {
            if (trace_reply) {
                LOGGER.info("Reply : \n");
                xml.print(System.out, 64);
                xml.rewind();
            }
            xml.open("s:Envelope");
            xml.open("s:Body");
            return xml;
        } catch (XML.Oops x) {
            System.err.println("OOPS " + x);
            x.printStackTrace();
            return null;
        }
    }

    /**
     * Open tag.
     * 
     * @param name
     *            the name
     * @return the soap rpc
     */
    public SoapRPC openTag(final String name) {
        msg.append('<');
        msg.append(name);
        msg.append('>');
        return this;
    }

    /**
     * Prepare.
     * 
     * @param ept
     *            the ept
     * @param method
     *            the method
     */
    public void prepare(final Endpoint ept, final String method) {
        xmethod = method;
        xservice = ept.service;
        xpath = ept.path;

        msg.setLength(0);
        hdr.setLength(0);

        /* setup message envelope/prefix */
        msg.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:");
        msg.append(xmethod);
        msg.append(" xmlns:u=\"urn:schemas-upnp-org:service:");
        msg.append(xservice);
        msg.append("\">\n");
    }

    /**
     * Simple tag.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     * @return the soap rpc
     */
    public SoapRPC simpleTag(final String name, final int value) {
        openTag(name);
        msg.append(value);
        closeTag(name);
        return this;
    }

    /**
     * Simple tag.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     * @return the soap rpc
     */
    public SoapRPC simpleTag(final String name, final String value) {
        openTag(name);
        encode(value);
        closeTag(name);
        return this;
    }
}
