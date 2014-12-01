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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.tel.quenference.activities.R;
import de.tel.quenference.db.ConferenceDBContract;
import de.tel.quenference.db.Entity;
import de.tel.quenference.db.dao.SQLQuery;
import de.tel.quenference.db.entities.AuthorEntity;

/**
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class AuthorviewFragment extends Fragment {
  
  public static final String ARG_AUTHORVIEW_FRAGMENT = "author_arg";
  private static final String TAG_AUTHORVIEW = "author_tag";
  
  private AuthorEntity author;
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
    SQLQuery.Builder builder = new SQLQuery.Builder(select,
                                                    Entity.PAPER_AUTHORS);
    
    builder.addArgs(author.getId().toString());
    args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_QUERY, builder.build());
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
