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
package org.que.async;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a async GET requester. For given URL's the AsyncTask will execute
 * GET requests and returns the result of each request as JSONObject in given
 * PostExecuteJob doJob method call. Means to executed the AsyncTask, the caller
 * needs to pass into the task the URL's to request and a PostExecuteJob Object
 * which will be used to return the values or used if an exception appears. If
 * the Task was successful the doJob Method will be called of the
 * PostExecutedJob, if not the doException will be called.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncGETRequester extends AsyncTask<GetRequestInfo, Void, List<JSONObject>> {

  /**
   * The content type of the objects which will be send.
   */
  private static final String CONTENT_TYPE = "application/json";
  /**
   * The error log message.
   */
  private static final String ERROR_LOG_MSG = "Oops, the HTTP-GET request failed! Statuscode: %d";
  /**
   * The exception log message.
   */
  private static final String EXECEPTION_LOG_MSG = "Exception!";
  /**
   * The job which will be executed after sending the objects.
   */
  private PostExecuteJob job;
  /**
   * The credentials for basic authentication.
   */
  private UsernamePasswordCredentials credentials;
  /**
   * The string for the If-None-Match Http-header.
   */
  private static final String HEADER_IF_NONE_MATCH = "If-None-Match";
  /**
   * The string for the Etag Http-header.
   */
  private static final String HEADER_ETAG = "Etag";

  /**
   * The ctor of the AsyncJSONSender
   *
   * @param job the job which will be executed after sending the objects
   */
  public AsyncGETRequester(PostExecuteJob job) {
    this.job = job;
  }

  /**
   * The ctor of the AsyncJSONSender
   *
   * @param job the job which will be executed after sending the objects
   * @param credentials the user credentials to make the get request
   */
  public AsyncGETRequester(PostExecuteJob job, UsernamePasswordCredentials credentials) {
    this.job = job;
    this.credentials = credentials;
  }

  @Override
  protected List<JSONObject> doInBackground(GetRequestInfo... urls) {
    List<JSONObject> result = new ArrayList<JSONObject>();
    if (urls != null) {
      for (int i = 0; i < urls.length; i++) {
        String url = urls[i].getUrl();
        if (url != null) {
          HttpGet get = new HttpGet(url);
          String etag = urls[i].getEtag();
          if (etag != null && !etag.isEmpty()) {
            get.setHeader(HEADER_IF_NONE_MATCH, etag);
          }

          if (credentials != null) {
            get.addHeader(BasicScheme.authenticate(credentials, HTTP.UTF_8, false));
          }
          executeGetRequest(get, result);
        }
      }
    }
    return result;
  }

  private void executeGetRequest(HttpGet get, List<JSONObject> result) {
    HttpClient client = new DefaultHttpClient();
    try {
      HttpResponse response = client.execute(get);
      int statusCode = response.getStatusLine().getStatusCode();
      if (response == null || statusCode >= 400) {
        Log.e(AsyncGETRequester.class.getName(), String.format(ERROR_LOG_MSG, response.getStatusLine().getStatusCode()));
        job.doExeptionHandling(null);
      } else {
        if (statusCode != 304) {
          HttpEntity entity = response.getEntity();
          if (entity != null && entity.getContentType().getValue().contains(CONTENT_TYPE)) {
            try {
              JSONObject object = new JSONObject(EntityUtils.toString(entity));
              result.add(object);
            } catch (JSONException ex) {
              Log.e(AsyncGETRequester.class.getName(), EXECEPTION_LOG_MSG, ex);
            }
          }
        }
        Header responseEtag = response.getFirstHeader(HEADER_ETAG);
        if (responseEtag != null) {
          String newEtag = responseEtag.getValue();
          if (newEtag != null &&
              !newEtag.equalsIgnoreCase(get.getFirstHeader(HEADER_IF_NONE_MATCH).getValue())) {
            job.handleNewEtag(get.getURI().toString(), newEtag);
          }
        }
      }
    } catch (IOException ex) {
      Log.e(AsyncGETRequester.class.getName(), EXECEPTION_LOG_MSG, ex);
      job.doExeptionHandling(ex);
    }
  }

  @Override
  protected void onPostExecute(List<JSONObject> result) {
    for (int i = 0; i < result.size(); i++) {
      if (job != null) {
        job.doJob(result.get(i));
      }
    }
    super.onPostExecute(result);
  }

  /**
   * Represents an job which will be executed after sending the objects to the
   * web service.
   */
  public interface PostExecuteJob {

    /**
     * The job which will be done after the send was successful.
     *
     * @param jsonResult the result of the sending
     */
    public void doJob(JSONObject response);

    /**
     * The job which will be done after an error appears.
     *
     * @param t the throwable which cause the error
     */
    public void doExeptionHandling(Throwable t);

    /**
     * Method should handle the new etag.
     *
     * @param oldEtag the corresponding url
     * @param newEtag the new etag
     */
    public void handleNewEtag(String url, String newEtag);
  }
}
