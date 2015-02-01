package org.tensin.sonos.tts.builder;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.builder.impl.VoiceBuilderGoogleTTS;
import org.tensin.sonos.tts.builder.impl.VoiceBuilderGoogleTTSCached;

public class VoiceBuilderFactory {
    /**
     * Builds the renderer.
     *
     * @param type the type
     * @return the i speech renderer
     * @throws SonosException the sonos exception
     */
    public static final IVoiceBuilder buildVoiceBuilder(final VoiceBuilderType type) throws SonosException {
        switch (type) {
        case GOOGLE_V1_TTS :
            return new VoiceBuilderGoogleTTS();
        case GOOGLE_V1_CACHED_TTS :
            return new VoiceBuilderGoogleTTSCached();
        default :
            throw new SonosException("Don't know hot to build voice builder type [" + type.name() + "]");
        }
    }

}
