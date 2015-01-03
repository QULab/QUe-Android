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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.que.activities.R;
import org.que.db.dao.ConferenceDAO;

/**
 * Created by deLaczkovich on 25-Jul-14.
 */
public class AgendaViewPager extends Fragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

    int currentDayPage = 0;
    boolean firstTimeOpened = true;

    @Override
    public void onPause() {
        //save the current page for back navigation
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.agendaFragmentViewPager);
        currentDayPage = viewPager.getCurrentItem();
        firstTimeOpened = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        super.onResume();
        //Switch to the view that is the current day unless this page was navigated back to and was on another page
        String[] conferenceDates = getResources().getStringArray(R.array.Conference_Dates);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date now = new Date();
        if (firstTimeOpened) {
            for (int i = 0; i < conferenceDates.length - 1; i++) {
                try {
                    Log.d("calVP", "current time is: " + now.getTime() + " lowEnd is:" +
                            sdf.parse(conferenceDates[i]).getTime() + " high end is: " + sdf.parse(conferenceDates[i]).getTime());
                    if (now.getTime() > sdf.parse(conferenceDates[i]).getTime() && now.getTime() < sdf.parse(conferenceDates[i + 1]).getTime()) {
                        currentDayPage = i;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.agendaFragmentViewPager);
        viewPager.setCurrentItem(currentDayPage);
    }

    @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_agenda_viewpage, container, false);

    ViewPager viewPager = (ViewPager) root.findViewById(R.id.agendaFragmentViewPager);
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
      String[] conferenceDates = getResources().getStringArray(R.array.Conference_Dates);
      return conferenceDates.length;
    }

    //get Item basically calls the subFragments using the newInstance method
    @Override
    public Fragment getItem(int position) {
      String day = Integer.toString(position);
      Bundle args = new Bundle();
      args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_QUERY, ConferenceDAO.getSessionDateOrderQuery(day));
      args.putSerializable(SearchTabFragmentViewPager.ARG_SEARCH_FRAGMENT, SearchTabFragmentViewPager.TabSearch.AGENDA);
//            String[] conferenceDates = getResources().getStringArray(R.array.Conference_Dates);
      return SearchFragmentVP.newInstance(args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
      //Get the current date and today and tomorrow in order to compare the conference dates with the current days.
      Calendar dateHelperYesterday = Calendar.getInstance();
      Calendar dateHelperToday = Calendar.getInstance();
      Calendar dateHelperTomorrow = Calendar.getInstance();
      dateHelperYesterday.add(Calendar.DATE, -1);
      dateHelperTomorrow.add(Calendar.DATE, +1);

      //get the conference dates
      String[] conferenceDates = getResources().getStringArray(R.array.Conference_Dates);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      String yesterday = sdf.format((dateHelperYesterday.getTime()));
      String today = sdf.format(dateHelperToday.getTime());
      String tomorrow = sdf.format(dateHelperTomorrow.getTime());


      String confDayDate = null;
      try {
        confDayDate = sdf.format(sdf.parse(conferenceDates[position]));
      } catch (ParseException e) {
        Log.e(AgendaViewPager.class.getName(), "Wrong Date Format in XML Resource!", e);
      }
      if (confDayDate.equals(today)) {
        confDayDate = "Today";
      } else if (confDayDate.equals(yesterday)) {
        confDayDate = "Yesterday";
      } else if (confDayDate.equals(tomorrow)) {
        confDayDate = "Tomorrow";
      }
      return confDayDate;
    }
  }
}
