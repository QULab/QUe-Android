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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.tel.quenference.async.AsyncGETRequester;
import de.tel.quenference.async.GetRequestInfo;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.ConferenceDBHelper;
import de.tel.quenference.db.dao.extractor.AuthorExtractor;
import de.tel.quenference.db.dao.extractor.PaperAuthorsExtractor;
import de.tel.quenference.db.dao.extractor.PaperExtractor;
import de.tel.quenference.db.dao.extractor.SessionExtractor;
import de.tel.quenference.db.entities.*;
import de.tel.quenference.util.PropertiesProvider;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
  public static final String[] paperAuthorsColumns = getColumns(ConferenceDBContract.ConferencePaperAuthors.class);
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
   * The Entity enum represents the entities or also named tables from the
   * database.
   */
  public enum Entity {

    SESSION, AUTHOR, PAPER, PAPER_AUTHORS
  }

  /**
   * Executes the SQL Query which returns for the given entity the hole table
   * with all values. The return value is processed in the post job.
   *
   * @param context the application context which is used
   * @param entity the entity which represents the requested table
   * @param postJob the job which will be executed after the query
   */
  public static void getList(Context context, Entity entity, AsyncDBListReader.PostExecuteJob postJob) {
    getSelection(context, entity, postJob, null, null, null, null, null);
  }

  /**
   * Executes the SQL query for the entity on the corresponding table. The
   * return value is processed in the post job.
   *
   * @param context the application context which is used
   * @param entity the entity which represents the requested table
   * @param postJob the job which will be executed after the query
   * @param selection the selection of the SQL query - where clause
   * @param selectionArgs the arguments for the selection
   * @param groupBy the group by clause of the SQL query
   * @param having the having clause of the SQL query
   * @param orderBy the order by clause of the SQL query
   */
  public static void getSelection(Context context, Entity entity, AsyncDBListReader.PostExecuteJob postJob,
          String selection, String[] selectionArgs, String groupBy, String having,
          String orderBy) {

    if (context == null || entity == null) {
      throw new IllegalArgumentException();
    }

    List values = null;
    String[] columns = null;
    String tableName = null;
    CursorExtracting extract = null;

    if (entity.equals(Entity.AUTHOR)) {
      values = new ArrayList<AuthorEntity>();
      columns = AUTHOR_COLUMNS;
      tableName = ConferenceDBContract.ConferenceAuthor.TABLE_NAME;
      extract = getAuthorCursorExtractor();
    }

    if (entity.equals(Entity.PAPER)) {
      values = new ArrayList<PaperEntity>();
      columns = PAPER_COLUMNS;
      tableName = ConferenceDBContract.ConferencePaper.TABLE_NAME;
      extract = getPaperCursorExtractor();
    }

    if (entity.equals(Entity.SESSION)) {
      values = new ArrayList<SessionEntity>();
      columns = SESSION_COLUMNS;
      tableName = ConferenceDBContract.ConferenceSession.TABLE_NAME;
      extract = getSessionCursorExtractor();
    }

    if (entity.equals(Entity.PAPER_AUTHORS)) {
      values = new ArrayList<PaperAuthorsEntity>();
      columns = paperAuthorsColumns;
      tableName = ConferenceDBContract.ConferencePaperAuthors.TABLE_NAME;
      extract = getPaperAuthorsCursorExtractor();
    }
    AsyncDBListReader listReader = new AsyncDBListReader(values, columns, tableName,
            extract, postJob, selection,
            selectionArgs, groupBy, having,
            orderBy);
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
      //create the PaperEntity from the cursor data
      //beware: the paper abstract is read at cursor position 9, but paper requires it at cursor position 3...

      entity = (PaperEntity) getPaperCursorExtractor().extract(c);
    }
    c.close();
    db.close();
    return entity;
  }
}
