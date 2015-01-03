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
import java.util.ArrayList;
import java.util.List;
import org.que.activities.R;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class MapFragmentPageAdapter extends FragmentPagerAdapter {

    private static int[] images = {R.drawable.map,
            R.drawable.map2};

    private List<MapImageFragment> frgmts;


    public MapFragmentPageAdapter(FragmentManager fm) {
        super(fm);
        frgmts = new ArrayList<MapImageFragment>();
        for (int i = 0; i < images.length; i++) {
            MapImageFragment f = new MapImageFragment();
            Bundle arg = new Bundle();
            arg.putInt(MapImageFragment.ARG_MAP_IMAGE_FRAGMENT, images[i % images.length]);
            f.setArguments(arg);
            frgmts.add(f);
        }

    }

    @Override
    public Fragment getItem(int i) {
        if (i >= 2)
            frgmts.get(i - 2).clear();
        return frgmts.get(i);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public int getImageResID(int pos) {
        return images[pos];
    }

}
