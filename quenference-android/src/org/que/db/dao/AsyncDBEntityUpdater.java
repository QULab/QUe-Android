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
package org.que.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import org.que.db.ConferenceDBHelper;

/**
 * Represents the asynchronous entity updater which updates the given entity
 * on the database.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncDBEntityUpdater extends AsyncTask<Context, Void, Integer> {
  
  /**
   * The key-value pairs which contains the column names with the new values.
   */
  private ContentValues values;
  
  /**
   * The selection or also named where clause for the update.
   */
  private String selection;
  
  /**
   * The arguments for the selection.
   */
  private String selectionArgs[];
  
  /**
   * The table name of the updated entity/ies.
   */
  private String tableName;

  /**
   * The ctor which creates the asynchronous entity updater.
   * 
   * @param values      the values which should be updated
   * @param selection   the selection for which entities the update should takes effect
   * @param selectionArgs the arguments for the selection
   * @param tableName     the table name for the updated entity
   */
  public AsyncDBEntityUpdater(ContentValues values, String selection, String[] selectionArgs, String tableName) {
    this.values = values;
    this.selection = selection;
    this.selectionArgs = selectionArgs;
    this.tableName = tableName;
  }
  
  @Override
  protected Integer doInBackground(Context... arg0) {
    SQLiteDatabase db = new ConferenceDBHelper(arg0[0]).getWritableDatabase();
    return db.update(tableName, values, selection, selectionArgs);
  }

  @Override
  protected void onPostExecute(Integer result) {
    Log.d(AsyncDBEntityUpdater.class.getName(), "Update affected " + result + " rows.");
    super.onPostExecute(result);
  }
}
