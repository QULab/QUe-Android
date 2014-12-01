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
package de.tel.quenference.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import de.tel.quenference.db.ConferenceDBHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the AsyncDBListReader which reads asynchronous from the database a
 * list as result of a SQL Query.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncDBListReader extends AsyncTask<Context, Void, List> {

  /**
   * The list which contains the elements after the SQL query was executed.
   */
  private List values;
  
  /**
   * The extractor which will be used to extract the values from the result set.
   */
  private CursorExtracting extract;
  
  /**
   * The job which will be executed after the SQL query was executed.
   */
  private PostExecuteJob postJob;

  /**
   * Contains all necessary informations for the SQL request.
   */
  private SQLQuery query;
  
  /** 
   * The ctor to create the AsyncDBListReader object to read
   * asynchronous from the database a list as result of the given SQL Query. 
   * 
   * @param values              the list which will be used to save the result
   * @param query               the query which contains all necessary informations
   * @param extract             the extractor which will be used to extract the values from the result set
   * @param postJob             the job which will be executed after the SQL query was executed
   */
  public AsyncDBListReader(List values, SQLQuery query,
          CursorExtracting extract, PostExecuteJob postJob) {
    this.values = values;
    this.query = query;
    this.extract = extract;
    this.postJob = postJob;
  }

  @Override
  protected List doInBackground(Context... context) {
    if (extract == null || query == null
            || context == null || context.length == 0) {
      throw new IllegalStateException();
    }

    if (values == null) {
      values = new ArrayList();
    }

    final ConferenceDBHelper DB_HELPER = new ConferenceDBHelper(context[0]);
    SQLiteDatabase db = DB_HELPER.getReadableDatabase();
    Cursor c = db.query(query.getSelectedEntity().toString(), 
                        query.getRequestedColumns(),
                        query.getSelection(),
                        query.getSelectionArgs(),
                        query.getGroupBy(), 
                        query.getHaving(), 
                        query.getOrderBy());
    c.moveToFirst();
    while (!c.isAfterLast()) {
      Object o = extract.extract(c);
      values.add(o);
      c.moveToNext();
    }
    c.close();
    DB_HELPER.close();

    return values;
  }

  @Override
  protected void onPostExecute(List result) {
    super.onPostExecute(result);
    postJob.doJob(result);
  }

  /**
   * Represents the PostExecuteJob which will be used to execute a job/task after
   * the SQL query was executed.
   */
  public interface PostExecuteJob {

    /**
     * The job which will be executed.
     * 
     * @param result      the result from the executed SQL query
     */
    public void doJob(List result);
  }
}
