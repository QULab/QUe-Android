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
import org.que.activities.R;
import org.que.db.ConferenceDBContract;
import org.que.db.Entity;
import org.que.db.dao.ConferenceDAO;
import org.que.db.dao.SQLQuery;
import org.que.db.entities.AuthorEntity;

/**
 * Represents the Fragment which shows the information of the Author.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AuthorFragment extends Fragment {

  /**
   * The argument key of the fragment.
   */
  public static final String ARG_AUTHORVIEW_FRAGMENT = "author_arg";
  
  /**
   * The tag of the author view.
   */
  private static final String TAG_AUTHORVIEW = "author_tag";
  
  /**
   * The author entity which contains the author informations.
   */
  private AuthorEntity author;
  
  /**
   * The TextViews which should show the author informations.
   */
  private TextView name, affiliation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (null == savedInstanceState) {
      savedInstanceState = getArguments();
    }

    if (null != savedInstanceState) {
      author = (AuthorEntity) savedInstanceState.getSerializable(TAG_AUTHORVIEW);
    }

    if (null == author) {
      author = (AuthorEntity) savedInstanceState.getSerializable(ARG_AUTHORVIEW_FRAGMENT);
    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_authorview, container, false);
//    name = ((TextView) rootView.findViewById(R.id.author_name));
//    name.setText(author.getFullName());
    affiliation = ((TextView) rootView.findViewById(R.id.author_affiliation));
    affiliation.setText(author.getAffiliation());
    Fragment frg = new AuthorPapersListFragment();
    Bundle args = new Bundle();
    String select = ConferenceDBContract.ConferencePaperAuthors.COLUMN_NAME_AUTHOR_ID + SQLQuery.SQL_SEARCH_EQUAL;
    SQLQuery query = new SQLQuery(select,
                                  Entity.PAPER_AUTHORS,
                                  ConferenceDAO.PAPER_AUTHORS_COLUMNS);

    query.setSelectionArgs(author.getId().toString());
    args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_QUERY, query);
    args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_FRAGMENT, SearchTabFragmentViewPager.TabSearch.PAPER);
    frg.setArguments(args);
    FragmentManager mgr = ((FragmentActivity) getActivity()).getSupportFragmentManager();
    mgr.beginTransaction().add(R.id.author_papers, frg).commit();


    getActivity().setTitle(author.getFullName());
    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(TAG_AUTHORVIEW, author);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}