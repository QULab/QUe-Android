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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.Serializable;
import org.que.activities.R;
import org.que.db.ConferenceDBContract;
import org.que.db.Entity;
import org.que.db.dao.ConferenceDAO;
import org.que.db.dao.SQLQuery;
import org.que.db.entities.SessionEntity;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class SessionViewFragment extends FavoriteMenuFragment {

    public static final String ARG_SESSIONVIEW_FRAGMENT = "sessionview_arg";
    private static final String TAG_SESSIONVIEW = "sessionview_tag";
    private SessionEntity session;
    private TextView title, chairs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState) {
            savedInstanceState = getArguments();
        }

        if (null != savedInstanceState) {
            session = (SessionEntity) savedInstanceState.getSerializable(TAG_SESSIONVIEW);
        }

        if (null == session) {
            session = (SessionEntity) savedInstanceState.getSerializable(ARG_SESSIONVIEW_FRAGMENT);
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
        View rootView = inflater.inflate(R.layout.fragment_sessionview, container, false);
        title = ((TextView) rootView.findViewById(R.id.session_title));
        title.setText(session.getTitle());
        chairs = ((TextView) rootView.findViewById(R.id.session_chairs));
        String chair = session.getChair();
        String coChair = session.getCoChair();
        if (chair.equals("null")) {
            chair = "N.A.";
        }
        if (coChair.equals("null")) {
            coChair = "N.A.";
        }
        chairs.setText(String.format(getString(R.string.session_chairs),
                chair,
                coChair));

        Fragment frg = new SessionSpeechesList();
        Bundle args = new Bundle();
        String select = ConferenceDBContract.ConferencePaper.COLUMN_NAME_SESSION + SQLQuery.SQL_SEARCH_EQUAL;
        SQLQuery query = new SQLQuery(select,
                                      Entity.PAPER,
                                      ConferenceDAO.PAPER_COLUMNS);

        query.setSelectionArgs(session.getId().toString());
        args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_QUERY, query);
        args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_FRAGMENT, SearchTabFragmentViewPager.TabSearch.PAPER);
        frg.setArguments(args);
        FragmentManager mgr = ((FragmentActivity) getActivity()).getSupportFragmentManager();
        mgr.beginTransaction().add(R.id.session_speeches, frg).commit();
        //Toast.makeText(getActivity(), "create "+session.getFavorite().toString(), Toast.LENGTH_SHORT).show();
        getActivity().setTitle(session.getTitle());
        setFavIcon(session.getFavorite());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TAG_SESSIONVIEW, session);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public SQLQuery getFavoriteUpdateSQLQuery() {
        String select = ConferenceDBContract.ConferenceSession.COLUMN_NAME_ID + SQLQuery.SQL_SEARCH_EQUAL;
        SQLQuery query = new SQLQuery(select, Entity.SESSION, ConferenceDAO.PAPER_COLUMNS);
        query.setSelectionArgs(session.getId().toString());
        return query;
    }

    @Override
    protected String getFavoriteColumnName() {
        return ConferenceDBContract.ConferenceSession.COLUMN_NAME_FAVORITE;
    }

    @Override
    protected Serializable getEntity() {
        return session;
    }
}
