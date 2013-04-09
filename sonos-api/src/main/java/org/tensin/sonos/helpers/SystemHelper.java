package org.tensin.sonos.helpers;

/**
 * The Class SystemHelper. Has to be an object and not static method in order to put another implementation while running tests (for example, in order not to do any System.exit() while running tests)
 */
public class SystemHelper {

    /**
     * Err.
     * 
     * @param s
     *            the s
     */
    public void err(final String s) {
        System.err.print(s);
    }

    /**
     * Errln.
     * 
     * @param s
     *            the s
     */
    public void errln(final String s) {
        System.err.println(s);
    }

    /**
     * Exit.
     * 
     * @param code
     *            the code
     */
    public void exit(final int code) {
        System.exit(code);
    }

    /**
     * Out.
     * 
     * @param s
     *            the s
     */
    public void out(final String s) {
        System.out.print(s);
    }

    /**
     * Outln.
     * 
     * @param s
     *            the s
     */
    public void outln(final String s) {
        System.out.println(s);
    }

}