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
package de.tel.quenference.activities.fragments.dialogs;

/**
 * The paper password which is used for the authentication for the paper view.
 * Contains the email, os, device and password of the user. These
 * informations are used as payload and send to the web service to authenticate
 * the user.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperPasswordPayload extends Payload {
  
  /**
   * The email of the user.
   */
  private String email;
  
  /**
   * The operation system of the device.
   */
  private String os;
  
  /**
   * The device of the user.
   */
  private String device;
  
  /**
   * The paper password for the authentication.
   */
  private String password;

  /**
   * The ctor to create a paper password paylod object.
   * 
   * @param email     the email of the user
   * @param os        the operating system of the device
   * @param device    the device of the user
   * @param password  the paper password
   */
  public PaperPasswordPayload(String email, String os, String device, String password) {
    this.email = email;
    this.os = os;
    this.device = device;
    this.password = password;
  }

  /**
   * Returns the email of the user.
   * 
   * @return        the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Replaces the email of the user
   * @param email     the email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the operation system of the device.
   * 
   * @return          the os
   */
  public String getOs() {
    return os;
  }

  /**
   * Replaces the operating system of the device.
   * 
   * @param os        the os
   */
  public void setOs(String os) {
    this.os = os;
  }

  /**
   * Returns the device of the user.
   * 
   * @return          the device
   */
  public String getDevice() {
    return device;
  }

  /**
   * Replaces the device of the user.
   * 
   * @param device    the device
   */
  public void setDevice(String device) {
    this.device = device;
  }

  /**
   * Returns the paper password.
   * 
   * @return          the paper password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Replaces the paper password.
   * 
   * @param password  the paper password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "{" + "\"email\"=\"" + email 
            + "\", \"os\"=\"" + os 
            + "\", \"device\"=\"" 
            + device + "\", \"password\"=\"" 
            + password + "\"}";
  }
  
}
