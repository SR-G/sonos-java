package org.tensin.sonos.upnp;

/**
 * The Class XMLSequence.
 */
public class XMLSequence implements CharSequence {

    /** The data. */
    char[] data;

    /** The offset. */
    int offset;

    /** The count. */
    int count;

    /** The pos. */
    int pos;

    /** The x name0. */
    static char xName0[];

    /** The x name x. */
    static char xNameX[];
    static {
        xName0 = new char[127];
        xNameX = new char[127];
        int n;
        for (n = 'a'; n <= 'z'; n++) {
            xName0[n] = 1;
            xNameX[n] = 1;
        }
        for (n = 'A'; n <= 'Z'; n++) {
            xName0[n] = 1;
            xNameX[n] = 1;
        }
        xName0['_'] = 1;
        xName0[':'] = 1;

        for (n = '0'; n <= '9'; n++) {
            xNameX[n] = 1;
        }
        xNameX['_'] = 1;
        xNameX[':'] = 1;
        xNameX['-'] = 1;
        xNameX['.'] = 1;
    }

    /**
     * Instantiates a new xML sequence.
     */
    public XMLSequence() {
    }

    /**
     * Adjust.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     */
    void adjust(final int start, final int end) {
        offset = start;
        count = end - start;
        pos = offset;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.CharSequence#charAt(int)
     */
    @Override
    public char charAt(final int index) {
        return data[offset + index];
    }

    /**
     * Copy.
     * 
     * @return the char sequence
     */
    public CharSequence copy() {
        XMLSequence s = new XMLSequence();
        s.init(data, offset, offset + count);
        return s;
    }

    /**
     * Eq.
     * 
     * @param s
     *            the s
     * @return true, if successful
     */
    public boolean eq(final String s) {
        int len = s.length();
        if (len != count) {
            return false;
        }
        for (int n = 0; n < len; n++) {
            if (s.charAt(n) != data[offset + n]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Eq.
     * 
     * @param other
     *            the other
     * @return true, if successful
     */
    boolean eq(final XMLSequence other) {
        int count = this.count;
        if (count != other.count) {
            return false;
        }
        char[] a = data;
        int ao = offset;
        char[] b = other.data;
        int bo = other.offset;
        while (count-- > 0) {
            if (a[ao++] != b[bo++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Inits the.
     * 
     * @param data
     *            the data
     * @param start
     *            the start
     * @param end
     *            the end
     */
    public void init(final char[] data, final int start, final int end) {
        this.data = data;
        offset = start;
        count = end - start;
        pos = offset;
    }

    /**
     * Inits the.
     * 
     * @param s
     *            the s
     */
    public void init(final XMLSequence s) {
        data = s.data;
        offset = s.offset;
        count = s.count;
        pos = offset;
    }

    /* returns false (and advances) if data[pos] == '/', otherwise true */
    /**
     * Checks if is open.
     * 
     * @return true, if is open
     */
    boolean isOpen() {
        if (data[pos] == '/') {
            pos++;
            return false;
        } else {
            return true;
        }
    }

    /* CharSequence interface */
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.CharSequence#length()
     */
    @Override
    public int length() {
        return count;
    }

    /* matches an XML element or attribute name, returns length (-1==failure), pos will be after */
    /**
     * Name.
     * 
     * @return the int
     */
    int name() {
        char[] x = data;
        int n = pos;
        int end = offset + count;
        if (n >= end) {
            return -1;
        }
        try {
            if (xName0[x[n]] == 0) {
                return -1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
        while (++n < end) {
            try {
                if (xNameX[x[n]] == 1) {
                    continue;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            break;
        }
        end = n - pos;
        pos = n;
        return end;
    }

    /* Parsing Tools */

    /* returns next offset after a match with c, or -1 if no match */
    /**
     * Next.
     * 
     * @param c
     *            the c
     * @return the int
     */
    int next(final char c) {
        char[] x = data;
        int n = pos;
        int end = offset + count;
        while (n < end) {
            if (x[n++] == c) {
                pos = n;
                return n;
            }
        }
        return -1;
    }

    /* advance pos beyond whitespace, returns updated position */
    /**
     * Space.
     * 
     * @return the int
     */
    int space() {
        char[] x = data;
        int n = pos;
        int end = offset + count;
        while (n < end) {
            switch (x[n]) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                n++;
                break;
            default:
                pos = n;
                return n;
            }
        }
        pos = n;
        return n;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    @Override
    public CharSequence subSequence(final int start, final int end) {
        XMLSequence x = new XMLSequence();
        x.init(data, offset + start, offset + end);
        return x;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (data == null) {
            return "";
        }
        return new String(data, offset, count);
    }

    /**
     * Trim.
     */
    public void trim() {
        while (count > 0) {
            char c = data[offset];
            if ((c == ' ') || (c == '\r') || (c == '\n') || (c == '\t')) {
                offset++;
                count--;
                continue;
            }
            c = data[(offset + count) - 1];
            if ((c == ' ') || (c == '\r') || (c == '\n') || (c == '\t')) {
                count--;
                continue;
            }
            break;
        }
        pos = offset;
    }

    /* modifies the sequence in-place, escaping basic entities */
    /**
     * Unescape.
     * 
     * @return the xML sequence
     */
    public XMLSequence unescape() {
        count = unescape(data, offset) - offset;
        return this;
    }

    /* copies the sequence into a char[] + offset, escaping basic entities */
    /**
     * Unescape.
     * 
     * @param out
     *            the out
     * @param off
     *            the off
     * @return the int
     */
    public int unescape(final char[] out, int off) {
        char[] in = data;
        int n = offset;
        int max = n + count;

        while (n < max) {
            char c = in[n++];
            if (c != '&') {
                out[off++] = c;
                continue;
            }
            int e = n;
            while (n < max) {
                if (in[n++] != ';') {
                    continue;
                }
                switch (in[e]) {
                case 'l': // lt
                    out[off++] = '<';
                    break;
                case 'g': // gt
                    out[off++] = '>';
                    break;
                case 'q': // quot
                    out[off++] = '"';
                    break;
                case 'a': // amp | apos
                    if (in[e + 1] == 'm') {
                        out[off++] = '&';
                    } else if (in[e + 1] == 'p') {
                        out[off++] = '\'';
                    }
                    break;
                }
                break;
            }
        }
        return off;
    }

    /* match =" returning position or -1 if no match */
    /**
     * Value.
     * 
     * @return the int
     */
    int value() {
        char[] x = data;
        int n = pos;
        int end = offset + count;
        if (n >= end) {
            return -1;
        }
        if (x[n++] != '=') {
            return -1;
        }
        if (n >= end) {
            return -1;
        }
        if (x[n++] != '"') {
            return -1;
        }
        pos = n;
        return n;
    }
}
