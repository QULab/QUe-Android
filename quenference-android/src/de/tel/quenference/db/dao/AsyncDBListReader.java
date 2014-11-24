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

//TODO replace fields with SQLQuery class

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
   * The columns which are requested in the SQL query.
   */
  private String[] columns;
  
  /**
   * The table name on which the query will be executed.
   */
  private String tableName;
  
  /**
   * The extractor which will be used to extract the values from the result set.
   */
  private CursorExtracting extract;
  
  /**
   * The job which will be executed after the SQL query was executed.
   */
  private PostExecuteJob postJob;
  
  //================================================================
  //============================OPTIONAL============================
  //================================================================
  
  /**
   * The selection or also named where clause.
   */
  private String selection;
  
  /**
   * The arguments for the where clause.
   */
  private String[] selectionArgs;
  
  /**
   * The group by clause for the SQL Statement.
   */
  private String groupBy;
  
  /**
   * The having clause for the SQL Statement.
   */
  private String having;
  
  /**
   * The order by clause for the SQL Statement.
   */
  private String orderBy;

  /** 
   * The ctor to create the AsyncDBListReader object to read
   * asynchronous from the database a list as result of the given SQL Query. 
   * 
   * @param values              the list which will be used to save the result
   * @param columns             the columns which are requested in the SQL query
   * @param tableName           the table name on which the query will be executed.
   * @param extract             the extractor which will be used to extract the values from the result set
   * @param postJob             the job which will be executed after the SQL query was executed
   */
  public AsyncDBListReader(List values, String[] columns, String tableName,
          CursorExtracting extract, PostExecuteJob postJob) {
    this.values = values;
    this.columns = columns;
    this.tableName = tableName;
    this.extract = extract;
    this.postJob = postJob;
  }

  /**   
   * The ctor to create the AsyncDBListReader object to read
   * asynchronous from the database a list as result of the given SQL Query. 
   * 
   * @param values              the list which will be used to save the result
   * @param columns             the columns which are requested in the SQL query
   * @param tableName           the table name on which the query will be executed.
   * @param extract             the extractor which will be used to extract the values from the result set
   * @param postJob             the job which will be executed after the SQL query was executed
   * @param selection           the selection or also named where clause
   * @param selectionArgs       the arguments for the where clause
   * @param groupBy             the group by clause for the SQL Statement
   * @param having              the having clause for the SQL Statement
   * @param orderBy             the order by clause for the SQL Statement
   */
  public AsyncDBListReader(List values, String[] columns, String tableName,
          CursorExtracting extract, PostExecuteJob postJob, String selection,
          String[] selectionArgs, String groupBy, String having, String orderBy) {
    this.values = values;
    this.columns = columns;
    this.tableName = tableName;
    this.extract = extract;
    this.selection = selection;
    this.selectionArgs = selectionArgs;
    this.groupBy = groupBy;
    this.having = having;
    this.orderBy = orderBy;
    this.postJob = postJob;
  }

  @Override
  protected List doInBackground(Context... context) {
    if (columns == null || extract == null || tableName == null
            || context == null || context.length == 0) {
      throw new IllegalStateException();
    }

    if (values == null) {
      values = new ArrayList();
    }

    final ConferenceDBHelper DB_HELPER = new ConferenceDBHelper(context[0]);
    SQLiteDatabase db = DB_HELPER.getReadableDatabase();
    Cursor c = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
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
