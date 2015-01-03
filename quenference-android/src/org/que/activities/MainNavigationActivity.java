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
package org.que.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.que.activities.fragments.AgendaViewPager;
import org.que.activities.fragments.CalendarViewPager;
import org.que.activities.fragments.MapFragment;
import org.que.activities.fragments.SearchTabFragmentViewPager;
import org.que.activities.fragments.WebviewFragment;
import org.que.activities.fragments.WelcomeFragment;

/**
 * Represents the main navigation activity with a navigation drawer. On the left
 * side of the drawer displays the navigation to other existing views. If some
 * of them are selected the current fragment are replaced with the selected view
 * (respectively fragment). That means it exists only one main fragment in the
 * center which will be replaced every time.
 *
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class MainNavigationActivity extends FragmentActivity {

  /**
   * Drawer which will be used to draw the navigation on the left side.
   */
  private DrawerLayout mDrawerLayout;
  /**
   * The ListView which shows the fragments for the navigation.
   */
  private ListView mDrawerList;
  /**
   * The ActionBar toggle which enables to toggle the navigationbar.
   */
  private ActionBarDrawerToggle mDrawerToggle;
  /**
   * The title of the actionbar.
   */
  private CharSequence mDrawerTitle;
  /**
   * The title of the current view.
   */
  private CharSequence mTitle;
  /**
   * The name of the applications.
   */
  private String[] applications;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTitle = mDrawerTitle = getTitle();
    applications = getResources().getStringArray(R.array.applications);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerList = (ListView) findViewById(R.id.left_drawer);

    // set a custom shadow that overlays the main content when the drawer opens
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener
    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
            R.layout.drawer_list_item, applications));
    
    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    // enable ActionBar app icon to behave as action to toggle nav drawer
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the sliding drawer and the action bar app icon
    mDrawerToggle = new ActionBarDrawerToggle(
            this, /* host Activity */
            mDrawerLayout, /* DrawerLayout object */
            R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
            R.string.drawer_open, /* "open drawer" description for accessibility */
            R.string.drawer_close /* "close drawer" description for accessibility */) {
      @Override
      public void onDrawerClosed(View view) {
        getActionBar().setTitle(mTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        getActionBar().setTitle(mDrawerTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }
    };
    mDrawerLayout.setDrawerListener(mDrawerToggle);
    ((DrawerItemClickListener) mDrawerList.getOnItemClickListener()).selectItem(0);
    //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (menu != null) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main, menu);

      final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        public boolean onQueryTextSubmit(String query) {
          Toast.makeText(MainNavigationActivity.this, "Submitted: " + query, Toast.LENGTH_LONG).show();
          searchView.onActionViewCollapsed(); //close search view
          startSearch(query);
          return true;
        }

        public boolean onQueryTextChange(String arg0) {
          return true;
        }
      });
    }
    return super.onCreateOptionsMenu(menu);
  }

  /* Called whenever we call invalidateOptionsMenu() */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    // If the nav drawer is open, hide action items related to the content view
    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    menu.findItem(R.id.action_search).setVisible(!drawerOpen);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // The action bar home/up action should open or close the drawer.
    // ActionBarDrawerToggle will take care of this.
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    return false;
  }

  /**
   * Starts the searching with the tipped query. The query is tipped in on the
   * navigation bar (left).
   *
   * @param query the value which is searched for
   */
  private void startSearch(String query) {
    Fragment frag = new SearchTabFragmentViewPager();
    Bundle args = new Bundle();
    args.putString(SearchTabFragmentViewPager.ARG_SEARCH_QUERY, query);
    frag.setArguments(args);

    FragmentManager fragMgr = getSupportFragmentManager();
    Fragment old = fragMgr.findFragmentById(R.id.content_frame);
    FragmentTransaction trx = fragMgr.beginTransaction();
    if (old != null) {
      trx.remove(old);
    }

    trx.add(R.id.content_frame, frag)
       //.replace(R.id.content_frame, frag)
       .addToBackStack(null) //adds to stack - if return button is called it will be returned to the main fragment
       .commit();

  }

  /*
   * The clicklistner for ListView in the navigation drawer.
   */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {

    /**
     * Contains all existing main fragments.
     */
    private final Fragment fragments[] = {new WelcomeFragment(),
                                          new AgendaViewPager(),
                                          getBrowseFragment(),
                                          getWebFragment(),
                                          new MapFragment(),
                                          new CalendarViewPager()};

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      selectItem(position);
    }

    /**
     * Selects the item which the user has clicked and starts the clicked
     * fragment (application).
     *
     * @param position the position in the list
     */
    public void selectItem(int position) {
      // update the main content by replacing fragments
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment old = fragmentManager.findFragmentById(R.id.content_frame);
      FragmentTransaction trx = fragmentManager.beginTransaction();
      if (old != null) {
        trx.remove(old);
      }

      trx.replace(R.id.content_frame, getFragmentForChoice(position))
              .addToBackStack(null) //TODO
              .commit();
      // update selected item and title, then close the drawer
      mDrawerList.setItemChecked(position, true);
      setTitle(applications[position]);
      mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * Returns for the given choice the corresponding fragment.
     * 
     * @param choice        the choice
     * @return              the corresponding fragment
     */
    private Fragment getFragmentForChoice(int choice) {
      return fragments[choice % fragments.length];
    }

    /**
     * Returns the browse fragment with his argument bundle.
     * 
     * @return              the browse fragment
     */
    private Fragment getBrowseFragment() {
      Fragment fragment = new SearchTabFragmentViewPager();
      Bundle args = new Bundle();
      args.putString(SearchTabFragmentViewPager.ARG_SEARCH_QUERY, " ");
      args.putString(SearchTabFragmentViewPager.ARG_SEARCH_TITLE, "Browse");
      fragment.setArguments(args);
      return fragment;
    }

    
    /**
     * Returns the web fragment with his argument bundle.
     * 
     * @return              the web fragment
     */
    private Fragment getWebFragment() {
      Fragment fragment = new WebviewFragment();
      Bundle args = new Bundle();
      args.putString(WebviewFragment.ARG_WEBVIEW_FRAGMENT_URL, getString(R.string.webview_url));
      args.putString(WebviewFragment.ARG_WEBVIEW_FRAGMENT_TITLE, getString(R.string.webview_info_title));
      fragment.setArguments(args);
      return fragment;
    }
  }

  @Override
  public void setTitle(CharSequence title) {
    mTitle = title;
    getActionBar().setTitle(mTitle);
  }

  /**
   * When using the ActionBarDrawerToggle, you must call it during
   * onPostCreate() and onConfigurationChanged()...
   */
  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggls
    mDrawerToggle.onConfigurationChanged(newConfig);
  }
}