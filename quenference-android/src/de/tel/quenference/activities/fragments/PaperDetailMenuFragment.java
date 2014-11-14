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
package de.tel.quenference.activities.fragments;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import de.tel.quenference.activities.R;
import de.tel.quenference.activities.fragments.dialogs.AuthRequiredDialogFragment;
import de.tel.quenference.db.entities.PaperEntity;
import java.util.Date;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public abstract class PaperDetailMenuFragment extends FavoriteMenuFragment {

  public static String TAG_PAPER_API_KEY = "API_KEY";
  protected PaperEntity paper;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.paper_detail_menu, menu);
    item = menu.findItem(R.id.action_favorite);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Date d = new Date();

    if (item.getItemId() == R.id.action_paper_detail) {
      if (d.getTime() < 1410645600000l) { //14th of September 2014 06:00 GMT+8
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Sorry, but this feature will only be available only on and after the 14th.");
        builder.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            dialog.dismiss();
          }
        });
        builder.show();
        return super.onOptionsItemSelected(item);
      }
      String key = getString(R.string.pref_api_key);
      SharedPreferences pref = getActivity().getSharedPreferences(PaperDetailMenuFragment.class.getName(), Context.MODE_PRIVATE);
      final String apiKey = pref.getString(key, null);
      if (apiKey == null) {
        DialogFragment auth_required = new AuthRequiredDialogFragment();
        auth_required.show(getActivity().getSupportFragmentManager(), AuthRequiredDialogFragment.TAG_AUTH_REQUIRED);
        Toast.makeText(getActivity(), "Authentication successful", Toast.LENGTH_LONG).show();

      } else {
        //TODO Paper detail view call

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How would you like to view it?")
                .setMessage("If you have a PDF viewer installed we recommend you download the file and view it on your phone.\nAlternatively it can be rendered by a Google service")
                .setPositiveButton("I have a PDF Viewer", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
            String url = String.format(getString(R.string.alert_paper_view_url_direct), paper.getId(), apiKey);
            Uri Download_Uri = Uri.parse(url);
            String fileName = paper.getTitle() + ".pdf";
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setTitle("Paper Download")
                    .setDescription("Getting the PDF for you")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);
            Toast.makeText(getActivity(), "Download starting! Check your notification area for the status", Toast.LENGTH_LONG).show();

          }
        })
                .setNegativeButton("View it through Google", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Bundle args = new Bundle();
            String url = String.format(getString(R.string.alert_paper_view_url), paper.getId(), apiKey);
            args.putString(WebviewFragment.ARG_WEBVIEW_FRAGMENT_URL, url);
            args.putString(WebviewFragment.ARG_WEBVIEW_FRAGMENT_TITLE, paper.getTitle());
            Fragment fragment = new WebviewFragment();
            fragment.setArguments(args);
            FragmentManager mgr = ((FragmentActivity) getActivity()).getSupportFragmentManager();
            Fragment old = mgr.findFragmentById(R.id.content_frame);
            FragmentTransaction trx = mgr.beginTransaction();
            if (old != null) {
              trx.remove(old);
            }

            trx.add(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
          }
        })
                .show();
      }
    }
    return super.onOptionsItemSelected(item);
  }
}
