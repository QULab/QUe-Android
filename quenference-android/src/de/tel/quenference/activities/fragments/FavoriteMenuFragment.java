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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import de.tel.quenference.activities.R;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.dao.SQLQuery;
import de.tel.quenference.util.calender.CalendarHelper;
import java.io.Serializable;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public abstract class FavoriteMenuFragment extends Fragment {

  protected MenuItem item;
  protected boolean favorited;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  abstract SQLQuery.Builder getFavoriteUpdateSQLQuery();

  abstract String getFavoriteColumnName();

  abstract Serializable getEntity();

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.fav_menu, menu);
    item = menu.findItem(R.id.action_favorite);
    if (favorited) {
      item.setIcon(R.drawable.ic_favorite_set);
      item.setChecked(favorited);
    } else {
      item.setIcon(R.drawable.ic_favorite);
      item.setChecked(favorited);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().invalidateOptionsMenu();
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    //TODO BUG FIX - Nicholas look over it
//        getActivity().invalidateOptionsMenu(); 
    item = menu.findItem(R.id.action_favorite);
    if (favorited) {
      item.setIcon(R.drawable.ic_favorite_set);
      item.setChecked(favorited);
    } else {
      item.setIcon(R.drawable.ic_favorite);
      item.setChecked(favorited);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem favItem) {
    if (favItem.getItemId() == R.id.action_favorite) {
      SQLQuery.Builder builder = getFavoriteUpdateSQLQuery();

      Integer fav;
      if (favItem.isChecked()) { //UNfavorited
        setFavIcon(false);
        fav = 0;
        CalendarHelper.deleteItemFromCalendar(getEntity(), getActivity());
        Toast.makeText(getActivity(), "Removing item from favorites", Toast.LENGTH_SHORT).show();
      } else {
        setFavIcon(true);
        fav = 1;
        CalendarHelper.attemptToAddToCalendar(getActivity(), getEntity());
        Toast.makeText(getActivity(), "Added item to favorites", Toast.LENGTH_SHORT).show();
      }

      builder.addValues(getFavoriteColumnName(), fav.toString());
      ConferenceDAO.updateEntity(getActivity(), builder.build());
    }

    return super.onOptionsItemSelected(favItem);
  }

  public void setFavIcon(boolean set) {
    favorited = set;
    if (item != null) {
      if (set) {
        item.setIcon(R.drawable.ic_favorite_set);
        item.setChecked(set);
      } else {
        item.setIcon(R.drawable.ic_favorite);
        item.setChecked(set);
      }
    }
  }
}
