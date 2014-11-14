/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tel.quenference.db.dao;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.ConferenceDBHelper;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncConferenceDBUpdater extends AsyncTask<JSONObject, Void, Boolean> {

    private static final String ERROR_MSG_JSON_EXTRACT = "JSON EXTRACT FAILED!";
    private static final String ERROR_MSG_INSERT_FAILED = "Insert failed, try update...";
    private static final String LOG_MSG_INSERT = "Table: %s values: %s";
    private static final String VALUES_ID = "id";
    private static final String UNUSED_JSON_META = "meta";
    private ConferenceDBHelper dBHelper;

    public AsyncConferenceDBUpdater(ConferenceDBHelper dbHelper) {
        this.dBHelper = dbHelper;
    }

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
//      TODO AUTHOR adding
//      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR,
//              json.getString(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR));
//      values.put(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID,
//              json.getInt(ConferenceDBContract.ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID));
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
                            author.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_FIRST_NAME) +
                                    " " + author.getString(ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_LAST_NAME));
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
        if (args.length > 0) {
            JSONObject json = args[0];
            SQLiteDatabase db = dBHelper.getWritableDatabase();
            Iterator iterator = json.keys();
            String arrayKey = "";
            JSONArray responseValues = null;
            if (iterator.hasNext()) {
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

                    if (responseValues != null) {
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
                                //    Log.d(AsyncConferenceDBUpdater.class.getName(), String.format(LOG_MSG_INSERT, table, values));
                                try {
                                    db.insertOrThrow(table, null, values);
                                } catch (SQLException sqlEx) {
                                    Log.d(AsyncConferenceDBUpdater.class.getName(), ERROR_MSG_INSERT_FAILED);
                                    db.update(table, values, VALUES_ID + " = ?", new String[]{values.get(VALUES_ID).toString()});
                                }
                            }
                        }
                    }
                } catch (JSONException ex) {
                    Log.e(ConferenceDAO.class.getName(), ERROR_MSG_JSON_EXTRACT, ex);
                }
            }
            Log.d(AsyncConferenceDBUpdater.class.getName(), "Database update completed");
            db.close();
            return true;
        } else
            return false;
    }
}
