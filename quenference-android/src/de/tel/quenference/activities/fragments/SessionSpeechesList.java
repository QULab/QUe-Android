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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.tel.quenference.activities.fragments.SearchTabFragmentViewPager.TabSearch;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.dao.AsyncDBListReader;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.dao.SQLQuery;
import de.tel.quenference.db.entities.PaperEntity;
import java.util.List;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class SessionSpeechesList extends SearchFragmentVP {


    public ListAdapter getListAdapter(TabSearch searchfrg) {
        //Log.d(SessionSpeechesList.class.getName(), "GETLIST ADAPTER!!!!");
        return new SessionSpeechesListAdapter(getActivity());
    }


    @Override
    protected void search() {
        if (query == null) {
            String selection = ConferenceDBContract.ConferencePaper.COLUMN_NAME_SESSION + SQLQuery.SQL_SEARCH_EQUAL;
            query = new SQLQuery(selection, ConferenceDAO.Entity.PAPER);
            query.setSelectionArgs(new String[]{"1"});
        }

        ConferenceDAO.getSelection(getActivity(), query.getSelectedEntity(), new AsyncDBListReader.PostExecuteJob() {

            public void doJob(final List result) {
                ((SessionSpeechesListAdapter) getListAdapter()).setSpeeches(result);
                ((SessionSpeechesListAdapter) getListAdapter()).notifyDataSetChanged();
            }
        }, query.getSelection(), query.getSelectionArgs(), null, null, query.getOrderBy());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//    setListAdapter(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getListView() != null) {
            getListView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (getListAdapter().getItem(position) instanceof PaperEntity)
            super.onListItemClick(l, v, position, id);
        //else do nothing - section was clicked
    }


}
