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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.que.activities.R;

/**
 * Represents the main Map Fragment which shows the Map.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class MapFragment extends Fragment {
  
  /**
   * The argument key of the fragment.
   */
  public static final String ARG_MAP_FRAGMENT = "mapFrag";
  
  /**
   * The page adapter for the map.
   */
  private MapFragmentPageAdapter adapter;
  
  /**
   * The pager which shows the different maps.
   */
  private ViewPager pager;
  
  /**
   * The page indicator which shows the current page information.
   */
  private TextView pageIndicator;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    
    View rootView = inflater.inflate(R.layout.fragment_mapview, container, false);
    
    
    adapter = new MapFragmentPageAdapter(getChildFragmentManager());//getActivity().getSupportFragmentManager());
    
    pager = (ViewPager) rootView.findViewById(R.id.map_pager);
    pager.setAdapter(adapter);
    
    pageIndicator = (TextView) rootView.findViewById(R.id.map_indicator);
    setTextToIndicator(0);
    pager.setOffscreenPageLimit(0);
    pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

              public void onPageScrolled(int i, float f, int i1) {
              }

              public void onPageSelected(int i) {
                setTextToIndicator(i);
              }

              public void onPageScrollStateChanged(int i) {
              }
            });
    
    return rootView;
  }
  
  /**
   * Sets the page indication information.
   * 
   * @param page the page indication
   */
  private void setTextToIndicator(int page) {
    int size = pager.getAdapter().getCount();
    String indicator = String.format(getString(R.string.map_indicator_str), page+1, size);
    pageIndicator.setText(indicator);
  }
  
}
