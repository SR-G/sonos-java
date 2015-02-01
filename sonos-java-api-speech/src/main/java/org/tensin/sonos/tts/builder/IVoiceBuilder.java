package org.tensin.sonos.tts.builder;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.Language;

public interface IVoiceBuilder  {
    
    VoiceBuilderOutput retrieve(final Language languageCode, final String text) throws SonosException;

    void close();
}