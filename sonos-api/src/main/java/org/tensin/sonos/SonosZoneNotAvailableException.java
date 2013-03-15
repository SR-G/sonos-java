/*
 * Copyright 2009 David Wheeler
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos;

/**
 * The Class ZoneNotAvailableException.
 */
public class SonosZoneNotAvailableException extends Exception {

    /** serialVersionUID. */
    private static final long serialVersionUID = 5644065070482688190L;

    /**
     * Instantiates a new zone not available exception.
     */
    public SonosZoneNotAvailableException() {
        super();
    }

    /**
     * Instantiates a new zone not available exception.
     * 
     * @param message
     *            the message
     */
    public SonosZoneNotAvailableException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new zone not available exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public SonosZoneNotAvailableException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new zone not available exception.
     * 
     * @param cause
     *            the cause
     */
    public SonosZoneNotAvailableException(final Throwable cause) {
        super(cause);
    }

}
