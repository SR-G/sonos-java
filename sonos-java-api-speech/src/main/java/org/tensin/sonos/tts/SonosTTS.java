package org.tensin.sonos.tts;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.helpers.LogHelper;
import org.tensin.sonos.tts.builder.IVoiceBuilder;
import org.tensin.sonos.tts.builder.VoiceBuilderFactory;
import org.tensin.sonos.tts.builder.VoiceBuilderType;
import org.tensin.sonos.tts.builder.impl.VoiceBuilderGoogleTTS;
import org.tensin.sonos.tts.builder.impl.VoiceBuilderGoogleTTSCached;
import org.tensin.sonos.tts.renderer.ISpeechRenderer;
import org.tensin.sonos.tts.renderer.RendererType;
import org.tensin.sonos.tts.renderer.SpeechRendererFactory;
import org.tensin.sonos.tts.renderer.impl.SpeechRendererBlank;
import org.tensin.sonos.tts.renderer.impl.SpeechRendererCIFS;

/**
 * The Class SonosSpeech.
 */
public class SonosTTS {

    /**
     * Builds the.
     *
     * @param language the language
     * @param text the text
     * @return the sonos speech
     */
    public static SonosTTS build(final String language, final String text) {
        return build(Language.parse(language), text);
    }

    /**
     * Builds the.
     *
     * @param language the language
     * @param text the text
     * @return the sonos tts
     */
    public static SonosTTS build(final Language language, final String text) {
        final SonosTTS result = new SonosTTS();
        result.languageCode = language;
        result.text = text;
        return result;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws SonosException the sonos exception
     */
    public static void main(String[] args) throws SonosException {
        LogHelper.initLoggers();
        final SonosTTS speech = SonosTTS.build("fr", "Ceci est un texte très très long et même plus que çà");
        speech.useVoiceBuilder(VoiceBuilderType.GOOGLE_V1_CACHED_TTS);
        // speech.useRenderer(RendererType.LOCAL);
        speech.useRenderer(SpeechRendererCIFS.build("\\\\192.168.8.4\\Z\\", "\\\\JUPITER\\Z\\"));
        speech.render("chambre");
    }
    
    /** The Constant LOGGER. */
    // private static final Logger LOGGER = LogManager.getLogger();
    
    /** The language code. */
    private Language languageCode = Language.AUTO;
    
    /** The text. */
    private String text;
    
    /** The voice builder. */
    private IVoiceBuilder voiceBuilder = new VoiceBuilderGoogleTTS();
    
    /** The speech renderer. */
    private ISpeechRenderer speechRenderer = new SpeechRendererBlank();
    
    /**
     * Render.
     *
     * @param zones the zones
     * @throws SonosException the sonos exception
     */
    public void render(final String... zones) throws SonosException {
        final long start = System.currentTimeMillis();
        try {
            speechRenderer.init();
            speechRenderer.render(voiceBuilder.retrieve(languageCode, text), zones);
        } finally {
            speechRenderer.shutdown();
            voiceBuilder.close();
            // LOGGER.info("Voice text [" + text + "] in [" + languageCode + "] rendered in [" + (System.currentTimeMillis() - start) + "ms] on renderer [" + speechRenderer.getType() + "]");
        }
    }
    
    /**
     * Render to.
     *
     * @param renderer the renderer
     * @throws SonosException the sonos exception
     */
    public void useRenderer(ISpeechRenderer renderer) throws SonosException {
        speechRenderer = renderer;
    }
    
    /**
     * Render to.
     *
     * @param type the type
     * @throws SonosException the sonos exception
     */
    public void useRenderer(final RendererType type) throws SonosException {
        useRenderer(SpeechRendererFactory.buildRenderer(type));
    }

    /**
     * Use voice builder.
     *
     * @param voiceBuilder the voice builder
     * @throws SonosException the sonos exception
     */
    public void useVoiceBuilder(final IVoiceBuilder voiceBuilder) throws SonosException {
        this.voiceBuilder = voiceBuilder;
    }
    
    /**
     * Use voice builder.
     *
     * @param type the type
     * @throws SonosException the sonos exception
     */
    private void useVoiceBuilder(final VoiceBuilderType type) throws SonosException {
        useVoiceBuilder(VoiceBuilderFactory.buildVoiceBuilder(type));
    }

    /**
     * Use voice builder cached.
     *
     * @param cachePath the cache path
     * @throws SonosException the sonos exception
     */
    public void useVoiceBuilderCached(final String cachePath) throws SonosException {
        useVoiceBuilder(new VoiceBuilderGoogleTTSCached(cachePath));
    }
}