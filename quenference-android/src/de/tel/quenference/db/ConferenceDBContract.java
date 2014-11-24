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
package de.tel.quenference.db;

/**
 * Represents the contract with the SQLiteDatabase. Contains all tables and
 * there column names etc. and also the CREATE and DROP statements.
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
  /**
   * SQL create statement for the author table.
   */
  public static final String SQL_CREATE_AUTHOR_TABLE = String.format(CREATE_STATEMENT,
          ConferenceAuthor.TABLE_NAME,
          ConferenceAuthor.COLUMN_NAME_ID,
          INT_TYPE,
          ConferenceAuthor.COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP
          + ConferenceAuthor.COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP
          + ConferenceAuthor.COLUMN_NAME_AFFILIATION + TEXT_TYPE);
  /**
   * SQL create statement for the paper table.
   */
  public static final String SQL_CREATE_PAPER_TABLE = String.format(CREATE_STATEMENT,
          ConferencePaper.TABLE_NAME,
          ConferencePaper.COLUMN_NAME_ID,
          INT_TYPE,
          ConferencePaper.COLUMN_NAME_SUBMISSION_ID + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_MAIN_AUTHOR + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_MAIN_AUTHOR_ID + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_PAPER_CODE + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_DATETIME_END + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_KEYWORDS + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_ABSTRACT + TEXT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_SESSION + INT_TYPE + COMMA_SEP
          + ConferencePaper.COLUMN_NAME_FAVORITE + INT_TYPE);
  /**
   * SQL create statement for the session table.
   */
  public static final String SQL_CREATE_SESSION_TABLE = String.format(CREATE_STATEMENT,
          ConferenceSession.TABLE_NAME,
          ConferenceSession.COLUMN_NAME_ID,
          INT_TYPE,
          ConferenceSession.COLUMN_NAME_DAY + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_TYPE_NAME + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_CODE + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_LENGTH + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_ROOM + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_CHAIR + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_CO_CHAIR + TEXT_TYPE + COMMA_SEP
          + ConferenceSession.COLUMN_NAME_FAVORITE + INT_TYPE);
  /**
   * SQL create statement for the relation paper-authors table.
   */
  public static final String SQL_CREATE_PAPER_AUTHORS_TABLE = String.format(CREATE_STATEMENT_COMPOSITE_PK,
          ConferencePaperAuthors.TABLE_NAME,
          ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID + INT_TYPE + COMMA_SEP
          + ConferencePaperAuthors.COLUMN_NAME_PAPER_ID + INT_TYPE,
          ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID,
          ConferencePaperAuthors.COLUMN_NAME_PAPER_ID);
  /**
   * SQL drop statement for the author table.
   */
  public static final String SQL_DROP_AUTHOR_TABLE = String.format(DROP_STATEMENT, ConferenceAuthor.TABLE_NAME);
  /**
   * SQL drop statement for the paper table.
   */
  public static final String SQL_DROP_PAPER_TABLE = String.format(DROP_STATEMENT, ConferencePaper.TABLE_NAME);
  /**
   * SQL drop statement for the session table.
   */
  public static final String SQL_DROP_SESSION_TABLE = String.format(DROP_STATEMENT, ConferenceSession.TABLE_NAME);
  /**
   * SQL drop statement for the relation paper-authors table.
   */
  public static final String SQL_DROP_PAPER_AUTHORS_TABLE = String.format(DROP_STATEMENT, ConferencePaperAuthors.TABLE_NAME);

  /**
   * Private ctor to create no object of the DB Contract class.
   */
  private ConferenceDBContract() {
  }

  /**
   * The abstract class which contains all columns and the name of the author
   * table.
   */
  public static abstract class ConferenceAuthor {

    public static final String COLUMN_NAME_AFFILIATION = "affiliation";
    public static final String COLUMN_NAME_FIRST_NAME = "first_name";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_LAST_NAME = "last_name";
    public static final String TABLE_NAME = "author";
  }

  /**
   * The abstract class which contains all columns and the name of the paper
   * table.
   */
  public static abstract class ConferencePaper {

    public static final String COLUMN_NAME_ABSTRACT = "abstract";
    public static final String COLUMN_NAME_DATETIME = "datetime";
    public static final String COLUMN_NAME_DATETIME_END = "datetime_end";
    public static final String COLUMN_NAME_FAVORITE = "favorite";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_KEYWORDS = "keywords";
    public static final String COLUMN_NAME_MAIN_AUTHOR = "main_author";
    public static final String COLUMN_NAME_MAIN_AUTHOR_ID = "main_author_id";
    public static final String COLUMN_NAME_PAPER_CODE = "paper_code";
    public static final String COLUMN_NAME_SESSION = "session";
    public static final String COLUMN_NAME_SUBMISSION_ID = "submission_id";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String TABLE_NAME = "paper";
  }

  /**
   * The abstract class which contains all columns and
   * the name of the session table.
   */
  public static abstract class ConferenceSession {

    public static final String COLUMN_NAME_CHAIR = "chair";
    public static final String COLUMN_NAME_CODE = "code";
    public static final String COLUMN_NAME_CO_CHAIR = "co_chair";
    public static final String COLUMN_NAME_DATETIME = "datetime";
    public static final String COLUMN_NAME_DAY = "day";
    public static final String COLUMN_NAME_FAVORITE = "favorite";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_LENGTH = "length";
    public static final String COLUMN_NAME_ROOM = "room";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_TYPE_NAME = "type_name";
    public static final String TABLE_NAME = "session";
  }

  /**
   * The abstract class which contains all columns and 
   * the name of the paper-authors table.
   */
  public static abstract class ConferencePaperAuthors {

    public static final String COLUMN_NAME_AUTHOR_ID = "authorID";
    public static final String COLUMN_NAME_PAPER_ID = "paperID";
    public static final String TABLE_NAME = "paperAuthors";
  }
}
