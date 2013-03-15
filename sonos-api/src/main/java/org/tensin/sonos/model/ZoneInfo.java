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
 * An immutable data transfer object containing detailed information about the zone.
 * @author David WHEELER
 * @author Serge SIMON
 *
 */
public class ZoneInfo {

  /** The serial number. */
  private String serialNumber;
  
  /** The software version. */
  private String softwareVersion;
  
  /** The display software version. */
  private String displaySoftwareVersion;
  
  /** The hardware version. */
  private String hardwareVersion;
  
  /** The ip address. */
  private String ipAddress;
  
  /** The mac addres. */
  private String macAddres;
  
  /** The copyright info. */
  private String copyrightInfo;
  
  /** The extra info. */
  private String extraInfo;

  /**
   * Creates a new ZoneInfo object.
   *
   * @param serialNumber the serial number
   * @param softwareVersion the software version
   * @param displaySoftwareVersion the display software version
   * @param hardwareVersion the hardware version
   * @param ipAddress the ip address
   * @param macAddress the mac address
   * @param copyrightInfo the copyright info
   * @param extraInfo the extra info
   */
  public ZoneInfo(String serialNumber, 
      String softwareVersion, 
      String displaySoftwareVersion, 
      String hardwareVersion, 
      String ipAddress, 
      String macAddress, 
      String copyrightInfo, 
      String extraInfo) {
    this.serialNumber = serialNumber;
    this.softwareVersion = softwareVersion;
    this.displaySoftwareVersion = displaySoftwareVersion;
    this.hardwareVersion = hardwareVersion;
    this.ipAddress = ipAddress;
    this.macAddres = macAddress;
    this.copyrightInfo = copyrightInfo;
    this.extraInfo = extraInfo;
  }

  /**
   * Gets the copyright info.
   *
   * @return the copyright info
   */
  public String getCopyrightInfo() {
    return copyrightInfo;
  }

  /**
   * Gets the display software version.
   *
   * @return the display software version
   */
  public String getDisplaySoftwareVersion() {
    return displaySoftwareVersion;
  }

  /**
   * Gets the extra info.
   *
   * @return the extra info
   */
  public String getExtraInfo() {
    return extraInfo;
  }

  /**
   * Gets the hardware version.
   *
   * @return the hardware version
   */
  public String getHardwareVersion() {
    return hardwareVersion;
  }

  /**
   * Gets the ip address.
   *
   * @return the ip address
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * Gets the mac addres.
   *
   * @return the mac addres
   */
  public String getMacAddres() {
    return macAddres;
  }

  /**
   * Gets the serial number.
   *
   * @return the serial number
   */
  public String getSerialNumber() {
    return serialNumber;
  }

  /**
   * Gets the software version.
   *
   * @return the software version
   */
  public String getSoftwareVersion() {
    return softwareVersion;
  }
}
