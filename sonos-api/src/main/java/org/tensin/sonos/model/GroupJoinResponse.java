/*
 * Copyright 2008 David Wheeler
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
 * The Class GroupJoinResponse.
 */
public class GroupJoinResponse {
  // TODO parse the currentTransportSettings
  /** The current transport settings. */
  private final String currentTransportSettings;
  
  /** The group uuid. */
  private final String groupUuid;
  
  /**
   * Instantiates a new group join response.
   *
   * @param currentTransportSettings the current transport settings
   * @param groupUuid the group uuid
   */
  public GroupJoinResponse(String currentTransportSettings, String groupUuid) {
    this.currentTransportSettings = currentTransportSettings;
    this.groupUuid = groupUuid;
  }

  /**
   * Gets the current transport settings.
   *
   * @return the current transport settings
   */
  public String getCurrentTransportSettings() {
    return currentTransportSettings;
  }

  /**
   * Gets the group uuid.
   *
   * @return the group uuid
   */
  public String getGroupUuid() {
    return groupUuid;
  }

}
