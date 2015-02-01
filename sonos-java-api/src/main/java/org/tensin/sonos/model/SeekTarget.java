/*
 * Copyright 2008 David Wheeler
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
package org.tensin.sonos.model;

import org.tensin.sonos.control.AVTransportService;

/**
 * Specifies a target for the {@link AVTransportService#seek(SeekTarget)} method.
 * 
 * @see SeekTargetFactory
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class SeekTarget {

    /** The mode. */
    private final SeekMode mode;

    /** The target. */
    private final String target;

    /**
     * Instantiates a new seek target.
     * 
     * @param mode
     *            the mode
     * @param target
     *            the target
     */
    public SeekTarget(final SeekMode mode, final String target) {
        this.mode = mode;
        this.target = target;
    }

    /**
     * Gets the seek mode.
     * 
     * @return the seek mode
     */
    public String getSeekMode() {
        return mode.getModeString();
    }

    /**
     * Gets the target.
     * 
     * @return the target
     */
    public String getTarget() {
        return target;
    }

}
