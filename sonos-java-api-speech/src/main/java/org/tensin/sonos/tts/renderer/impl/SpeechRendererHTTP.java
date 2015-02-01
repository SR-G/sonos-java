package org.tensin.sonos.tts.renderer.impl;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;
import org.tensin.sonos.tts.renderer.ISpeechRenderer;
import org.tensin.sonos.tts.renderer.RendererType;

/**
 * The Class SpeechRendererHTTP.
 */
public class SpeechRendererHTTP extends AbstractSpeechRenderer implements ISpeechRenderer {
    
    /** The port. */
    private int port;
    
    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.renderer.ISpeechRenderer#getType()
     */
    @Override
    public RendererType getType() {
        return RendererType.HTTP;
    }

    /* (non-Javadoc)
     * @see org.tensin.sonos.speech.renderer.ISpeechRenderer#render(java.io.InputStream)
     */
    @Override
    public void render(final VoiceBuilderOutput voiceBuilderOutput, final String... zones) throws SonosException {
        // TODO exposes over an embedded jetty the stream, on a given port, directly readable by Sonos
    }
}