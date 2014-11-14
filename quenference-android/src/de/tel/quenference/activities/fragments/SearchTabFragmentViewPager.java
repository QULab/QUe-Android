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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.tel.quenference.activities.R;
import de.tel.quenference.db.dao.SQLQuery;

/**
 * @author deLaczkovich, Christopher Zell <zelldon91@googlemail.com>
 */
public class SearchTabFragmentViewPager extends Fragment {


  /**
   * The enum contains the existing tabs for the search.
   */
  public enum TabSearch {

    AUTHOR, PAPER, SESSION, AGENDA
  }
  /**
   * The tag for the for the search value.
   */
  private static final String TAG_SEARCH_FRAGMENT = "searchFrag";
  /**
   * The argument key for the search value.
   */
  public static final String ARG_SEARCH_QUERY = "searchQuery";
  /**
   * The argument key for the title of the search.
   */
  public static final String ARG_SEARCH_FRAGMENT = "searchTitle";
  //the title of the page
  public static final String ARG_SEARCH_TITLE = "pageTitle";
  /**
   * The values to search for.
   */
  private String searchValue;
  private String pageTitle = "noTitle";

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (null == savedInstanceState) {
      savedInstanceState = getArguments();
    }

    if (null != savedInstanceState) {
      searchValue = savedInstanceState.getString(TAG_SEARCH_FRAGMENT);
    }

    if (null == searchValue) {
      searchValue = savedInstanceState.getString(ARG_SEARCH_QUERY);
      if (savedInstanceState.getString(ARG_SEARCH_TITLE) != null) {
        pageTitle = savedInstanceState.getString(ARG_SEARCH_TITLE);
      }
    }

  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_search_viewpager, container, false);

    ViewPager viewPager = (ViewPager) root.findViewById(R.id.search_tab_viewPager);
    /**
     * Important: Must use the child FragmentManager or you will see side
     * effects.
     */
    viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

    return root;
  }

  public class MyAdapter extends FragmentPagerAdapter {

    public MyAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return 3; //Author, Paper, Session
    }

    //get Item basically calls the subFragments using the newInstance method
    @Override
    public Fragment getItem(int position) {
      Bundle args = new Bundle();

      if (position == 0) {
        args.putSerializable(ARG_SEARCH_QUERY, SQLQuery.getAuthorQuery(searchValue));
        args.putSerializable(ARG_SEARCH_FRAGMENT, TabSearch.AUTHOR);
      }
      if (position == 1) {
        args.putSerializable(ARG_SEARCH_QUERY, SQLQuery.getPaperQuery(searchValue));
        args.putSerializable(ARG_SEARCH_FRAGMENT, TabSearch.PAPER);
      }
      if (position == 2) {
        args.putSerializable(ARG_SEARCH_QUERY, SQLQuery.getSessionQuery(searchValue));
        args.putSerializable(ARG_SEARCH_FRAGMENT, TabSearch.SESSION);
      }
      return SearchFragmentVP.newInstance(args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
      String title = "TabTitle";
      if (position == 0) {
        //AUTHOR TAB
        title = getString(R.string.search_author_tab);
      }
      if (position == 1) {
        //PAPER TAB
        title = getString(R.string.search_paper_tab);
      }

      if (position == 2) {
        //SESSION TAB
        title = getString(R.string.search_session_tab);
      }
      if (pageTitle.equals("noTitle")) {
        getActivity().setTitle(getResources().getString(R.string.search_title));
      } else {
        getActivity().setTitle(pageTitle);
      }
      return title;
    }
  }
}
