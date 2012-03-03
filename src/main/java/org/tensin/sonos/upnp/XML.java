package org.tensin.sonos.upnp;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: &apos; -> '

/**
 * The Class XML.
 */
public class XML {

    /**
     * The Class Oops.
     */
    static public class Oops extends Exception {

        /**
         * Instantiates a new oops.
         * 
         * @param msg
         *            the msg
         */
        public Oops(final String msg) {
            super(msg);
        }
    }

    /** The Constant DEBUG. */
    private static final boolean DEBUG = false;

    /** The seq. */
    XMLSequence seq; /* entire buffer */

    /** The tmp. */
    XMLSequence tmp; /* for content return */

    /** The xml. */
    char[] xml;
    /* offset and length of the name of the current tag */
    /** The tag_off. */
    int tag_off;

    /** The tag_len. */
    int tag_len;
    /* offset and length of the attr area of the current tag */
    /** The att_off. */
    int att_off;

    /** The att_len. */
    int att_len;

    /* true if the current tag is <...>, false if </...> */
    /** The is open. */
    boolean isOpen;

    /** The decoder. */
    CharsetDecoder decoder;

    /* used for io operations */
    /** The buf. */
    CharBuffer buf;

    /** The cs. */
    static Charset cs = Charset.forName("UTF-8");

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XML.class);

    /**
     * Instantiates a new xML.
     * 
     * @param size
     *            the size
     */
    public XML(final int size) {
        decoder = cs.newDecoder();
        seq = new XMLSequence();
        tmp = new XMLSequence();
        xml = new char[size];
        buf = CharBuffer.wrap(xml);
    }

    /* require </tag> and consume it */
    /**
     * Close.
     * 
     * @param name
     *            the name
     * @throws Oops
     *             the oops
     */
    public void close(final String name) throws XML.Oops {
        if (isOpen || !tag_eq(name)) {
            throw new XML.Oops("expecting </" + name + "> but found " + str());
        }
        nextTag();
    }

    /**
     * Close.
     * 
     * @param name
     *            the name
     * @throws Oops
     *             the oops
     */
    public void close(final XMLSequence name) throws XML.Oops {
        if (isOpen) {
            throw new XML.Oops("1expected </" + name + ">, found " + str());
        }
        if (!tag_eq(name)) {
            throw new XML.Oops("2expected </" + name + ">, found " + str());
        }
        nextTag();
    }

    /* eat the current tag and any children */
    /**
     * Consume.
     * 
     * @throws Oops
     *             the oops
     */
    public void consume() throws XML.Oops {
        tmp.offset = tag_off;
        tmp.count = tag_len;
        nextTag();
        while (isOpen) {
            consume();
        }
        close(tmp);
    }

    /**
     * Gets the attr.
     * 
     * @param name
     *            the name
     * @return the attr
     */
    public XMLSequence getAttr(final String name) {
        int nlen = name.length();
        int n;

        tmp.offset = att_off;
        tmp.count = att_len;
        tmp.pos = att_off;

        for (;;) {
            int off = tmp.space();
            int len = tmp.name();
            if (len < 0) {
                break;
            }
            LOGGER.debug("ANAME: [" + new String(tmp.data, off, len) + "]");
            int voff = tmp.value();
            if (voff < 0) {
                break;
            }
            int vend = tmp.next('"');
            if (vend < 0) {
                break;
            }
            vend--;
            LOGGER.debug("ATEXT: [" + new String(tmp.data, voff, vend - voff) + "]");

            if (nlen != len) {
                continue;
            }
            for (n = 0; n < len; n++) {
                if (name.charAt(n) != tmp.data[off + n]) {
                    break;
                }
            }
            if (nlen != n) {
                continue;
            }

            tmp.offset = voff;
            tmp.count = vend - voff;
            return tmp;
        }
        return null;
    }

    /*
     * set sequence to the text between the end of the current tag
     * and the beginning of the next tag.
     */
    /**
     * Gets the text.
     * 
     * @return the text
     */
    public XMLSequence getText() {
        char[] data = xml;
        int n;
        tmp.data = data;
        n = tmp.offset = (att_off + att_len);
        try {
            for (;;) {
                if (data[n] == '<') {
                    break;
                }
                n++;
            }
            tmp.count = n - tmp.offset;
        } catch (ArrayIndexOutOfBoundsException x) {
            tmp.count = 0;
        }
        return tmp;
    }

    /**
     * Inits the.
     * 
     * @param in
     *            the in
     */
    public void init(final ByteBuffer in) {
        buf.clear();
        CoderResult cr = decoder.decode(in, buf, true);
        // TODO: error handling
        buf.flip();
        reset();
    }

    /**
     * Inits the.
     * 
     * @param s
     *            the s
     */
    public void init(final XMLSequence s) {
        buf.clear();
        buf.put(s.data, s.offset, s.count);
        buf.flip();
        reset();
    }

    /**
     * More.
     * 
     * @return true, if successful
     */
    public boolean more() {
        return isOpen;
    }

    /**
     * Next tag.
     */
    void nextTag() {
        /* can't deal with comments or directives */
        int off = seq.next('<');
        boolean opn = seq.isOpen();
        int len = seq.name();
        int att = seq.pos;
        int nxt = seq.next('>');

        /* don't advance if we're in a strange state */
        if ((off < 0) || (len < 0) || (nxt < 0)) {
            return;
        }

        if (!opn) {
            off++;
        }

        tag_off = off;
        tag_len = len;
        isOpen = opn;

        att_off = att;
        att_len = nxt - att;

        if (opn) {
            LOGGER.debug("TAG [" + new String(seq.data, tag_off, tag_len) + "]");
            LOGGER.debug("ATR [" + new String(seq.data, att_off, att_len) + "]");
        } else {
            LOGGER.debug("tag [" + new String(seq.data, tag_off, tag_len) + "]");
        }
    }

    /* require <tag> and consume it */
    /**
     * Open.
     * 
     * @param name
     *            the name
     * @throws Oops
     *             the oops
     */
    public void open(final String name) throws XML.Oops {
        if (!isOpen || !tag_eq(name)) {
            throw new XML.Oops("expecting <" + name + "> but found " + str());
        }
        nextTag();
    }

    /**
     * Prints the.
     * 
     * @param out
     *            the out
     * @param max
     *            the max
     */
    public void print(final PrintStream out, final int max) {
        char[] buf = new char[max];
        print(out, max, 0, buf);
    }

    /**
     * Prints the.
     * 
     * @param out
     *            the out
     * @param max
     *            the max
     * @param indent
     *            the indent
     * @param buf
     *            the buf
     */
    void print(final PrintStream out, final int max, final int indent, final char[] buf) {
        XMLSequence s;
        int n;
        if (!isOpen) {
            out.println("ERROR");
            return;
        }
        for (n = 0; n < indent; n++) {
            out.print(" ");
        }
        out.print(str());
        s = getText();
        nextTag();
        if (isOpen) {
            out.print("\n");
            do {
                print(out, max, indent + 2, buf);
            } while (isOpen);
            for (n = 0; n < indent; n++) {
                out.print(" ");
            }
            out.println(str());
        } else {
            if (s.count > max) {
                s.count = max;
                n = s.unescape(buf, 0);
                out.println("" + new String(buf, 0, n) + "..." + str());
            } else {
                n = s.unescape(buf, 0);
                out.println("" + new String(buf, 0, n) + str());
            }
        }
        nextTag();
    }

    /* require <tag> text </tag> and return text */
    /**
     * Read.
     * 
     * @param name
     *            the name
     * @return the xML sequence
     * @throws Oops
     *             the oops
     */
    public XMLSequence read(final String name) throws XML.Oops {
        int start = att_off + att_len;
        open(name);
        tmp.adjust(start, tag_off - 2);
        close(name);
        LOGGER.debug("VAL [" + tmp + "]");
        return tmp;
    }

    /**
     * Reset.
     */
    void reset() {
        seq.init(xml, buf.arrayOffset(), buf.length());
        tmp.init(xml, 0, 0);
        nextTag();
    }

    /**
     * Rewind.
     */
    public void rewind() {
        seq.pos = seq.offset;
        nextTag();
    }

    /* format current begin/end tag as a string. for error messages */
    /**
     * Str.
     * 
     * @return the string
     */
    String str() {
        if (isOpen) {
            return "<" + new String(seq.data, tag_off, tag_len) + ">";
        } else {
            return "</" + new String(seq.data, tag_off, tag_len) + ">";
        }
    }

    /**
     * Tag_eq.
     * 
     * @param name
     *            the name
     * @return true, if successful
     */
    public boolean tag_eq(final CharSequence name) {
        if (name.length() != tag_len) {
            return false;
        }
        for (int n = 0; n < tag_len; n++) {
            if (name.charAt(n) != seq.data[tag_off + n]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Try read.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     * @return true, if successful
     * @throws Oops
     *             the oops
     */
    public boolean tryRead(final String name, final XMLSequence value) throws XML.Oops {
        if (!isOpen || !tag_eq(name)) {
            return false;
        }
        value.data = xml;
        value.offset = att_off + att_len;
        nextTag();
        value.count = tag_off - value.offset;
        close(name);
        return true;
    }

    /* read the next <name> value </name> returns false if no open tag */
    /**
     * Try read.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     * @return true, if successful
     * @throws Oops
     *             the oops
     */
    public boolean tryRead(final XMLSequence name, final XMLSequence value) throws XML.Oops {
        if (!isOpen) {
            return false;
        }

        name.data = xml;
        name.offset = tag_off;
        name.count = tag_len;

        value.data = xml;
        value.offset = att_off + att_len;
        nextTag();
        value.count = tag_off - value.offset - 2;

        close(name);

        return true;
    }
}
