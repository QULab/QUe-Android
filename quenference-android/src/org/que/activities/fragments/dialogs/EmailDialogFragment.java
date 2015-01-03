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
import org.que.async.AsyncJSONSender;

/**
 * Represents the email authentication dialog for the paper view.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class EmailDialogFragment extends DialogFragment {
  
  /**
   * The tag for the email authentication dialog.
   */
  public static final String TAG_EMAIL_DIALOG = "EMAIL_DIALOG";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    View v = inflater.inflate(R.layout.alert_type_email, null);
    setOnClickListenerToSignInButton(v);
    setOnClickListenerToCancelButton(v);

    return builder.setView(v).create();
  }

  /**
   * Set the onClicklistener for the sign in button,
   * which is used to authenticate with the given email address.
   * 
   * @param root            the root view, which contains the sign in button
   */
  private void setOnClickListenerToSignInButton(View root) {
    Button signIn = (Button) root.findViewById(R.id.alert_email_sign_in);
    final EditText emailText = (EditText) root.findViewById(R.id.alert_email);

    signIn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String url = getString(R.string.alert_email_register_url);
        AsyncJSONSender sender = new AsyncJSONSender(url, new AsyncJSONSender.PostExecuteJob() {
          private boolean failed = false;

          public void doJob(JSONObject jsonResult) {
            //TODO password dialog
            String key = "";
            try {
              key = jsonResult.getString("key_id");
            } catch (JSONException ex) {
              Log.e(EmailDialogFragment.class.getName(), "key_id not exists in JSON Result", ex);
            }
            PasswordDialogFragment pwDialog = new PasswordDialogFragment(key);
            pwDialog.show(getActivity().getSupportFragmentManager(), PasswordDialogFragment.TAG_PASSWORD_DIALOG);
          }

          public void doExeptionHandling(Throwable t) {
            failed = true;
          }

          public void doFinalJob() {
            if (failed) {
              try {
                Toast.makeText(getActivity(), getString(R.string.alert_email_register_failed), Toast.LENGTH_LONG).show();
              } catch (IllegalStateException e) {
              }
            }
            try {
              EmailDialogFragment.this.getDialog().cancel();
            } catch (NullPointerException e) {
            }
          }

          public boolean isFailed() {
            return failed;
          }
        });
        String os = String.format(getString(R.string.alert_email_device_os), Build.VERSION.RELEASE);
        RegisteredEmailPayload payload = new RegisteredEmailPayload(emailText.getText().toString(),
                os,
                Build.MODEL);
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
    Button cancel = (Button) root.findViewById(R.id.alert_email_cancel_button);
    cancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        EmailDialogFragment.this.getDialog().cancel();
      }
    });
  }
}
