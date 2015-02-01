package org.tensin.sonos.tts.renderer;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;

/**
 * The Interface ISpeechRenderer.
 */
public interface ISpeechRenderer {

    /**
     * Gets the type.
     *
     * @return the type
     */
    RendererType getType();

    /**
     * Render.
     *
     * @param speechInputStream the speech input stream
     * @throws SonosException the sonos exception
     */
    void render(final VoiceBuilderOutput voiceBuilderOutput, final String... zones) throws SonosException;

    /**
     * Inits the.
     *
     * @throws SonosException the sonos exception
     */
    void init() throws SonosException;
    
    /**
     * Shutdown.
     *
     * @throws SonosException the sonos exception
     */
    void shutdown() throws SonosException;
}