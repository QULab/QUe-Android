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
 * Represents the password payload which will be send to the web service
 * for the authentication.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class RegisteredPasswordPayload extends Payload {
  
  /**
   * The password for the authentication.
   */
  private String password;

  /**
   * The ctor which create the password payload object.
   * 
   * @param password    the password
   */
  public RegisteredPasswordPayload(String password) {
    this.password = password;
  }
  
  /**
   * Returns the password for the authentication.
   * 
   * @return        the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Replaces the password for the authentication.
   * 
   * @param password  the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "{" + "\"password\"=\"" + password + "\"}";
  }
  
  
}
