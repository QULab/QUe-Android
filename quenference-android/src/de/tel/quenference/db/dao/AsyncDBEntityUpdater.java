/*
 * QUe
 * 
 * Copyright (c) 2014 Quality and Usability Lab,
 * Telekom Innvation Laboratories, TU Berlin. All rights reserved.
 * https://github.com/QULab/QUe-Android
 * 
 * This file is part of QUe.
 * 
 * QUe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * QUe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with QUe. If not, see <http://www.gnu.org/licenses/>.
 */
package de.tel.quenference.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.ConferenceDBHelper;

/**
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncDBEntityUpdater extends AsyncTask<Context, Void, Integer> {
  private ContentValues values;
  private String selection;
  private String selectionArgs[];
  private String tableName;

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
