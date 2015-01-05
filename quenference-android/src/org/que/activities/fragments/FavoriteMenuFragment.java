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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.Serializable;
import org.que.activities.R;
import org.que.db.dao.ConferenceDAO;
import org.que.db.dao.SQLQuery;
import org.que.util.calender.CalendarHelper;

/**
 * Represents the favorite options menu which will be shown in the action bar
 * of the navigation drawer.
 * 
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public abstract class FavoriteMenuFragment extends Fragment {

  /**
   * The favorite menu item.
   */
  protected MenuItem item;
  
  /**
   * The flag indicates whether the favorite is set or not.
   */
  protected boolean favorited;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  /**
   * Returns the SQLQuery which should be used to update the favorite column
   * of the entity.
   * 
   * @return the SQLQuery
   */
  protected abstract SQLQuery getFavoriteUpdateSQLQuery();

  /**
   * Returns the favorite column name of the entity.
   * 
   * @return the favorite column name
   */
  protected abstract String getFavoriteColumnName();

  /**
   * Returns the current entity which can be favoured.
   * 
   * @return the entity
   */
  protected abstract Serializable getEntity();

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
      SQLQuery query = getFavoriteUpdateSQLQuery();

      Integer fav;
      if (favItem.isChecked()) { //UNfavorited
        setFavIcon(false);
        fav = 0;
        CalendarHelper.deleteItemFromCalendar(getEntity(), getActivity());
        Toast.makeText(getActivity(), getString(R.string.removed), Toast.LENGTH_SHORT).show();
      } else {
        setFavIcon(true);
        fav = 1;
        CalendarHelper.attemptToAddToCalendar(getActivity(), getEntity());
        Toast.makeText(getActivity(), getString(R.string.added), Toast.LENGTH_SHORT).show();
      }

      query.addValues(getFavoriteColumnName(), fav.toString());
      ConferenceDAO.updateEntity(getActivity(), query);
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
