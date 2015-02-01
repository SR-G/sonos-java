package org.tensin.sonos.tts.builder.impl;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.Language;
import org.tensin.sonos.tts.builder.IVoiceBuilder;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;

import com.darkprograms.speech.synthesiser.Synthesiser;

/**
 * The Class VoiceBuilder.
 */
public class VoiceBuilderGoogleTTS implements IVoiceBuilder {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Builds the synthesier.
     *
     * @param languageCode the language code
     * @return the synthesiser
     */
    protected Synthesiser buildSynthesier(final Language languageCode) {
        final Synthesiser s = new Synthesiser();
        s.setLanguage(languageCode.code());
        return s;
    }
    
    /** The is. */
    private InputStream is;
    
    private BufferedInputStream bis;
    
    /**
     * Retrieve.
     *
     * @param language the language code
     * @param text the text
     * @throws SonosException the sonos exception
     */
    public VoiceBuilderOutput retrieve(final Language language, final String text) throws SonosException {
        LOGGER.info("Rendering [" + text + "] in language [" + language.code() + "]");
        final Synthesiser s = buildSynthesier(language);
        try {
            is = s.getMP3Data(text);
            bis = new BufferedInputStream(is);
            return VoiceBuilderOutput.build(bis, language, text);
        } catch (FileNotFoundException e) {
            throw new SonosException("Can't render text [" + text + "]", e);
        } catch (IOException e) {
            throw new SonosException("Can't render text [" + text + "]", e);
        }
    }
    
    /**
     * Close.
     */
    public void close() {
        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(is);
    }
}