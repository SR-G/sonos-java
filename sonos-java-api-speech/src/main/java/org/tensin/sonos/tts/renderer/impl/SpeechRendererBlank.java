package org.tensin.sonos.tts.renderer.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;
import org.tensin.sonos.tts.renderer.ISpeechRenderer;
import org.tensin.sonos.tts.renderer.RendererType;

/**
 * The Class SpeechRendererBlank.
 */
public class SpeechRendererBlank extends AbstractSpeechRenderer implements ISpeechRenderer {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.renderer.ISpeechRenderer#getType()
     */
    @Override
    public RendererType getType() {
        return RendererType.BLANK;
    }

    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.renderer.ISpeechRenderer#render(java.io.InputStream)
     */
    @Override
    public void render(final VoiceBuilderOutput voiceBuilderOutput, final String... zones) throws SonosException {
        LOGGER.warn("Blank renderer, not exposing TTS stream");
    }
}