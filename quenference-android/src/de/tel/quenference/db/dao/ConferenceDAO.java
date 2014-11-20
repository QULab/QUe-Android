/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import de.tel.quenference.db.entities.*;
import de.tel.quenference.util.PropertiesProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class ConferenceDAO {

    public static final String[] authorColumns = {
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_AFFILIATION,
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_EMAIL,
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME,
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID,
            ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME};
    public static final String[] paperColumns = {
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_DATETIME,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_DATETIME_END,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_KEYWORDS,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_PAPER_CODE,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_SUBMISSION_ID,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_TITLE,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_ABSTRACT,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_SESSION,
            ConferenceDBContract.ConferencePaper.COLUMN_NAME_FAVORITE
    };

    public static final String[] sessionColumns = {
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_CHAIR,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_CODE,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_CO_CHAIR,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_DAY,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_LENGTH,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_ROOM,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_TITLE,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_TYPE,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_TYPE_NAME,
            ConferenceDBContract.ConferenceSession.COLUMN_NAME_FAVORITE};

    public static final String[] paperAuthorsColumns = {
            ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID,
            ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_PAPER_ID
    };

    private static final String KEY_AUTHORS_ETAG = "authors";
    private static final String URL_AUTHORS = PropertiesProvider.getInstance().getProperty(PropertiesProvider.AUTHORS_URL_PROP);
    //"http://conference1.service.tu-berlin.de/v1/conferences/8/authors";
    private static final String KEY_PAPERS_ETAG = "papers";
    private static final String JSON_KEY_PAPERS = KEY_PAPERS_ETAG;
    private static final String JSON_KEY_BATCH = "batch_download";
    private static final String URL_PAPERS = PropertiesProvider.getInstance().getProperty(PropertiesProvider.PAPERS_URL_PROP);
    //"http://conference1.service.tu-berlin.de/v1/conferences/8/batch";
    private static final String KEY_SESSIONS_ETAG = "sessions";
    private static final String URL_SESSIONS = PropertiesProvider.getInstance().getProperty(PropertiesProvider.SESSIONS_URL_PROP);
    //"http://conference1.service.tu-berlin.de/v1/conferences/8/sessions";

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

    public enum Entity {

        SESSION, AUTHOR, PAPER, PAPER_AUTHORS
    }

    public static void getList(Context context, Entity entity, AsyncDBListReader.PostExecuteJob postJob) {
        getSelection(context, entity, postJob, null, null, null, null, null);
    }

    public static void getSelection(Context context, Entity entity, AsyncDBListReader.PostExecuteJob postJob,
                                    String selection, String[] selectionArgs, String groupBy, String having,
                                    String orderBy) {

        if (context == null || entity == null) {
            throw new IllegalArgumentException();
        }

        List values = null;
        String[] columns = null;
        String tableName = null;
        AsyncDBListReader.CursorExtractor extract = null;

        if (entity.equals(Entity.AUTHOR)) {
            values = new ArrayList<AuthorEntity>();
            columns = authorColumns;
            tableName = ConferenceDBContract.ConferenceAuthor.TABLE_NAME;
            extract = getAuthorCursorExtractor();
        }

        if (entity.equals(Entity.PAPER)) {
            values = new ArrayList<PaperEntity>();
            columns = paperColumns;
            tableName = ConferenceDBContract.ConferencePaper.TABLE_NAME;
            extract = getPaperCursorExtractor();
        }

        if (entity.equals(Entity.SESSION)) {
            values = new ArrayList<SessionEntity>();
            columns = sessionColumns;
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


    public static AsyncDBListReader.CursorExtractor getSessionCursorExtractor() {
        return new AsyncDBListReader.CursorExtractor() {
            public Object extract(Cursor c) {
                SessionEntity s = new SessionEntity(c.getInt(5), c.getInt(4), c.getString(3),
                        c.getString(8), c.getString(9), c.getString(10),
                        c.getString(1), c.getInt(6), c.getString(7),
                        c.getString(0), c.getString(2), c.getInt(11));
                //Log.d(ConferenceDAO.class.getName(), s.toString());
                return s;
            }
        };
    }

    public static AsyncDBListReader.CursorExtractor getPaperCursorExtractor() {
        return new AsyncDBListReader.CursorExtractor() {
            public Object extract(Cursor c) {
                PaperEntity p = new PaperEntity(c.getInt(2), c.getInt(7), c.getString(8),
                        c.getString(9), c.getString(4), c.getInt(5), c.getString(6),
                        c.getString(0), c.getString(1), c.getString(3), c.getInt(10), c.getInt(11));
                //Log.d(ConferenceDAO.class.getName(), p.toString());
                return p;
            }
        };
    }

    public static AsyncDBListReader.CursorExtractor getAuthorCursorExtractor() {
        return new AsyncDBListReader.CursorExtractor() {
            public Object extract(Cursor c) {
                AuthorEntity a = new AuthorEntity(c.getInt(3), c.getString(2), c.getString(4), c.getString(0));
                //Log.d(ConferenceDAO.class.getName(), a.toString());
                return a;
            }
        };
    }

    public static AsyncDBListReader.CursorExtractor getPaperAuthorsCursorExtractor() {
        return new AsyncDBListReader.CursorExtractor() {

            public Object extract(Cursor c) {
                PaperAuthorsEntity p = new PaperAuthorsEntity(c.getInt(0), c.getInt(1));
                //Log.d(ConferenceDAO.class.getName(), p.toString());
                return p;
            }
        };
    }


    public static void updateEntity(Context context, SQLQuery query) {
        AsyncDBEntityUpdater updater = null;
        Entity e = query.getSelectedEntity();
        if (e == Entity.AUTHOR)
            updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
                    query.getSelectionArgs(),
                    ConferenceDBContract.ConferenceAuthor.TABLE_NAME);
        else if (e == Entity.PAPER)
            updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
                    query.getSelectionArgs(),
                    ConferenceDBContract.ConferencePaper.TABLE_NAME);
        else if (e == Entity.SESSION)
            updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
                    query.getSelectionArgs(),
                    ConferenceDBContract.ConferenceSession.TABLE_NAME);
        else if (e == Entity.PAPER_AUTHORS)
            updater = new AsyncDBEntityUpdater(query.getValues(), query.getSelection(),
                    query.getSelectionArgs(),
                    ConferenceDBContract.ConferencePaperAuthors.TABLE_NAME);
       

        if (updater != null)
            updater.execute(context);
    }

    public static SessionEntity getSessionByID(String id, Context context) {
        //Creates a SessionEntity from the database entry at row ID
        SessionEntity entity = new SessionEntity();
        ConferenceDBHelper dbHelper = new ConferenceDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //create the query to fill the cursor with the requested row gotten from the calendar description
        Cursor queryCursor = db.query(ConferenceDBContract.ConferenceSession.TABLE_NAME,
                null, ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID + " = " + id,
                null, null, null, null);
        if (queryCursor.moveToFirst()) {
            //create the session from the cursor data
            entity = new SessionEntity(queryCursor.getInt(0), queryCursor.getInt(1), queryCursor.getString(2),
                    queryCursor.getString(3), queryCursor.getString(4), queryCursor.getString(5),
                    queryCursor.getString(6), queryCursor.getInt(7), queryCursor.getString(8),
                    queryCursor.getString(9), queryCursor.getString(10), queryCursor.getInt(11));
        }
        queryCursor.close();
        db.close();
        return entity;
    }

    public static PaperEntity getPaperByID(String id, Context context) {
        PaperEntity entity = new PaperEntity();
        ConferenceDBHelper dbHelper = new ConferenceDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //create the query to fill the curspr with the requested row gotten from the calendar description
        Cursor queryCursor = db.query(ConferenceDBContract.ConferencePaper.TABLE_NAME,
                null, ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + " = " + id,
                null, null, null, null);
        if (queryCursor.moveToFirst()) {
            //create the PaperEntity from the cursor data
            //beware: the paper abstract is read at cursor position 9, but paper requires it at cursor position 3...
            entity = new PaperEntity(queryCursor.getInt(0), queryCursor.getInt(1), queryCursor.getString(2),
                    queryCursor.getString(9), queryCursor.getString(3), queryCursor.getInt(4),
                    queryCursor.getString(5), queryCursor.getString(6), queryCursor.getString(7),
                    queryCursor.getString(8), queryCursor.getInt(10), queryCursor.getInt(11));
        }
        queryCursor.close();
        db.close();
        return entity;
    }

    public static boolean getFavoriteByID(String paperOrSession, int id, Context context) {
        ConferenceDBHelper dbHelper = new ConferenceDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int answer = 99;
        if (paperOrSession.equals("paper")) {
            Cursor queryCursor = db.query(ConferenceDBContract.ConferencePaper.TABLE_NAME,
                    null, ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + " = " + id,
                    null, null, null, null);
            if (queryCursor.moveToFirst()) {
                answer = queryCursor.getInt(11);
            }
            queryCursor.close();
        } else if (paperOrSession.equals("session")) {
            Cursor queryCursor = db.query(ConferenceDBContract.ConferenceSession.TABLE_NAME,
                    null, ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID + " = " + id,
                    null, null, null, null);
            if (queryCursor.moveToFirst()) {
                answer = queryCursor.getInt(11);

            }
            queryCursor.close();
        }
        db.close();
        return answer == 1;
    }
}
