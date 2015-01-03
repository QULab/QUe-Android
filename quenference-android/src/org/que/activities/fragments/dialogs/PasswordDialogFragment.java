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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.que.activities.R;
import org.que.activities.fragments.PaperDetailMenuFragment;
import org.que.async.AsyncJSONSender;

/**
 * Represents the password submission dialog after the
 * email authentication was chosen.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PasswordDialogFragment extends DialogFragment {

  /**
   * The tag for the password submission dialog.
   */
  public static final String TAG_PASSWORD_DIALOG = "PASSWORD_DIALOG";
  
  /**
   * The api key which will be used to unlock the paper for the user.
   */
  private String key;

  /**
   * The default ctor.
   */
  public PasswordDialogFragment() {
  }

  /**
   * The ctor which get the api key as parameter to construct the password submission
   * dialog object.
   * 
   * @param key       the api key
   */
  public PasswordDialogFragment(String key) {
    this.key = key;
  }
  
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    View v = inflater.inflate(R.layout.alert_type_password, null);
    setOnClickListenerToSignInButton(v);
    setOnClickListenerToCancelButton(v);
    
    return builder.setView(v).create();
  }

  /**
   * Set the onClicklistener for the sign in button,
   * which is used to authenticate with the given password which was send by email.
   * 
   * @param root            the root view, which contains the sign in button
   */
  private void setOnClickListenerToSignInButton(View root) {
    Button submit = (Button) root.findViewById(R.id.alert_password_sign_in);
    final EditText pwText = (EditText) root.findViewById(R.id.alert_password);
    
    submit.setOnClickListener(new View.OnClickListener() {
      
      public void onClick(View v) {
        String url = String.format(getString(R.string.alert_password_url), key);
        AsyncJSONSender sender = new AsyncJSONSender(url, new AsyncJSONSender.PostExecuteJob() {
          
          private boolean failed = false;
          
          public void doJob(JSONObject jsonResult) {
            //TODO paper view call
            String api = getString(R.string.pref_api_key);
            try {
              SharedPreferences pref = getActivity().getSharedPreferences(PaperDetailMenuFragment.class.getName(), Context.MODE_PRIVATE);
              pref.edit().putString(api, jsonResult.getString(api)).apply();
            } catch (JSONException ex) {
              Log.e(PasswordDialogFragment.class.getName(), "api_key does not exists in JSON Result" ,ex);
            }
            Toast.makeText(getActivity(), "Registered finished!!!", Toast.LENGTH_LONG).show();
          }

          public void doExeptionHandling(Throwable t) {
            if (t != null)
              PasswordDialogFragment.this.getDialog().cancel();
            
            failed = true;
          }
          
          public void doFinalJob() {
            if (failed) {
              Toast.makeText(getActivity(), getString(R.string.alert_pw_register_failed), Toast.LENGTH_LONG).show();
            } else 
              PasswordDialogFragment.this.getDialog().cancel();
          }

          public boolean isFailed() {
            return failed;
          }
        });
        RegisteredPasswordPayload payload = new RegisteredPasswordPayload(pwText.getText().toString());
        sender.execute(payload.toJSON());

      }
    });
  }

  /**
   * Set the onClicklistener to the cancel button which will be used for the
   * canceling of the authentication process.
   *
   * @param root the root view, which contains the cancel button
   */
  private void setOnClickListenerToCancelButton(View root) {
    Button cancel = (Button) root.findViewById(R.id.alert_password_cancel_button);
    cancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        PasswordDialogFragment.this.getDialog().cancel();
      }
    });
  }
  
}
