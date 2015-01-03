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

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the payload which will be send to the web service.
 * Contains a single method which uses the toString Method of the own
 * class to create a JSON representation of the object.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class Payload {
  
  /**
   * Creates with the result of the toString Method from the current
   * object a JSON representation object.
   * 
   * @return        the JSONObject
   */
  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    try {
      json = new JSONObject(this.toString());
    } catch (JSONException ex) {
      Log.e(Payload.class.getName(), "JSONObject creation failed" ,ex);
    }
    return json;
  }
}
