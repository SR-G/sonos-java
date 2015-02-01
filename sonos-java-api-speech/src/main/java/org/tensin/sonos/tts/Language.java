package org.tensin.sonos.tts;

import org.apache.commons.lang3.StringUtils;

/**
 * The Enum Language.
 */
public enum Language {

    /** The auto. */
    AUTO("auto"),
    
    /** The au english. */
    AU_ENGLISH("en-AU"),
    
    /** The us english. */
    US_ENGLISH("en-US"),
    
    /** The uk english. */
    UK_ENGLISH("en-GB"),
    
    /** The es spanish. */
    ES_SPANISH("es"),
    
    /** The fr french. */
    FR_FRENCH("fr"),
    
    /** The de german. */
    DE_GERMAN("de"),
    
    /** The pt portuguese. */
    PT_PORTUGUESE("pt-pt"),
    
    /** The pt brazilian. */
    PT_BRAZILIAN("pt-br");

    /** The code. */
    private final String code;

    /**
     * Instantiates a new language.
     *
     * @param code the code
     */
    private Language(final String code) {
        this.code = code;
    }
    
    /**
     * Code.
     *
     * @return the string
     */
    public String code() {
        return code;
    }
 
    /**
     * Value of.
     *
     * @param s the s
     * @return the language
     */
    public static Language parse(final String s) {
        for (final Language language : Language.class.getEnumConstants()) {
            if (StringUtils.equalsIgnoreCase(s, language.code())) {
                return language;
            }
        }
        return AUTO;
    }
}