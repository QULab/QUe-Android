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
package de.tel.quenference.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import de.tel.quenference.activities.R;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The AsyncJSONSender sends to an given URL via POST
 * some JSONObjects.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncJSONSender extends AsyncTask<JSONObject, Void, List<JSONObject>> {
  
  /**
   * The content type of the objects which will be send.
   */
  private final String CONTENT_TYPE = "application/json; charset=utf-8";
  
  
  /**
   * The content type header (key).
   */
  private static final String CONTENT_TYPE_KEY = "Content-Type";
  
  /**
   * The error log message.
   */
  private static final String ERROR_LOG_MSG = "JSON sending failed!\nStatuscode %d";
  
  /**
   * The url of the web service.
   */
  private String url;
  
  /**
   * The job which will be executed after sending the objects.
   */
  private PostExecuteJob job;
  
  /**
   * The progress dialog to show the user the progress.
   */
  private ProgressDialog progress;
  
  /**
   * The context of the activity which called the AsyncJSONSender.
   */
  private Context context;
  
  /**
   * The ctor of the AsyncJSONSender
   * @param url     the url of the web service
   * @param job     the job which will be executed after sending the objects 
   */
  public AsyncJSONSender(String url, PostExecuteJob job) {
    this.url = url;
    this.job = job;
  }
  
  
  /**
   * The ctor of the AsyncJSONSender with context to show a progess dialog
   * @param url     the url of the web service
   * @param job     the job which will be executed after sending the objects 
   * @param context to create a progress dialog
   */
  public AsyncJSONSender(String url, PostExecuteJob job, Context context) {
    this.url = url;
    this.job = job;
    this.progress = new ProgressDialog(context);
  }

  @Override
  protected void onPreExecute() {
    if (progress != null) {
      progress.setMessage(context.getString(R.string.progress));
      progress.show();
    }
    super.onPreExecute();
  }
  
  
  
  @Override
  protected List<JSONObject> doInBackground(JSONObject ... arg0) {
    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost(url);
    post.addHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
    List<JSONObject> result = new ArrayList<JSONObject>();
    if (arg0 != null) {
      for (int i = 0; i < arg0.length; i++) {
        try {
          JSONObject json = arg0[i];
          post.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
          HttpResponse response = client.execute(post);
          if (response == null || response.getStatusLine().getStatusCode() >= 400) {
            Log.e(AsyncJSONSender.class.getName(), String.format(ERROR_LOG_MSG, response.getStatusLine().getStatusCode()));
            job.doExeptionHandling(null);
          } else {
            HttpEntity entity = response.getEntity();
            if (entity != null && entity.getContentType().getValue().equals(CONTENT_TYPE)) {
              try {
                JSONObject object = new JSONObject(EntityUtils.toString(entity));
                result.add(object);
              } catch (JSONException ex) {
                Log.e(AsyncJSONSender.class.getName(), "JSONObject creation failed" ,ex);
              }
            }
          }
        } catch (ClientProtocolException ex) {
          Log.e(AsyncJSONSender.class.getName(), "Exception" ,ex);
          job.doExeptionHandling(ex);
        } catch (UnsupportedEncodingException ex) {
          Log.e(AsyncJSONSender.class.getName(), "Exception" ,ex);
          job.doExeptionHandling(ex);
        } catch (IOException ex) {
          Log.e(AsyncJSONSender.class.getName(), "Exception" ,ex);
          job.doExeptionHandling(ex);
        } 
      }
    }
    return result;
  }

  @Override
  protected void onPostExecute(List<JSONObject> result) {
    
    for (int i = 0; i < result.size(); i++) {
      Log.d(AsyncJSONSender.class.getName(), result.get(i).toString());
      if (job != null)
        job.doJob(result.get(i));
    }
    if (progress != null)
      progress.dismiss();
    
    
    job.doFinalJob();
    super.onPostExecute(result);
  }
  
  /**
   * Represents an job which will be executed after sending the objects to
   * the web service.
   */
  public interface PostExecuteJob {
    
    /**
     * The job which will be done after the send was successful.
     * 
     * @param jsonResult    the result of the sending
     */
    public void doJob(JSONObject jsonResult);
    
    /**
     * The job which will be done after an error appears.
     * 
     * @param t       the throwable which cause the error
     */
    public void doExeptionHandling(Throwable t);
    
    /**
     * These job/method is called no matter if an exception or error appears
     * or the task was successfully. 
     * Can be used to add some other stuff like clean up or something else.
     * 
     */
    public void doFinalJob();
  }
  
}
