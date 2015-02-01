package org.tensin.sonos.tts.builder.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.Language;
import org.tensin.sonos.tts.SonosTTSConstants;
import org.tensin.sonos.tts.builder.IVoiceBuilder;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;

/**
 * The Class VoiceCacheBuilder.
 */
public final class VoiceBuilderGoogleTTSCached extends VoiceBuilderGoogleTTS implements IVoiceBuilder {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The Constant DEFAULT_CACHE_NAME. */
    private static final String DEFAULT_CACHE_NAME = "sonos-tts-speech";

    /** The cache path. */
    private final String cachePath;
    
    public String getCachePath() {
        return cachePath;
    }

    /** The is. */
    private InputStream is;
    
    private BufferedInputStream bis;

    /** The file extension. */
    private String fileExtension = SonosTTSConstants.DEFAULT_FILE_FORMAT; 
    
    /**
     * Instantiates a new voice cache builder.
     *
     * @param cachePath the cache path
     */
    public VoiceBuilderGoogleTTSCached(String cachePath) {
        this.cachePath = cachePath;
    }

    /**
     * Instantiates a new voice builder google tts cached.
     */
    public VoiceBuilderGoogleTTSCached() {
        this.cachePath = System.getProperty("java.io.tmpdir") + File.separator + DEFAULT_CACHE_NAME;
    }

    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.builder.VoiceBuilder#retrieve(java.lang.String, java.lang.String)
     */
    public VoiceBuilderOutput retrieve(final Language language, final String text) throws SonosException {
        final VoiceBuilderOutput result = VoiceBuilderOutput.build(language, text);
        final String hashedText = result.getHash();
        if (! isTextStoredInCache(hashedText)) {
            LOGGER.info("Storing text in cache [" + text + "]");
            final VoiceBuilderOutput output = super.retrieve(language, text);
            storeTextInCache(hashedText, output.getInputStream());
            super.close();
        }  else {
            LOGGER.info("Grabbing back text from cache [" + text + "] ("+ language.code()+")");
        }
        result.setInputStream(retrieveTextFromCache(hashedText));
        return result;
    }
    
    /**
     * Builds the cache file name.
     *
     * @param hashedText the hashed text
     * @return the string
     */
    private String buildCacheFileName(final String hashedText) {
        return cachePath + File.separator + hashedText + "." + fileExtension;
    }

    /**
     * Checks if is text stored in cache.
     *
     * @param hashedText the hashed text
     * @return true, if is text stored in cache
     */
    private boolean isTextStoredInCache(final String hashedText) {
        return new File(buildCacheFileName(hashedText)).isFile();
    }
    
    /**
     * Store text in cache.
     *
     * @param hashedText the hashed text
     * @param is the is
     * @throws SonosException the sonos exception
     */
    private void storeTextInCache(final String hashedText, final InputStream is) throws SonosException {
        final String cachedFileName = buildCacheFileName(hashedText);
        FileOutputStream fos = null;
        try {
            final File destFile = new File(cachedFileName);
            FileUtils.forceMkdir(destFile.getParentFile());
            fos = new FileOutputStream(destFile);
            IOUtils.copy(is, fos);
        } catch (IOException e) {
            throw new SonosException("Can't store cached voice file under [" + cachedFileName + "]", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }
    
    /**
     * Retrieve text from cache.
     *
     * @param hashedText the hashed text
     * @return the input stream
     * @throws SonosException the sonos exception
     */
    private InputStream retrieveTextFromCache(final String hashedText) throws SonosException {
        final String cachedFileName = buildCacheFileName(hashedText);
        try {
            is = new FileInputStream(new File(cachedFileName));
            bis = new BufferedInputStream(is);
            return bis;
        } catch (FileNotFoundException e) {
            throw new SonosException("Can't load file from cache [" + cachedFileName + "]", e);
        }
    }

    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.builder.VoiceBuilder#close()
     */
    public void close() {
        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(is);
        super.close();
    }
}