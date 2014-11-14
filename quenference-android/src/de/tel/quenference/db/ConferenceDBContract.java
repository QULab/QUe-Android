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
package de.tel.quenference.db;

import android.provider.BaseColumns;

/**
 * Represents the contract with the SQLiteDatabase.
 * Contains all tables and there column names etc. and also the CREATE and DROP
 * statements.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class ConferenceDBContract {

    /**
     * The SQLiteDatabase text datatype.
     */
    private static final String TEXT_TYPE = " TEXT";
    /**
     * The SQLiteDatabase integer datatype.
     */
    private static final String INT_TYPE = " INTEGER";

    /**
     * The comma separator which will be used in the SQL statements.
     */
    private static final String COMMA_SEP = ",";

    /**
     * The SQLiteDatabase create table formula.
     */
    private static final String CREATE_STATEMENT = "CREATE TABLE %s ( %s %s PRIMARY KEY, %s ); ";

    /**
     * The SQLiteDatabase create table formula, for composite key.
     */
    private static final String CREATE_STATEMENT_COMPOSITE_PK = "CREATE TABLE %s ( %s, PRIMARY KEY ( %s, %s ));";

    /**
     * The SQLiteDatabase drop table formula.
     */
    private static final String DROP_STATEMENT = "DROP TABLE IF EXISTS %s;";

    public static final String SQL_CREATE_AUTHOR_TABLE = String.format(CREATE_STATEMENT,
            ConferenceAuthor.TABLE_NAME,
            ConferenceAuthor.COLUMN_NAME_ID,
            INT_TYPE,
            ConferenceAuthor.COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    ConferenceAuthor.COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    ConferenceAuthor.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                    ConferenceAuthor.COLUMN_NAME_AFFILIATION + TEXT_TYPE);

    public static final String SQL_CREATE_PAPER_TABLE = String.format(CREATE_STATEMENT,
            ConferencePaper.TABLE_NAME,
            ConferencePaper.COLUMN_NAME_ID,
            INT_TYPE,
            ConferencePaper.COLUMN_NAME_SUBMISSION_ID + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_MAIN_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_PAPER_CODE + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_DATETIME_END + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_KEYWORDS + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_ABSTRACT + TEXT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_SESSION + INT_TYPE + COMMA_SEP +
                    ConferencePaper.COLUMN_NAME_FAVORITE + INT_TYPE);

    public static final String SQL_CREATE_SESSION_TABLE = String.format(CREATE_STATEMENT,
            ConferenceSession.TABLE_NAME,
            ConferenceSession.COLUMN_NAME_ID,
            INT_TYPE,
            ConferenceSession.COLUMN_NAME_DAY + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_TYPE_NAME + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_CODE + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_LENGTH + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_ROOM + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_CHAIR + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_CO_CHAIR + TEXT_TYPE + COMMA_SEP +
                    ConferenceSession.COLUMN_NAME_FAVORITE + INT_TYPE);

    public static final String SQL_CREATE_PAPER_AUTHORS_TABLE = String.format(CREATE_STATEMENT_COMPOSITE_PK,
            ConferencePaperAuthors.TABLE_NAME,
            ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID + INT_TYPE + COMMA_SEP +
                    ConferencePaperAuthors.COLUMN_NAME_PAPER_ID + INT_TYPE,
            ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID,
            ConferencePaperAuthors.COLUMN_NAME_PAPER_ID);

    public static final String SQL_DROP_AUTHOR_TABLE = String.format(DROP_STATEMENT, ConferenceAuthor.TABLE_NAME);

    public static final String SQL_DROP_PAPER_TABLE = String.format(DROP_STATEMENT, ConferencePaper.TABLE_NAME);

    public static final String SQL_DROP_SESSION_TABLE = String.format(DROP_STATEMENT, ConferenceSession.TABLE_NAME);

    public static final String SQL_DROP_PAPER_AUTHORS_TABLE = String.format(DROP_STATEMENT, ConferencePaperAuthors.TABLE_NAME);

    private ConferenceDBContract() {
    }

    public static abstract class ConferenceAuthor implements BaseColumns {
        public static final String TABLE_NAME = "author";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_AFFILIATION = "affiliation";
    }

    public static abstract class ConferencePaper implements BaseColumns {
        public static final String TABLE_NAME = "paper";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_SUBMISSION_ID = "submission_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_MAIN_AUTHOR = "main_author";
        public static final String COLUMN_NAME_MAIN_AUTHOR_ID = "main_author_id";
        public static final String COLUMN_NAME_PAPER_CODE = "paper_code";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_DATETIME_END = "datetime_end";
        public static final String COLUMN_NAME_KEYWORDS = "keywords";
        public static final String COLUMN_NAME_SESSION = "session";
        public static final String COLUMN_NAME_ABSTRACT = "abstract";
        public static final String COLUMN_NAME_FAVORITE = "favorite";

    }

    public static abstract class ConferenceSession implements BaseColumns {
        public static final String TABLE_NAME = "session";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_DATETIME = "datetime";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TYPE_NAME = "type_name";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_LENGTH = "length";
        public static final String COLUMN_NAME_ROOM = "room";
        public static final String COLUMN_NAME_CHAIR = "chair";
        public static final String COLUMN_NAME_CO_CHAIR = "co_chair";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
    }

    public static abstract class ConferencePaperAuthors implements BaseColumns {
        public static final String TABLE_NAME = "paperAuthors";
        public static final String COLUMN_NAME_AUTHOR_ID = "authorID";
        public static final String COLUMN_NAME_PAPER_ID = "paperID";
    }
}
