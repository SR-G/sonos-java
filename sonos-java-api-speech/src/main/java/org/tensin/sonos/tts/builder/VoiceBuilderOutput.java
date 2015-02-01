package org.tensin.sonos.tts.builder;

import java.io.InputStream;

import org.tensin.sonos.tts.Language;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * The Class VoiceBuilderOutput.
 */
public class VoiceBuilderOutput {

    /**
     * Builds the.
     *
     * @param inputStream the input stream
     * @param language the language
     * @param text the text
     * @return the voice builder output
     */
    public static VoiceBuilderOutput build(final InputStream inputStream, final Language language, final String text) {
        final VoiceBuilderOutput result = new VoiceBuilderOutput();
        result.inputStream = inputStream;
        result.text = text;
        result.language = language;
        return result;
    }
    
    /**
     * Builds the.
     *
     * @param language the language
     * @param text the text
     * @return the voice builder output
     */
    public static VoiceBuilderOutput build(Language language, String text) {
        final VoiceBuilderOutput result = new VoiceBuilderOutput();
        result.text = text;
        result.language = language;
        return result;
    }
    
    /** The input stream. */
    private InputStream inputStream;
    
    /** The text. */
    private String text;

    /** The language. */
    private Language language;
    
    /**
     * Hash text.
     *
     * @return the string
     */
    public String getHash() {
        final HashFunction hf = Hashing.md5();
        final HashCode hc = hf.newHasher().putString(language.code(), Charsets.UTF_8).putString(text, Charsets.UTF_8).hash();            
        return hc.toString();
    }

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the input stream.
     *
     * @param inputStream the new input stream
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}