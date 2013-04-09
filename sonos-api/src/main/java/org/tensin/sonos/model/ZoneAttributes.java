/*
 * Copyright 2007 David Wheeler
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An immutable data transfer object containing the name and icon (URI) for a
 * zone player.
 * 
 * TODO: this class could use a real getIcon() that loads from a file depending
 * on the icon URI?
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ZoneAttributes {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneAttributes.class);

    /** The name. */
    private final String name;

    /** The icon. */
    private final String icon;

    /**
     * Instantiates a new zone attributes.
     *
     * @param name the name
     * @param icon the icon
     */
    public ZoneAttributes(final String name, final String icon) {
        this.name = name;
        this.icon = icon;
        LOGGER.debug("Zone name: " + name + ", zone icon: " + icon);
    }

    /**
     * Gets the icon.
     *
     * @return a string representation of the URI of the icon for the Zone.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Gets the name.
     *
     * @return the name of the Zone.
     */
    public String getName() {
        return name;
    }
}
