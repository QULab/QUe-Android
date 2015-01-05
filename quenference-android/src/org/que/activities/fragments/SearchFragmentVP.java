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
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.que.activities.R;
import org.que.db.ConferenceDBContract;
import org.que.db.Entity;
import org.que.db.dao.AsyncDBListReader;
import org.que.db.dao.ConferenceDAO;
import org.que.db.dao.SQLQuery;
import org.que.db.entities.AuthorEntity;
import org.que.db.entities.PaperEntity;
import org.que.db.entities.SessionEntity;
import org.que.util.EntityListAdapter;

/**
 * The search fragment which shows the result for a determined search. E.g. for
 * Paper, Author or Session search.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class SearchFragmentVP extends ListFragment {

  /**
   * The tag for the saved search value.
   */
  protected static final String TAG_SEARCH_VALUE = "searchValue";
  /**
   * The tag for the saved search title.
   */
  protected static final String TAG_SEARCH_FRAGMENT = "searchFrag";
  /**
   * The value to search for.
   */
  protected SQLQuery query;
  /**
   * The title of the search (identifies the table for the searching).
   */
  protected SearchTabFragmentViewPager.TabSearch searchFragmentVP;
  private Date d;
  private ViewGroup locContainer;
  private int index = -1;
  private int top = 0;

  public ListAdapter getListAdapter(SearchTabFragmentViewPager.TabSearch searchfrg) {
    return new EntityListAdapter(new ArrayList(), searchFragmentVP, this.getActivity());
  }

  @Override
  public void onResume() {
    super.onResume();
    if (index != -1) {
      this.getListView().setSelectionFromTop(index, top);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    try {
      index = this.getListView().getFirstVisiblePosition();
      View v = this.getListView().getChildAt(0);
      top = (v == null) ? 0 : v.getTop();
    } catch (Throwable t) {
      t.printStackTrace();
    }

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    d = new Date();

    if (null == savedInstanceState) {
      savedInstanceState = getArguments();
    }

    if (null != savedInstanceState) {
      query = (SQLQuery) savedInstanceState.getSerializable(TAG_SEARCH_VALUE);
      searchFragmentVP = (SearchTabFragmentViewPager.TabSearch) savedInstanceState.getSerializable(TAG_SEARCH_FRAGMENT);
    }

    if (null == query) {
      query = (SQLQuery) savedInstanceState.getSerializable(SearchTabFragmentViewPager.ARG_SEARCH_QUERY);
    }

    if (null == searchFragmentVP) {
      searchFragmentVP = (SearchTabFragmentViewPager.TabSearch) savedInstanceState.getSerializable(SearchTabFragmentViewPager.ARG_SEARCH_FRAGMENT);
    }
    setListAdapter(getListAdapter(searchFragmentVP));
    search();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    //only set the fastscroll for authors view. It glitches often everywhere else.
    if (searchFragmentVP == SearchTabFragmentViewPager.TabSearch.AUTHOR) {
      getListView().setFastScrollEnabled(true);
    } else {
      getListView().setFastScrollEnabled(false);
    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    locContainer = container;

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Fragment frg = null;
    Bundle args = new Bundle();
    d = new Date();

    if (searchFragmentVP == SearchTabFragmentViewPager.TabSearch.PAPER) {
      PaperEntity entity = (PaperEntity) getListAdapter().getItem(position);
      frg = new PaperviewFragment();
      args.putSerializable(PaperviewFragment.ARG_PAPERVIEW_FRAGMENT, entity);
    } else if (searchFragmentVP == SearchTabFragmentViewPager.TabSearch.AUTHOR) {
      AuthorEntity entity = (AuthorEntity) getListAdapter().getItem(position);
      frg = new AuthorviewFragment();
      args.putSerializable(AuthorviewFragment.ARG_AUTHORVIEW_FRAGMENT, entity);

    } else if (searchFragmentVP == SearchTabFragmentViewPager.TabSearch.SESSION) {
      SessionEntity entity = (SessionEntity) getListAdapter().getItem(position);
      frg = new SessionViewFragment();
      args.putSerializable(SessionViewFragment.ARG_SESSIONVIEW_FRAGMENT, entity);

    } else if (searchFragmentVP == SearchTabFragmentViewPager.TabSearch.AGENDA) {
      SessionEntity entity = (SessionEntity) getListAdapter().getItem(position);
      frg = new SessionViewFragment();
      args.putSerializable(SessionViewFragment.ARG_SESSIONVIEW_FRAGMENT, entity);
    }

    if (frg != null) {
      frg.setArguments(args);
      FragmentManager mgr = ((FragmentActivity) getActivity()).getSupportFragmentManager();
      Fragment old = mgr.findFragmentById(R.id.content_frame);

      FragmentTransaction trx = mgr.beginTransaction();
      if (old != null) {
        trx.remove(old);
      }

      trx.add(R.id.content_frame, frg)
              .addToBackStack(null) //TODO
              .commit();

    }
  }

  /**
   * Executes the search for the given query in the SQLiteDatabase. The query is
   * saved in the field searchValue.
   */
  protected void search() {

    if (query.getSelectedEntity() == Entity.SESSION) {
      query.setOrderBy(ConferenceDBContract.ConferenceSession.COLUMN_NAME_DATETIME + " ASC");
      ConferenceDAO.getSelection(getActivity(), new AsyncDBListReader.PostExecuteJob() {
        public void doJob(List result) {
          ((EntityListAdapter) getListAdapter()).setResults(result);
          ((EntityListAdapter) getListAdapter()).notifyDataSetChanged();
        }
      }, query);
    } else {
      ConferenceDAO.getSelection(getActivity(), new AsyncDBListReader.PostExecuteJob() {
        public void doJob(List result) {
          ((EntityListAdapter) getListAdapter()).setResults(result);
          ((EntityListAdapter) getListAdapter()).notifyDataSetChanged();
        }
      }, query);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(TAG_SEARCH_FRAGMENT, searchFragmentVP);
    outState.putSerializable(TAG_SEARCH_VALUE, query);
  }

  public static SearchFragmentVP newInstance(Bundle args) {
    SearchFragmentVP fragment = new SearchFragmentVP();
    fragment.setArguments(args);
    return fragment;
  }
}
