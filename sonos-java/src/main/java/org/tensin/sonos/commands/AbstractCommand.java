package org.tensin.sonos.commands;

import java.util.List;

/**
 * The Class AbstractCommand.
 */
public abstract class AbstractCommand {

    /** The args. */
    private List<String> args;

    /**
     * Gets the args.
     * 
     * @return the args
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Checks for args.
     * 
     * @return true, if successful
     */
    public boolean hasArgs() {
        return ((args != null) && (args.size() > 0));
    }

    /**
     * Need args.
     * 
     * @return true, if successful
     */
    public boolean needArgs() {
        return false;
    }

    /**
     * Sets the args.
     * 
     * @param args
     *            the new args
     */
    public void setArgs(final List<String> args) {
        this.args = args;
    }

}
