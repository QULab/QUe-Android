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
import android.os.Build;
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
 * Represents the paper password dialog, which is used for the authentication
 * via a paper password.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperPasswordDialogFragment extends DialogFragment {
  
  /**
   * The tag for the paper password dialog.
   */
  public static final String TAG_PAPER_PW = "PAPER_PW";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View v = inflater.inflate(R.layout.alert_paper_password, null);
    setOnClickListenerToSubmitButton(v);
    setOnClickListenerToCancelButton(v);
    return builder.setView(v).create();
  }

  /**
   * Set the onClickListener for the submit button,
   * which submit the authentication for the paper password dialog.
   * 
   * @param root        the root view, which contains the submit button
   */
  public void setOnClickListenerToSubmitButton(View root) {
    Button submit = (Button) root.findViewById(R.id.alert_paper_password_submit);
    final EditText emailText = (EditText) root.findViewById(R.id.alert_paper_password_login);
    final EditText pwText = (EditText) root.findViewById(R.id.alert_paper_password_pw);

    submit.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        String url = getString(R.string.alert_paper_password_url);
        AsyncJSONSender sender = new AsyncJSONSender(url, new AsyncJSONSender.PostExecuteJob() {
          private boolean failed = false;

          public void doJob(JSONObject jsonResult) {
            //TODO paper view call
            String api = getString(R.string.pref_api_key);
            try {
              SharedPreferences pref = getActivity().getSharedPreferences(PaperDetailMenuFragment.class.getName(), Context.MODE_PRIVATE);
              pref.edit().putString(api, jsonResult.getString(api)).apply();
            } catch (JSONException ex) {
              Log.e(PasswordDialogFragment.class.getName(), "api_key does not exists in JSON Result", ex);
            }
            Toast.makeText(getActivity(), "Registered finished!!!", Toast.LENGTH_LONG).show();
          }

          public void doExeptionHandling(Throwable t) {
            if (t != null) {
              PaperPasswordDialogFragment.this.getDialog().cancel();
            }

            failed = true;
          }

          public void doFinalJob() {
            if (failed) {
              if (getActivity() != null) {
                Toast.makeText(getActivity(), getString(R.string.alert_pw_register_failed), Toast.LENGTH_LONG).show();
              }
            } else {
              PaperPasswordDialogFragment.this.getDialog().cancel();
            }
          }
        });

        String os = String.format(getString(R.string.alert_email_device_os), Build.VERSION.RELEASE);
        PaperPasswordPayload payload = new PaperPasswordPayload(emailText.getText().toString(),
                os,
                Build.MODEL,
                pwText.getText().toString());
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
  public void setOnClickListenerToCancelButton(View root) {
    Button cancel = (Button) root.findViewById(R.id.alert_paper_password_cancel_button);
    cancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        PaperPasswordDialogFragment.this.getDialog().cancel();
      }
    });
  }
}
