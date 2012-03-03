package org.tensin.sonos.upnp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * The Class Util.
 */
public class Util {

    /**
     * Load file.
     * 
     * @param fileName
     *            the file name
     * @return the byte[]
     */
    public static byte[] loadFile(final String fileName) throws SonosException {
        try {
            File f = new File(fileName);
            FileInputStream in = new FileInputStream(f);
            int sz = (int) f.length();
            byte[] data = new byte[sz];
            while (sz > 0) {
                int r = in.read(data, data.length - sz, sz);
                if (r <= 0) {
                    return null;
                }
                sz -= r;
            }
            return data;
        } catch (IOException ioex) {
            throw new SonosException("Error while reading file [" + fileName + "]", ioex);
        }

    }
}
