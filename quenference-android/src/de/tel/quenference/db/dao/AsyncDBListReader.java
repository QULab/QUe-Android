/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tel.quenference.db.dao;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import de.tel.quenference.activities.MainNavigationActivity;
import de.tel.quenference.db.ConferenceDBHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AsyncDBListReader extends AsyncTask<Context, Void, List> {

    private List values;
    private String[] columns;
    private String tableName;
    private CursorExtractor extract;
    private PostExecuteJob postJob;
    //OPTIONAL
    private String selection;
    private String[] selectionArgs;
    private String groupBy;
    private String having;
    private String orderBy;
    private Context context;
    private String TAG = "AsyncDBListReader says: ";

    public AsyncDBListReader(List values, String[] columns, String tableName,
                             CursorExtractor extract, PostExecuteJob postJob) {
        this.values = values;
        this.columns = columns;
        this.tableName = tableName;
        this.extract = extract;
        this.postJob = postJob;
    }

    public AsyncDBListReader(List values, String[] columns, String tableName,
                             CursorExtractor extract, PostExecuteJob postJob, String selection,
                             String[] selectionArgs, String groupBy, String having, String orderBy, Context context) {
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
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "On Pre-execute");
            }
        });
    }

    @Override
    protected List doInBackground(Context... context) {
        Log.d(TAG, "doing stuff in Background");
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

    public interface CursorExtractor {

        public Object extract(Cursor c);
    }

    public interface PostExecuteJob {

        public void doJob(List result);
    }
}
