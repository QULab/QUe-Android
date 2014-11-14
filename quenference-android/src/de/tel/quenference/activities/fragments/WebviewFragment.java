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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.tel.quenference.activities.R;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class WebviewFragment extends Fragment {

  public static final String ARG_WEBVIEW_FRAGMENT_URL = "webviewFragmentUrl";
  public static final String ARG_WEBVIEW_FRAGMENT_TITLE = "webviewFragmentTitle";
  private static final String TAG_WEBVIEW_URL = "webViewUrl";
  private static final String TAG_WEBVIEW_TITLE = "webViewTitle";
  public static final String TAG_WEBVIEW_FRAGMENT_HTML_CODE = "webViewFragmentHTML";
  public static final String ARG_WEBVIEW_FRAGMENT_HTML_CODE = "webViewFragmentHTMLCODE";
  private String url;
  private String title;
  private WebView content;
  private String htmlCode;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (null == savedInstanceState) {
      savedInstanceState = getArguments();
    }

    if (null != savedInstanceState) {
      url = savedInstanceState.getString(TAG_WEBVIEW_URL);
      title = savedInstanceState.getString(TAG_WEBVIEW_TITLE);
      htmlCode = savedInstanceState.getString(ARG_WEBVIEW_FRAGMENT_HTML_CODE);
    }

    if (null == url) {
      url = savedInstanceState.getString(ARG_WEBVIEW_FRAGMENT_URL);
      title = savedInstanceState.getString(ARG_WEBVIEW_FRAGMENT_TITLE);
    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
    if (url != null && !url.isEmpty()) {
      content = ((WebView) rootView.findViewById(R.id.webview));
      //WebViewClient is enabled in order to force all links to load within the webview and also
      // in order to enable JavaScript
      content.setWebViewClient(new HelloWebViewClient());
      WebSettings webSettings = content.getSettings();
      webSettings.setJavaScriptEnabled(true);
      webSettings.setBuiltInZoomControls(true);
      webSettings.setSupportZoom(true);
      content.loadUrl(url);
    } else if (htmlCode != null) {
      System.out.println("trying to render HTML Code");
      content = ((WebView) rootView.findViewById(R.id.webview));

      //WebViewClient is enabled in order to force all links to load within the webview and also
      // in order to enable JavaScript
      content.setWebViewClient(new HelloWebViewClient());
      WebSettings webSettings = content.getSettings();
      webSettings.setJavaScriptEnabled(true);
      webSettings.setBuiltInZoomControls(true);
      webSettings.setSupportZoom(true);
      content.loadData(htmlCode, "text/html", null);
    }

    getActivity().setTitle(title);
    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(TAG_WEBVIEW_URL, url);
    outState.putString(TAG_WEBVIEW_TITLE, title);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  //Webviewclient is only so the URL loaded is always loaded and the picker is never shown (for google maps f.e.)
  public class HelloWebViewClient extends WebViewClient {

    public HelloWebViewClient() {
      // do nothing
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      // TODO Auto-generated method stub
      super.onPageFinished(view, url);
    }
  }
}
