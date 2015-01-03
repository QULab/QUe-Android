/*
 * Copyright 2014 Quality and Usability Lab, Telekom Innvation Laboratories, TU Berlin..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.que.activities.fragments.dialogs;

/**
 * Represents the email payload for the authentication,
 * which will be send to the web service.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class RegisteredEmailPayload extends Payload {
  
  /**
   * The email address of the user.
   */
  private String email;
  
  /**
   * The operating system of the device.
   */
  private String os;
  
  /**
   * The device of the user.
   */
  private String device;

  /**
   * The ctor to create the email payload object.
   * 
   * @param email   the email of the user
   * @param os      the operating system of the device
   * @param device  the device of the user
   */
  public RegisteredEmailPayload(String email, String os, String device) {
    this.email = email;
    this.os = os;
    this.device = device;
  }

  /**
   * Returns the email address of the user.
   * 
   * @return        the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Replaces the email address of the user.
   * 
   * @param email   the email address
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the operating system of the device.
   * 
   * @return        the operation system
   */
  
  public String getOs() {
    return os;
  }

  /**
   * Replaces the operating system of the device.
   * 
   * @param os      the operating system
   */
  public void setOs(String os) {
    this.os = os;
  }

  /**
   * Returns the device of the user.
   * 
   * @return        the device
   */
  public String getDevice() {
    return device;
  }

  /**
   * Replaces the device of the user.
   * 
   * @param device  the device
   */
  public void setDevice(String device) {
    this.device = device;
  }

  @Override
  public String toString() {
    return "{" + "\"email\":\"" + email 
            + "\", \"os\"=\"" + os 
            + "\", \"device\"=\"" + device 
            + "\"}";
  }
  
}
