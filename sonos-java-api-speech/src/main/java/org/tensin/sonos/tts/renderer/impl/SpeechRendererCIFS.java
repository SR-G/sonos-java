package org.tensin.sonos.tts.renderer.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.AVTransportListener;
import org.tensin.sonos.control.AVTransportService;
import org.tensin.sonos.tts.SonosTTSConstants;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;
import org.tensin.sonos.tts.renderer.ISpeechRenderer;
import org.tensin.sonos.tts.renderer.RendererType;
import org.tensin.sonos.xml.AVTransportEventHandler.AVTransportEventType;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * The Class SpeechRendererCIFS.
 */
public class SpeechRendererCIFS extends AbstractSpeechRenderer implements ISpeechRenderer {
    
    /**
     * Builds the.
     *
     * @param path the filename
     * @return the speech renderer cifs
     */
    public static SpeechRendererCIFS build(final String path) {
        final SpeechRendererCIFS result = new SpeechRendererCIFS();
        result.pathFileGeneration = path;
        result.pathSonosPublication = path;
        return result;
    } 
    
    /**
     * Builds the.
     *
     * @param pathFileGeneration the filename published
     * @param pathSonosPublication the filename sonos
     * @return the speech renderer cifs
     */
    public static SpeechRendererCIFS build(final String pathFileGeneration, final String pathSonosPublication) {
        final SpeechRendererCIFS result = new SpeechRendererCIFS();
        result.pathFileGeneration = pathFileGeneration;
        result.pathSonosPublication = pathSonosPublication;
        return result;
    }

    /** The file extension. */
    private String fileExtension = SonosTTSConstants.DEFAULT_FILE_FORMAT;

    /** The cifs url. */
    private String pathFileGeneration;
    
    /** The filename sonos. */
    private String pathSonosPublication;
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.renderer.ISpeechRenderer#getType()
     */
    @Override
    public RendererType getType() {
        return RendererType.CIFS;
    }

    /**
     * Builds the cache file name.
     *
     * @param path the path
     * @param hashedText the hashed text
     * @return the string
     */
    private String buildCacheFileName(final String path, final String hashedText) {
        return path + File.separator + hashedText + "." + fileExtension;
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();
    
    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.renderer.ISpeechRenderer#render(java.io.InputStream)
     */
    @Override
    public void render(final VoiceBuilderOutput voiceBuilderOutput, final String... zones) throws SonosException {
        FileOutputStream fos = null;
        try {
            final String destZones = Joiner.on(", ").join(zones);
            LOGGER.info("Rendering on CIFS share file [" + voiceBuilderOutput.getHash() + "], stored in [" + pathFileGeneration + "] and published in [" + pathSonosPublication + "], zones [" + destZones + "]");
            fos = new FileOutputStream(buildCacheFileName(pathFileGeneration, voiceBuilderOutput.getHash()));
            IOUtils.copy(voiceBuilderOutput.getInputStream(), fos);
            final List<String> fileToPlay = ImmutableList.of(buildCacheFileName(pathSonosPublication, voiceBuilderOutput.getHash()));
            // Enqueue and play
            getSonosController().registerListener(destZones, new AVTransportListener() {
                
                @Override
                public void valuesChanged(Set<AVTransportEventType> events, AVTransportService source) {
                    System.out.println("================================================");
                }
            });
            getSonosController().execute(destZones, "add", fileToPlay);
            Thread.sleep(5000);
            getSonosController().execute(destZones, "remove", ImmutableList.of("1"));
        } catch (FileNotFoundException e) {
            throw new SonosException("Can't render TTS stream to CIFS location [" + pathFileGeneration + "] / playing from [" + pathSonosPublication + "]", e);
        } catch (IOException e) {
            throw new SonosException("Can't render TTS stream to CIFS location [" + pathFileGeneration + "] / playing from [" + pathSonosPublication + "]", e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}