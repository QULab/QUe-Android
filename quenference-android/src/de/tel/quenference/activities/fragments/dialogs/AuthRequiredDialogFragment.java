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

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import de.tel.quenference.activities.R;

/**
 * Represents the dialog which will be showed if the user is not authenticated
 * yet.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AuthRequiredDialogFragment extends DialogFragment {

  /**
   * The tag for the authentication dialog.
   */
  public static final String TAG_AUTH_REQUIRED = "AUTH_REQUIRED";

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View v = inflater.inflate(R.layout.alert_auth_required, null);
    setOnClickListenerToRegisteredButton(v);
    setOnClickListenerToPaperButton(v);
    setOnClickListenerToCancelButton(v);
    return builder.setView(v).create();
  }

  /**
   * Set the onClicklistener to the paper button which will be used for the
   * authentication via paper password.
   *
   * @param root the root view, which contains the paper button
   */
  private void setOnClickListenerToPaperButton(View root) {
    Button paperButton = (Button) root.findViewById(R.id.alert_auth_paper_button);
    paperButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        AuthRequiredDialogFragment.this.getDialog().cancel();
        DialogFragment paperDialog = new PaperPasswordDialogFragment();
        paperDialog.show(getActivity().getSupportFragmentManager(), PaperPasswordDialogFragment.TAG_PAPER_PW);
      }
    });
  }

  /**
   * Set the onClicklistener to the register button which will be used for the
   * authentication via email.
   *
   * @param root the root view, which contains the register button
   */
  private void setOnClickListenerToRegisteredButton(View root) {
    Button registeredButton = (Button) root.findViewById(R.id.alert_auth_registered_button);
    registeredButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        AuthRequiredDialogFragment.this.getDialog().cancel();
        DialogFragment emailFragment = new EmailDialogFragment();
        emailFragment.show(getActivity().getSupportFragmentManager(), EmailDialogFragment.TAG_EMAIL_DIALOG);
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
    Button cancel = (Button) root.findViewById(R.id.alert_auth_cancel_button);
    cancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View arg0) {
        AuthRequiredDialogFragment.this.getDialog().cancel();
      }
    });
  }
}
