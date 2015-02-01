package org.tensin.sonos.tts.renderer;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.renderer.impl.SpeechRendererBlank;
import org.tensin.sonos.tts.renderer.impl.SpeechRendererCIFS;
import org.tensin.sonos.tts.renderer.impl.SpeechRendererHTTP;
import org.tensin.sonos.tts.renderer.impl.SpeechRendererLocal;

/**
 * A factory for creating SpeechRenderer objects.
 */
public final class SpeechRendererFactory {

    /**
     * Builds the renderer.
     *
     * @param type the type
     * @return the i speech renderer
     * @throws SonosException the sonos exception
     */
    public static final ISpeechRenderer buildRenderer(final RendererType type) throws SonosException {
        switch (type) {
        case BLANK :
            return new SpeechRendererBlank();
        case CIFS :
            return new SpeechRendererCIFS();
        case HTTP :
            return new SpeechRendererHTTP();
        case LOCAL : 
            return new SpeechRendererLocal();
        default :
            throw new SonosException("Don't know hot to build renderer type [" + type.name() + "]");
        }
    }
}