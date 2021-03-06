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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.que.async.AsyncGETRequester;
import org.que.async.GetRequestInfo;
import org.que.db.ConferenceDBContract;
import org.que.db.ConferenceDBHelper;
import org.que.db.Entity;
import org.que.db.dao.extractor.AuthorExtractor;
import org.que.db.dao.extractor.PaperAuthorsExtractor;
import org.que.db.dao.extractor.PaperExtractor;
import org.que.db.dao.extractor.SessionExtractor;
import org.que.db.entities.AuthorEntity;
import org.que.db.entities.PaperAuthorsEntity;
import org.que.db.entities.PaperEntity;
import org.que.db.entities.SessionEntity;
import org.que.util.PropertiesProvider;

/**
 * Represents the DAO (data access object) for the conference application.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class ConferenceDAO {

  /**
   * Returns for the given class the declared fields. The method is used to get
   * the columns of the tables from the DB contract.
   *
   *
   * @param c the class which represents the database table
   * @return the corresponding columns
   */
  private static String[] getColumns(Class c) {
    Field fields[] = c.getDeclaredFields();
    String values[] = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      try {
        if (!fields[i].getName().equalsIgnoreCase("TABLE_NAME")) {
          values[i] = fields[i].get(null).toString();
        }
      } catch (IllegalAccessException ex) {
        Logger.getLogger(ConferenceDAO.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalArgumentException ex) {
        Logger.getLogger(ConferenceDAO.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return values;
  }
  /**
   * The columns of the author table.
   */
  public static final String[] AUTHOR_COLUMNS = getColumns(ConferenceDBContract.ConferenceAuthor.class);
  /**
   * The columns of the paper table.
   */
  public static final String[] PAPER_COLUMNS = getColumns(ConferenceDBContract.ConferencePaper.class);
  /**
   * The columns of the session table.
   */
  public static final String[] SESSION_COLUMNS = getColumns(ConferenceDBContract.ConferenceSession.class);
  /**
   * The columns of the paper-authors relation table.
   */
  public static final String[] PAPER_AUTHORS_COLUMNS = getColumns(ConferenceDBContract.ConferencePaperAuthors.class);
  /**
   * The key for the authors etag which is saved in the preferences.
   */
  private static final String KEY_AUTHORS_ETAG = "authors";
  /**
   * The URL for the author values.
   */
  private static final String URL_AUTHORS = PropertiesProvider.getInstance().getProperty(PropertiesProvider.AUTHORS_URL_PROP);
  /**
   * The key for the papers etag which is saved in the preferences.
   */
  private static final String KEY_PAPERS_ETAG = "papers";
  /**
   * The json key which is used to identify the papers array.
   */
  private static final String JSON_KEY_PAPERS = KEY_PAPERS_ETAG;
  /**
   * The json key which is used to identify the batch array. The batch array
   * contains all other values.
   */
  private static final String JSON_KEY_BATCH = "batch_download";
  /**
   * The URL for the paper values.
   */
  private static final String URL_PAPERS = PropertiesProvider.getInstance().getProperty(PropertiesProvider.PAPERS_URL_PROP);
  /**
   * The key for the sessions etag which is saved in the preferences.
   */
  private static final String KEY_SESSIONS_ETAG = "sessions";
  /**
   * The URL for the session values.
   */
  private static final String URL_SESSIONS = PropertiesProvider.getInstance().getProperty(PropertiesProvider.SESSIONS_URL_PROP);
  /**
   * The selection for the author search query (where clause).
   */
  private static final String authorSelection =
          ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_AFFILIATION + SQLQuery.SQL_SEARCH_LIKE + SQLQuery.SQL_OR
          + ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME + SQLQuery.SQL_SEARCH_LIKE + SQLQuery.SQL_OR
          + ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME + SQLQuery.SQL_SEARCH_LIKE;
  private static final String authorOrder =
          ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME + SQLQuery.SQL_ASC_ORDER;
  /**
   * The selection for the paper search query (where clause).
   */
  private static final String paperSelection =
          ConferenceDBContract.ConferencePaper.COLUMN_NAME_TITLE + SQLQuery.SQL_SEARCH_LIKE + SQLQuery.SQL_OR
          + ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR + SQLQuery.SQL_SEARCH_LIKE + SQLQuery.SQL_OR
          + ConferenceDBContract.ConferencePaper.COLUMN_NAME_ABSTRACT + SQLQuery.SQL_SEARCH_LIKE;
  /**
   * The selection for the session search query (where clause).
   */
  private static final String sessionSelection =
          ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE + SQLQuery.SQL_SEARCH_LIKE + SQLQuery.SQL_OR
          + ConferenceDBContract.ConferenceSession.COLUMN_NAME_CHAIR + SQLQuery.SQL_SEARCH_LIKE + SQLQuery.SQL_OR
          + ConferenceDBContract.ConferenceSession.COLUMN_NAME_CO_CHAIR + SQLQuery.SQL_SEARCH_LIKE;
  
  /**
   * The order by title clause for a paper query.
   */
  private static final String paperSessionOrder =
          ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE + SQLQuery.SQL_ASC_ORDER;
  
  /**
   * The day selection for a session query.
   */
  private static final String sessionDaySelection =
          ConferenceDBContract.ConferenceSession.COLUMN_NAME_DAY + SQLQuery.SQL_SEARCH_EQUAL;
  /**
   * The order by day clause for a session query.
   */
  private static final String sessionDayOrderBy =
          ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME + SQLQuery.SQL_ASC_ORDER;

  /**
   * Creates the SQLQuery for the paper table and the given argument.
   *
   * @param arg the argument for the selection
   * @return the created SQL query
   */
  public static SQLQuery getPaperQuery(String arg) {
    arg = SQLQuery.SQL_VARIABLE_EXP + arg + SQLQuery.SQL_VARIABLE_EXP;
    SQLQuery query = new SQLQuery(paperSelection, Entity.PAPER, PAPER_COLUMNS);
    query.setOrderBy(paperSessionOrder);
    query.setSelectionArgs(new String[]{arg, arg, arg});
    return query;
  }

  /**
   * Creates the SQLQuery for the author table and the given argument.
   *
   * @param arg the argument for the selection
   * @return the created SQL query
   */
  public static SQLQuery getAuthorQuery(String arg) {
    arg = SQLQuery.SQL_VARIABLE_EXP + arg + SQLQuery.SQL_VARIABLE_EXP;
    SQLQuery query = new SQLQuery(authorSelection, Entity.AUTHOR, AUTHOR_COLUMNS);
    query.setOrderBy(authorOrder);
    query.setSelectionArgs(new String[]{arg, arg, arg});
    return query;
  }

  /**
   * Creates the SQLQuery for the session table and the given argument.
   *
   * @param arg the argument for the selection
   * @return the created SQL query
   */
  public static SQLQuery getSessionQuery(String arg) {
    arg = SQLQuery.SQL_VARIABLE_EXP + arg + SQLQuery.SQL_VARIABLE_EXP;
    SQLQuery query = new SQLQuery(sessionSelection,
            Entity.SESSION,
            SESSION_COLUMNS);
    query.setOrderBy(paperSessionOrder);
    query.setSelectionArgs(new String[]{arg, arg, arg});
    return query;
  }

  /**
   * Creates the SQLQuery for the session table and the given argument. The
   * query contains the order by clause for the day column.
   *
   * @param arg the arguments for the selection
   * @return the created SQL query
   */
  public static SQLQuery getSessionDateOrderQuery(String arg) {
    SQLQuery query = new SQLQuery(sessionDaySelection,
            Entity.SESSION,
            SESSION_COLUMNS);
    query.setOrderBy(sessionDayOrderBy);
    query.setSelectionArgs(arg);
    return query;
  }

  /**
   * Updates the database with the new values from the web service. The saved
   * etags are checked if they are equal nothing is done.
   *
   * @param context the application context to update the database
   */
  public static void updateDB(Context context) {
    if (context == null) {
      throw new IllegalArgumentException();
    }

    final SharedPreferences pref = context.getSharedPreferences(ConferenceDAO.class.getName(), 0);
    String authorsEtag = pref.getString(KEY_AUTHORS_ETAG, "");
    String papersETag = pref.getString(KEY_PAPERS_ETAG, "");
    String sessionsEtag = pref.getString(KEY_SESSIONS_ETAG, "");
    final ConferenceDBHelper DB_HELPER = new ConferenceDBHelper(context);
    AsyncGETRequester requester = new AsyncGETRequester(new AsyncGETRequester.PostExecuteJob() {
      public void doJob(JSONObject response) {

        Iterator it = response.keys();
        JSONArray array;
        try {
          if (it.hasNext()) {
            String key = it.next().toString();
            if (key.equalsIgnoreCase(JSON_KEY_BATCH)) {
              // parse from batch_download papers with abstract
              response = response.getJSONObject(key);
              it = response.keys();
              while (it.hasNext()) {
                key = it.next().toString();
                if (key.equalsIgnoreCase(JSON_KEY_PAPERS)) {
                  array = response.getJSONArray(key);
                  response = new JSONObject().put(JSON_KEY_PAPERS, array);
                }
              }
            }
          }
        } catch (JSONException ex) {
          Log.e(ConferenceDAO.class.getName(), "JSONException", ex);
        }
        //pass only correct json objects (sessions, papers or authors arrays)
        AsyncConferenceDBUpdater updater = new AsyncConferenceDBUpdater(DB_HELPER);
        updater.execute(response);
      }

      public void doExeptionHandling(Throwable t) {
      }

      public void handleNewEtag(String url, String newEtag) {
        if (url.equalsIgnoreCase(URL_AUTHORS)) {
          pref.edit().putString(KEY_AUTHORS_ETAG, newEtag).commit();
        }
        if (url.equalsIgnoreCase(URL_PAPERS)) {
          pref.edit().putString(KEY_PAPERS_ETAG, newEtag).commit();
        }
        if (url.equalsIgnoreCase(URL_SESSIONS)) {
          pref.edit().putString(KEY_SESSIONS_ETAG, newEtag).commit();
        }
      }
    });

    GetRequestInfo[] infos = {new GetRequestInfo(URL_PAPERS, papersETag),
      new GetRequestInfo(URL_AUTHORS, authorsEtag),
      new GetRequestInfo(URL_SESSIONS, sessionsEtag)};
    requester.execute(infos);
    DB_HELPER.close();
  }

  /**
   * Executes the SQL query for the entity on the corresponding table. The
   * return value is processed in the post job.
   *
   * @param context the application context which is used
   * @param postJob the job which will be executed after the query
   * @param query the sql query which contains the information for the request
   */
  public static void getSelection(Context context, AsyncDBListReader.PostExecuteJob postJob,
          SQLQuery query) {

    if (context == null || query == null) {
      throw new IllegalArgumentException();
    }

    List values = null;
    CursorExtracting extract = null;

    if (query.getSelectedEntity().equals(Entity.AUTHOR)) {
      values = new ArrayList<AuthorEntity>();
      extract = getAuthorCursorExtractor();
    } else if (query.getSelectedEntity().equals(Entity.PAPER)) {
      values = new ArrayList<PaperEntity>();
      extract = getPaperCursorExtractor();
    } else if (query.getSelectedEntity().equals(Entity.SESSION)) {
      values = new ArrayList<SessionEntity>();
      extract = getSessionCursorExtractor();
    } else if (query.getSelectedEntity().equals(Entity.PAPER_AUTHORS)) {
      values = new ArrayList<PaperAuthorsEntity>();
      extract = getPaperAuthorsCursorExtractor();
    }
    AsyncDBListReader listReader = new AsyncDBListReader(values, query,
            extract, postJob);
    listReader.execute(context);
  }

  /**
   * Returns the SessionExtractor object to extract the session from a given
   * cursor object.
   *
   * @return the SessionExtractor object
   */
  public static CursorExtracting getSessionCursorExtractor() {
    return new SessionExtractor();
  }

  /**
   * Returns the PaperExtractor object to extract the session from a given
   * cursor object.
   *
   * @return the PaperExtractor object
   */
  public static CursorExtracting getPaperCursorExtractor() {
    return new PaperExtractor();
  }

  /**
   * Returns the PaperExtractor object to extract the session from a given
   * cursor object.
   *
   * @return the AuthorExtractor object
   */
  public static CursorExtracting getAuthorCursorExtractor() {
    return new AuthorExtractor();
  }

  /**
   * Returns the PaperAuthorsExtractor object to extract the session from a
   * given cursor object.
   *
   * @return the PaperAuthorsExtractor object
   */
  public static CursorExtracting getPaperAuthorsCursorExtractor() {
    return new PaperAuthorsExtractor();
  }

  /**
   * Updates with the given SQLQuery the corresponding entity/entities in the
   * database.
   *
   * @param context the application context which is used
   * @param query the SQL query which selects the rows and contains the new
   * values
   */
  public static void updateEntity(Context context, SQLQuery query) {
    AsyncDBEntityUpdater updater = null;
    Entity e = query.getSelectedEntity();
    if (e == Entity.AUTHOR) {
      updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
              query.getSelectionArgs(),
              ConferenceDBContract.ConferenceAuthor.TABLE_NAME);
    } else if (e == Entity.PAPER) {
      updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
              query.getSelectionArgs(),
              ConferenceDBContract.ConferencePaper.TABLE_NAME);
    } else if (e == Entity.SESSION) {
      updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
              query.getSelectionArgs(),
              ConferenceDBContract.ConferenceSession.TABLE_NAME);
    } else if (e == Entity.PAPER_AUTHORS) {
      updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
              query.getSelectionArgs(),
              ConferenceDBContract.ConferencePaperAuthors.TABLE_NAME);
    }


    if (updater != null) {
      updater.execute(context);
    }
  }

  /**
   * Returns the SessionEntity for the given id from the database.
   *
   * @param id the session id
   * @param context the application context which will be used
   * @return the corresponding SessionEntity object
   */
  public static SessionEntity getSessionByID(String id, Context context) {
    //Creates a SessionEntity from the database entry at row ID
    SessionEntity entity = new SessionEntity();
    ConferenceDBHelper dbHelper = new ConferenceDBHelper(context);
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    //create the query to fill the cursor with the requested row gotten from the calendar description
    Cursor c = db.query(ConferenceDBContract.ConferenceSession.TABLE_NAME,
            SESSION_COLUMNS, ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID + " = " + id,
            null, null, null, null);
    if (c.moveToFirst()) {
      //create the session from the cursor data
      entity = (SessionEntity) getSessionCursorExtractor().extract(c);
    }
    c.close();
    db.close();
    return entity;
  }

  /**
   * Returns the PaperEntity for the given id from the database.
   *
   * @param id the paper id
   * @param context the application context which will be used
   * @return the corresponding PaperEntity object
   */
  public static PaperEntity getPaperByID(String id, Context context) {
    PaperEntity entity = new PaperEntity();
    ConferenceDBHelper dbHelper = new ConferenceDBHelper(context);
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    //create the query to fill the curspr with the requested row gotten from the calendar description
    Cursor c = db.query(ConferenceDBContract.ConferencePaper.TABLE_NAME,
            PAPER_COLUMNS, ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + " = " + id,
            null, null, null, null);
    if (c.moveToFirst()) {
      entity = (PaperEntity) getPaperCursorExtractor().extract(c);
    }
    c.close();
    db.close();
    return entity;
  }
}
