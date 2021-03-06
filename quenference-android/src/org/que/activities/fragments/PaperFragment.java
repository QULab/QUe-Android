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
package org.que.activities.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.que.activities.R;
import org.que.db.ConferenceDBContract;
import org.que.db.ConferenceDBHelper;
import org.que.db.Entity;
import org.que.db.dao.AsyncDBListReader;
import org.que.db.dao.ConferenceDAO;
import org.que.db.dao.SQLQuery;
import org.que.db.entities.AuthorEntity;
import org.que.db.entities.PaperAuthorsEntity;
import org.que.db.entities.PaperEntity;
import org.que.db.entities.SessionEntity;

/**
 * Represents the fragment which shows the paper informations.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperFragment extends PaperDetailMenuFragment {

  /**
   * The argument key for the fragment.
   */
  public static final String ARG_PAPERVIEW_FRAGMENT = "paper_arg";
  
  /**
   * The tag for the paper view.
   */
  private static final String TAG_PAPERVIEW = "paper_tag";
  
  /**
   * The TextViews which shows the informations of the paper.
   */
  private TextView title, author, time, abstrct;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (null == savedInstanceState) {
      savedInstanceState = getArguments();
    }

    if (null != savedInstanceState) {
      paper = (PaperEntity) savedInstanceState.getSerializable(TAG_PAPERVIEW);
    }

    if (null == paper) {
      paper = (PaperEntity) savedInstanceState.getSerializable(ARG_PAPERVIEW_FRAGMENT);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    setFavIcon(favorited);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_paperview, container, false);
    title = ((TextView) rootView.findViewById(R.id.paper_title));
    title.setText(paper.getTitle());

    author = ((TextView) rootView.findViewById(R.id.paper_author));
    setAuthors(author);

    time = ((TextView) rootView.findViewById(R.id.paper_speech_time));
    time.setText(getTimeValue());

    abstrct = ((TextView) rootView.findViewById(R.id.paper_abstract));
    abstrct.setText(paper.getAbstrct());
    return rootView;
  }

  /**
   * Returns the time value of the paper.
   * 
   * @return the time value
   */
  private String getTimeValue() {
    StringBuilder builder = new StringBuilder();

    SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.paper_time_pattern));//"yyyy-MM-dd'T'hh:mm:ssZ"
    Date begin = null, end = null;
    try {
      begin = sdf.parse(paper.getDateTime());
      begin = new Date(begin.getTime());
      end = sdf.parse(paper.getDateTimeEnd());
      end = new Date(end.getTime());
    } catch (ParseException ex) {
      Log.e(PaperFragment.class.getName(), TAG_PAPERVIEW, ex);
    }
    if (begin != null && end != null) {
      //Date Format
      int style = DateFormat.MEDIUM;
      Locale l = new Locale(getString(R.string.paper_date_locale));
      DateFormat dateFormat = DateFormat.getDateInstance(style, l);

      //Hour Format
      SimpleDateFormat hourFormat = new SimpleDateFormat(getString(R.string.paper_hour_format));
      builder.append(dateFormat.format(begin)) //date
              .append("\n")
              .append(hourFormat.format(begin))
              .append(" ")
              .append(getString(R.string.paper_hour_seperator))
              .append(" ")
              .append(hourFormat.format(end));
    }

    //now we set the venue and session
    SessionEntity sessionEntity = ConferenceDAO.getSessionByID(Integer.toString(paper.getSession()), getActivity());
    builder.append("\n");
    builder.append(sessionEntity.getTitle());
    builder.append("\n");
    builder.append(sessionEntity.getRoom());
    return builder.toString();
  }

  /**
   * Writes the authors of the paper into the TextView.
   *
   * @param view the view which should be used
   */
  private void setAuthors(final TextView view) {
    final StringBuilder builder = new StringBuilder("");
    String selection = ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_PAPER_ID + SQLQuery.SQL_SEARCH_EQUAL;
    SQLQuery query = new SQLQuery(selection, Entity.PAPER_AUTHORS, ConferenceDAO.PAPER_AUTHORS_COLUMNS);
    query.setSelectionArgs(paper.getId().toString());
    ConferenceDAO.getSelection(getActivity(),
            new AsyncDBListReader.PostExecuteJob() {
              public void doJob(final List result) {
                new AsyncTask<Context, Void, List<AuthorEntity>>() {
                  @Override
                  protected List<AuthorEntity> doInBackground(Context... contexts) {
                    List<AuthorEntity> authors = new ArrayList<AuthorEntity>();
                    final ConferenceDBHelper DB_HELPER = new ConferenceDBHelper(contexts[0]);
                    SQLiteDatabase db = DB_HELPER.getReadableDatabase();
                    for (Object obj : result) {
                      String select = ConferenceDBContract.ConferenceAuthor.COLUMN_NAME_ID + SQLQuery.SQL_SEARCH_EQUAL;
                      SQLQuery query = new SQLQuery(select, Entity.PAPER, ConferenceDAO.PAPER_COLUMNS);
                      query.setSelectionArgs(((PaperAuthorsEntity) obj).getAuthorID().toString());

                      Cursor c = db.query(ConferenceDBContract.ConferenceAuthor.TABLE_NAME,
                              ConferenceDAO.AUTHOR_COLUMNS, query.getSelection(),
                              query.getSelectionArgs(), query.getGroupBy(),
                              query.getHaving(), query.getHaving());
                      c.moveToFirst();
                      while (!c.isAfterLast()) {
                        AuthorEntity p = (AuthorEntity) ConferenceDAO.getAuthorCursorExtractor().extract(c);
                        authors.add(p);
                        c.moveToNext();
                      }
                      c.close();
                    }
                    DB_HELPER.close();
                    return authors;
                  }

                  @Override
                  protected void onPostExecute(List<AuthorEntity> result) {
                    super.onPostExecute(result);
                    for (AuthorEntity a : result) {
                      //if (a.getId() != paper.getMainAuthorId()) //get only co authors
                      builder.append(a.getFullName()).append(", ");
                    }
                    view.setText(builder.toString());
                  }
                }.execute(getActivity());
              }
            }, query);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(TAG_PAPERVIEW, paper);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected SQLQuery getFavoriteUpdateSQLQuery() {
    String select = ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + SQLQuery.SQL_SEARCH_EQUAL;
    SQLQuery query = new SQLQuery(select, Entity.PAPER, ConferenceDAO.PAPER_COLUMNS);
    query.setSelectionArgs(paper.getId().toString());
    return query;
  }

  @Override
  protected String getFavoriteColumnName() {
    return ConferenceDBContract.ConferencePaper.COLUMN_NAME_FAVORITE;
  }

  @Override
  protected Serializable getEntity() {
    return paper;
  }
}
