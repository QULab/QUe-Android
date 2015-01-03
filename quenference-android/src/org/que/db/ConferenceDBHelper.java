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
package org.que.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Represents the database helper class which is used to create and
 * upgrade/update the database on the smart phone.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class ConferenceDBHelper extends SQLiteOpenHelper {
  
  /**
   * The version of the database, must be increment if the database was changed.
   * If the version was changed automatically an upgrade or downgrade will be
   * executed.
   */
  public static final int DATABASE_VERSION = 1;
  
  /**
   * The database name which will be used for saving the database.
   */
  public static final String DATABASE_NAME = "conference.db";

  /**
   * The ctor to create the database helper for the conference application.
   * 
   * @param context       the application context which will be
   *                       used for the database management.
   */
  public ConferenceDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(ConferenceDBContract.SQL_CREATE_AUTHOR_TABLE);
    db.execSQL(ConferenceDBContract.SQL_CREATE_PAPER_TABLE);
    db.execSQL(ConferenceDBContract.SQL_CREATE_SESSION_TABLE);
    db.execSQL(ConferenceDBContract.SQL_CREATE_PAPER_AUTHORS_TABLE);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(ConferenceDBContract.SQL_DROP_AUTHOR_TABLE);
    db.execSQL(ConferenceDBContract.SQL_DROP_PAPER_TABLE);
    db.execSQL(ConferenceDBContract.SQL_DROP_SESSION_TABLE);
    db.execSQL(ConferenceDBContract.SQL_DROP_PAPER_AUTHORS_TABLE);
    onCreate(db);
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }
}
