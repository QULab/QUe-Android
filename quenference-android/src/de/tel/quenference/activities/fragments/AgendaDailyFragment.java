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


import android.os.Bundle;
import android.support.v4.app.*;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.tel.quenference.activities.R;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.Entity;
import de.tel.quenference.db.dao.AsyncDBListReader;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.dao.SQLQuery;
import de.tel.quenference.db.entities.SessionEntity;
import de.tel.quenference.util.EntityListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deLaczkovich on 30-Jul-14.
 * @author deLaczkovich, Christopher Zell <zelldon91@googlemail.com>
 */
public class AgendaDailyFragment extends ListFragment {

    protected static final String TAG_SEARCH_VALUE = "searchValue";
    private static final String TITLE = "AGENDA";
    /**
     * The tag for the saved search title.
     */
    protected static final String TAG_AGENDA_FRAGMENT = "agendaFrag";

    /**
     * The value to search for.
     */
//    protected SQLQuery query;

    /**
     * The title of the search (identifies the table for the searching).
     */
    protected SearchTabFragmentViewPager.TabSearch searchFragmentVP;
    protected String agendaDay;

    public ListAdapter getListAdapter(String agendaDay) {
        return new EntityListAdapter(new ArrayList(), agendaDay, this.getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agendaDay = getArguments().getString("CONFDAY");
        setListAdapter(getListAdapter(agendaDay));

        final String SQL_SEARCH_LIKE = " LIKE ? ";
        String SQL_OR = " OR ";
        String SQL_ASC_ORDER = " ASC";
        String sessionSelection = ConferenceDBContract.ConferenceSession.COLUMN_NAME_DAY + SQL_SEARCH_LIKE;
        String paperSessionOrder =
                ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME + SQL_ASC_ORDER;
        String arg = agendaDay;//"SELECT * FROM SESSION WHERE DAY LIKE '0'";
        SQLQuery query = new SQLQuery(sessionSelection, Entity.SESSION, ConferenceDAO.SESSION_COLUMNS);
        query.setOrderBy(paperSessionOrder);
        query.setSelectionArgs(new String[]{arg});
        //System.out.println("agenda day: " + agendaDay + "SessionSelection: " + sessionSelection);
        search(query);
    }

    public void onResume(Bundle savedInstanceState) {
        super.onResume();
        getActivity().setTitle(TITLE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setFastScrollEnabled(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Fragment frg = null;
        Bundle args = new Bundle();

        SessionEntity entity = (SessionEntity) getListAdapter().getItem(position);
        frg = new SessionViewFragment();
        args.putSerializable(SessionViewFragment.ARG_SESSIONVIEW_FRAGMENT, entity);


        if (frg != null) {
            frg.setArguments(args);
            FragmentManager mgr = ((FragmentActivity) getActivity()).getSupportFragmentManager();
            Fragment old = mgr.findFragmentById(R.id.content_frame);


            FragmentTransaction trx = mgr.beginTransaction();
            if (old != null)
                trx.remove(old);

            trx.add(R.id.content_frame, frg)
                    .addToBackStack(null) //TODO
                    .commit();
        }
    }

    /**
     * Executes the search for the given query in the SQLiteDatabase.
     * The query is saved in the field searchValue.
     */
    protected void search(SQLQuery q) {

        ConferenceDAO.getSelection(getActivity(), new AsyncDBListReader.PostExecuteJob() {
                    public void doJob(List result) {
                        ((EntityListAdapter) getListAdapter()).setResults(result);
                        ((EntityListAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                }, q);
    }


    public static AgendaDailyFragment newInstance(String confday) {
        AgendaDailyFragment fragment = new AgendaDailyFragment();
        Bundle arguments = new Bundle();
        arguments.putString("CONFDAY", confday);
        fragment.setArguments(arguments);
        return fragment;
    }

}
