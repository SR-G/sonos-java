/*
 * Copyright 2007 David Wheeler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.model;

/**
 * UPNP specified modes of play. TODO check this against Sonos modes
 * possibilities.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public enum PlayMode {
  
  /** The normal. */
  NORMAL, 
  
  /** The shuffle. */
  SHUFFLE,
  
  /** The shuffle norepeat. */
  SHUFFLE_NOREPEAT,
  
  /** The repeat one. */
  REPEAT_ONE, 
  
  /** The repeat all. */
  REPEAT_ALL, 
  
  /** The random. */
  RANDOM, 
  
  /** The DIREC t_1. */
  DIRECT_1, 
  
  /** The intro. */
  INTRO;
}
