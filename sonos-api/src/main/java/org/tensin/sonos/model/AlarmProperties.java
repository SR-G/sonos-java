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
 * An immutable data transfer object containing alarm properties.
 * @author David WHEELER
 * @author Serge SIMON
 *
 */
public class AlarmProperties {
  
  /** The alarm id. */
  private int alarmId;
  
  /** The group id. */
  private int groupId;
  
  /** The start time. */
  private long startTime;
  
  /**
   * Instantiates a new alarm properties.
   *
   * @param alarmId the alarm id
   * @param groupId the group id
   * @param startTime the start time
   */
  public AlarmProperties(int alarmId, int groupId, long startTime) {
    this.alarmId = alarmId;
    this.groupId = groupId;
    this.startTime = startTime;
  }

  /**
   * Gets the alarm id.
   *
   * @return the alarm id
   */
  public int getAlarmId() {
    return alarmId;
  }

  /**
   * Gets the group id.
   *
   * @return the group id
   */
  public int getGroupId() {
    return groupId;
  }

  /**
   * Gets the start time.
   *
   * @return the start time
   */
  public long getStartTime() {
    return startTime;
  }

}
