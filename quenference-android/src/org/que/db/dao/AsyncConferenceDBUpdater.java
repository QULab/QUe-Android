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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.que.db.ConferenceDBContract;
import org.que.db.ConferenceDBHelper;

/**
 * Represents the database updater which updates the database asynchronous.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncConferenceDBUpdater extends AsyncTask<JSONObject, Void, Boolean> {

  /**
   * The error message if the json extraction failed.
   */
  private static final String ERROR_MSG_JSON_EXTRACT = "JSON EXTRACT FAILED!";
  /**
   * The error message if the insertion failed.
   */
  private static final String ERROR_MSG_INSERT_FAILED = "Insert failed, try update...";
  
  /**
   * The complete message indicates that the update was completed.
   */
  private static final String COMPLETE_MSG_UPDATE_DB = "Database update completed";
  /**
   * The id to identify the values.
   */
  private static final String VALUES_ID = "id";
  /**
   * The json key of the meta data.
   */
  private static final String UNUSED_JSON_META = "meta";
  /**
   * The database helper of the application which is used to update the
   * database.
   */
  private ConferenceDBHelper dBHelper;

  /**
   * The ctor to create the asynchronous database updater.
   *
   * @param dbHelper the database helper which is used for the update
   */
  public AsyncConferenceDBUpdater(ConferenceDBHelper dbHelper) {
    this.dBHelper = dbHelper;
  }

  /**
   * The processing to extract the session for the given json. The ContentValues
   * object contains for each column of the session table the corresponding
   * value from the json.
   *
   * @param json the json which contains the session
   * @return the extracted session in a ContentValues object
   */
  private ContentValues extractSession(JSONObject json) {
    ContentValues values = new ContentValues();
    try {
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID,
              json.getInt(ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_DAY,
              json.getInt(ConferenceDBContract.ConferenceSession.COLUMN_NAME_DAY));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_TYPE,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_TYPE));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_TYPE_NAME,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_TYPE_NAME));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_CODE,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_CODE));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_LENGTH,
              json.getInt(ConferenceDBContract.ConferenceSession.COLUMN_NAME_LENGTH));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_ROOM,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_ROOM));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_CHAIR,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_CHAIR));
      values.put(ConferenceDBContract.ConferenceSession.COLUMN_NAME_CO_CHAIR,
              json.getString(ConferenceDBContract.ConferenceSession.COLUMN_NAME_CO_CHAIR));
    } catch (JSONException ex) {
      Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
    }
    return values;
  }

  /**
   * The processing to extract the paper for the given json. The ContentValues
   * object contains for each column of the paper table the corresponding value
   * from the json.
   *
   * @param json the json which contains the paper
   * @return the extracted paper in a ContentValues object
   */
  private ContentValues extractPaper(JSONObject json) {
    ContentValues values = new ContentValues();
    try {
      //ID
      int id = json.getInt(ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID);
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID, id);
      //SUBMISSION ID
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_SUBMISSION_ID,
              json.getInt(ConferenceDBContract.ConferencePaper.COLUMN_NAME_SUBMISSION_ID));
      // TITLE
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_TITLE,
              json.getString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_TITLE));
      //ABSTRACT
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_ABSTRACT,
              json.optString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_ABSTRACT));
      //
      JSONArray authors = json.getJSONArray(ConferenceDBContract.ConferenceAuthor.TABLE_NAME + "s");
      if (authors.length() > 0) {
        JSONObject mainAuthor = authors.getJSONObject(0);
        values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR,
                mainAuthor.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME)
                + " "
                + mainAuthor.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME));
        values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID,
                mainAuthor.getInt(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID));
      }
      JSONObject session = json.getJSONObject(ConferenceDBContract.ConferencePaper.COLUMN_NAME_SESSION);

      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_SESSION,
              session.getInt(ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID));
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_PAPER_CODE,
              json.getString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_PAPER_CODE));
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_DATETIME,
              json.getString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_DATETIME));
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_DATETIME_END,
              json.getString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_DATETIME_END));
      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_KEYWORDS,
              json.getString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_KEYWORDS));
    } catch (JSONException ex) {
      Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
    }
    return values;
  }

  /**
   * The processing to extract the author for the given json. The ContentValues
   * object contains for each column of the author table the corresponding value
   * from the json.
   *
   * @param json the json which contains the author
   * @return the extracted author in a ContentValues object
   */
  private ContentValues extractAuthor(JSONObject json) {
    ContentValues values = new ContentValues();
    try {
      values.put(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID,
              json.getInt(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID));
      values.put(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME,
              json.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME));
      values.put(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME,
              json.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME));
      values.put(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_AFFILIATION,
              json.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_AFFILIATION));
    } catch (JSONException ex) {
      Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
    }
    return values;
  }

  /**
   * The processing to extract and save the paper authors for the given json and
   * paper. For each author in the json the paper and author are saved in the
   * relation paper-authors table.
   *
   * @param db the database object which will be used to save the entries
   * @param json the json which contains the author
   * @param paper the paper for which the authors are saved
   * @return the paper and his main author
   */
  private ContentValues savedPaperAuthors(SQLiteDatabase db, JSONObject json, ContentValues paper) {
    Iterator it = json.keys();
    JSONArray authors = null;
    if (it.hasNext()) {
      while (it.hasNext()) {
        String key = it.next().toString();
        if (key.equals(ConferenceDBContract.ConferenceAuthor.TABLE_NAME + "s")) {
          try {
            authors = json.getJSONArray(key);
          } catch (JSONException ex) {
            Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
          }
        }
      }
      if (authors != null) {
        int size = authors.length();
        try {
          JSONObject author = authors.getJSONObject(0);
          paper.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR,
                  author.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME)
                  + " " + author.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME));
          paper.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID,
                  author.getInt(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID));
        } catch (JSONException ex) {
          Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
        }
        if (size > 0) {
          for (int i = 0; i < size; i++) {
            try {
              JSONObject author = authors.getJSONObject(i);
              int authprID = author.getInt(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID);
              int paperID = paper.getAsInteger(ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID);
              ContentValues value = new ContentValues();
              value.put(ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID, authprID);
              value.put(ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_PAPER_ID, paperID);
              db.insert(ConferenceDBContract.ConferencePaperAuthors.TABLE_NAME, null, value);
            } catch (JSONException ex) {
              Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
            }
          }
        }
      }
    }
    return paper;
  }

  @Override
  protected Boolean doInBackground(JSONObject... args) {
    if (args != null && args.length > 0) {
      JSONObject json = args[0];
      if (json == null) {
        return false;
      }
      Iterator iterator = json.keys();
      String arrayKey = "";
      JSONArray responseValues = null;
      if (iterator != null && iterator.hasNext()) {
        try {
          arrayKey = (String) iterator.next();
          if (arrayKey.equalsIgnoreCase(UNUSED_JSON_META)) {
            if (iterator.hasNext()) {
              arrayKey = (String) iterator.next();
            }
          }

          if (!arrayKey.equalsIgnoreCase(UNUSED_JSON_META)) {
            responseValues = json.getJSONArray(arrayKey);
          }
          updateValues(arrayKey, responseValues);
        } catch (JSONException ex) {
          Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
        }
      }
      Log.d(AsyncConferenceDBUpdater.class.getName(),COMPLETE_MSG_UPDATE_DB);
      return true;
    } else {
      return false;
    }
  }

  private void updateValues(String arrayKey, JSONArray responseValues) throws JSONException {
    if (responseValues != null) {
      SQLiteDatabase db = dBHelper.getWritableDatabase();
      for (int i = 0; i < responseValues.length(); i++) {
        ContentValues values = new ContentValues();
        String table = "";
        if (arrayKey.equalsIgnoreCase(ConferenceDBContract.ConferenceAuthor.TABLE_NAME + "s")) {
          values = extractAuthor(responseValues.getJSONObject(i));
          table = ConferenceDBContract.ConferenceAuthor.TABLE_NAME;
        }
        if (arrayKey.equalsIgnoreCase(ConferenceDBContract.ConferencePaper.TABLE_NAME + "s")) {
          values = extractPaper(responseValues.getJSONObject(i));
          table = ConferenceDBContract.ConferencePaper.TABLE_NAME;
          values = savedPaperAuthors(db, responseValues.getJSONObject(i), values);
        }
        if (arrayKey.equalsIgnoreCase(ConferenceDBContract.ConferenceSession.TABLE_NAME + "s")) {
          values = extractSession(responseValues.getJSONObject(i));
          table = ConferenceDBContract.ConferenceSession.TABLE_NAME;
        }
        if (values != null && values.size() > 0 && !table.isEmpty()) {
          try {
            db.insertOrThrow(table, null, values);
          } catch (SQLException sqlEx) {
            Log.d(AsyncConferenceDBUpdater.class.getName(), ERROR_MSG_INSERT_FAILED, sqlEx);
            db.update(table, values,
                      VALUES_ID + SQLQuery.SQL_SEARCH_EQUAL,
                      new String[]{values.get(VALUES_ID).toString()});
          }
        }
      }
      db.close();
    }
  }
}
