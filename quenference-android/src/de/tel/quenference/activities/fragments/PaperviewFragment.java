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
package de.tel.quenference.activities.fragments;

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
import de.tel.quenference.activities.R;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.ConferenceDBHelper;
import de.tel.quenference.db.dao.AsyncDBListReader;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.dao.SQLQuery;
import de.tel.quenference.db.dao.SQLQuery.Builder;
import de.tel.quenference.db.entities.AuthorEntity;
import de.tel.quenference.db.entities.PaperAuthorsEntity;
import de.tel.quenference.db.entities.PaperEntity;
import de.tel.quenference.db.entities.SessionEntity;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class PaperviewFragment extends PaperDetailMenuFragment {

    public static final String ARG_PAPERVIEW_FRAGMENT = "paper_arg";
    private static final String TAG_PAPERVIEW = "paper_tag";

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
        setFavIcon(paper.getFavorite());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paperview, container, false);
        title = ((TextView) rootView.findViewById(R.id.paper_title));
        title.setText(paper.getTitle());

        author = ((TextView) rootView.findViewById(R.id.paper_author));
        setAuthors(author);
//    author.setText(paper.getMainAuthor());

        time = ((TextView) rootView.findViewById(R.id.paper_speech_time));
        time.setText(getTimeValue());

        abstrct = ((TextView) rootView.findViewById(R.id.paper_abstract));
        abstrct.setText(paper.getAbstrct());


//    getActivity().setTitle(paper.getTitle());
        return rootView;
    }

    private String getTimeValue() {
        StringBuilder builder = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.paper_time_pattern));//"yyyy-MM-dd'T'hh:mm:ssZ"
        Date begin = null, end = null;
        try {
            begin = sdf.parse(paper.getDateTime());
            begin = new Date(begin.getTime() - 21600000); //FIXME Removed 6 hrs due to Singapore Time. Delete on next conference)
            end = sdf.parse(paper.getDateTimeEnd());
            System.out.println("End Time before removal of ms: " + end.getTime());
            end = new Date(end.getTime() - 21600000); //FIXME Removed 6 hrs due to Singapore Time. Delete on next conference)
        } catch (ParseException ex) {
            Log.e(PaperviewFragment.class.getName(), TAG_PAPERVIEW, ex);
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

    private void setAuthors(final TextView view) {
        final StringBuilder builder = new StringBuilder("");
        String selection = ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_PAPER_ID + SQLQuery.SQL_SEARCH_EQUAL;
        SQLQuery.Builder queryBuilder = new SQLQuery.Builder(selection, ConferenceDAO.Entity.PAPER_AUTHORS);
        queryBuilder.addArgs(paper.getId().toString());
        SQLQuery query = queryBuilder.build();
        ConferenceDAO.getSelection(getActivity(), query.getSelectedEntity(),
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
                                    SQLQuery.Builder builder = new SQLQuery.Builder(select, ConferenceDAO.Entity.PAPER);
                                    builder.addArgs(((PaperAuthorsEntity) obj).getAuthorID().toString());
                                    SQLQuery q = builder.build();

                                    Cursor c = db.query(ConferenceDBContract.ConferenceAuthor.TABLE_NAME,
                                            ConferenceDAO.AUTHOR_COLUMNS, q.getSelection(),
                                            q.getSelectionArgs(), q.getGroupBy(),
                                            q.getHaving(), q.getHaving());
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
                }, query.getSelection(), query.getSelectionArgs(), query.getGroupBy(),
                query.getHaving(), query.getOrderBy());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TAG_PAPERVIEW, paper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //APPEND SCREEN TO LOGGER
//    logger.appendLog(start, new Date().getTime(), application);
    }

    @Override
    Builder getFavoriteUpdateSQLQuery() {
        String select = ConferenceDBContract.ConferencePaper.COLUMN_NAME_ID + SQLQuery.SQL_SEARCH_EQUAL;
        SQLQuery.Builder builder = new SQLQuery.Builder(select, ConferenceDAO.Entity.PAPER);
        builder.addArgs(paper.getId().toString());
        return builder;
    }

    @Override
    String getFavoriteColumnName() {
        return ConferenceDBContract.ConferencePaper.COLUMN_NAME_FAVORITE;
    }

    @Override
    Serializable getEntity() {
        return paper;
    }
}
