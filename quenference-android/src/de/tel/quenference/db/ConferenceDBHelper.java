/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tel.quenference.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class ConferenceDBHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "data/data/de.tel.quenference.activities/databases";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "conference.db";
    private final Context dbHContext;
    private SQLiteDatabase myDataBase;

    public ConferenceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dbHContext = context;
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

    public void createDataBase() {

        boolean dbExist;
        try {
            dbExist = checkDataBase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            throw new Error("database dose not exist");
        }
        if (dbExist) {
            //do nothing - database already exist
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();


        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + "/" + DATABASE_NAME;

            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        //copyDataBase();
        //Open your local db as the input stream
        InputStream myInput = dbHContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + "/" + DATABASE_NAME;
        File databaseFile = new File(DB_PATH);
        // check if databases folder exists, if not create one and its subfolders
        if (!databaseFile.exists()) {
            databaseFile.mkdir();
        }

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();


    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }


    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}

