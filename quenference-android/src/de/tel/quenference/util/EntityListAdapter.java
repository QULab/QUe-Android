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
package de.tel.quenference.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import de.tel.quenference.activities.R;
import de.tel.quenference.activities.fragments.SearchTabFragmentViewPager;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.entities.AuthorEntity;
import de.tel.quenference.db.entities.PaperEntity;
import de.tel.quenference.db.entities.SessionEntity;
import de.tel.quenference.util.calender.CalendarHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The ItemAdapter to display each single result of the search in the ListView.
 *
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class EntityListAdapter extends BaseAdapter implements SectionIndexer {

  /**
   * String which contains, null as char sequence.
   */
  private static final String NULL_STRING = "null";
  /**
   * The results of the search.
   */
  private List results;
  /**
   * Contains the sections with the corresponding index of the first element in
   * the section in the result list.
   */
  private HashMap<String, Integer> sectionIndexes;
  /**
   * Contains the sections.
   */
  private String[] sections;
  /**
   * The entities of the result list.
   */
  private SearchTabFragmentViewPager.TabSearch entity;
  /**
   * The application context.
   */
  private Context context;

  /**
   * The ctor of the QueryItemAdapter.
   *
   * @param r the results of the search
   */
  public EntityListAdapter(List r, SearchTabFragmentViewPager.TabSearch search, Context c) {
    this.results = r;
    this.entity = search;
    this.context = c;
    createSections();
  }

  /**
   * Creates the sections for the given result list.
   */
  private void createSections() {
    sectionIndexes = new HashMap<String, Integer>();
    int size = results.size();
    for (int i = 0; i < size; i++) {
      String str = getSectionForEntity(i);
      if (str != null) {
        if (!sectionIndexes.containsKey(str)) {
          sectionIndexes.put(str, i);
        }
      }
      ArrayList<String> sectionList = new ArrayList<String>(sectionIndexes.keySet());
      Collections.sort(sectionList);
      sections = new String[sectionList.size()];
      sectionList.toArray(sections);
    }
  }

  /**
   * Returns for the given i (position in the result list) the corresponding
   * section.
   *
   * @param i the position in the result list
   * @return the corresponding section of the entity
   */
  private String getSectionForEntity(int i) {
    String str = null;
    if (SearchTabFragmentViewPager.TabSearch.AUTHOR == entity) {
      str = ((AuthorEntity) results.get(i)).getFirstName();
    } else if (SearchTabFragmentViewPager.TabSearch.PAPER == entity) {
      str = ((PaperEntity) results.get(i)).getTitle();
    } else if (SearchTabFragmentViewPager.TabSearch.SESSION == entity) {
      str = ((SessionEntity) results.get(i)).getTitle();
    }
    return str != null ? str.substring(0, 1).toUpperCase() : null;
  }

  @Override
  public int getCount() {
    return results.size();
  }

  @Override
  public Object getItem(int arg0) {
    return results.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }

  /**
   * Returns the results of the search.
   *
   * @return the results
   */
  public List getResults() {
    return results;
  }

  /**
   * Replaces the results of the search.
   *
   * @param results the results
   */
  public void setResults(List results) {
    this.results = results;
    createSections();
  }

  @Override
  public View getView(int position, View convertview, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View row = inflater.inflate(R.layout.fragment_search_result_row, parent, false);
    TextView title = (TextView) row.findViewById(R.id.search_result_row_title);

    if (SearchTabFragmentViewPager.TabSearch.AUTHOR == entity) {
      row = getAuthorView(row, position, title);
    } else if (SearchTabFragmentViewPager.TabSearch.PAPER == entity) {
      row = getPaperView(row, position, title);
    } else if (SearchTabFragmentViewPager.TabSearch.SESSION == entity) {
      row = getSessionView(row, position, title);
    } else if (SearchTabFragmentViewPager.TabSearch.AGENDA == entity) {
      row = getCalendarView(inflater.inflate(R.layout.fragment_agenda_result_row, parent, false), position);
    }

    return row;
  }

  /**
   * Returns the author entry view.
   * 
   * @param row the view which should be filled 
   * @param position  the position of the entity in the result list
   * @param title the title text view of the view
   * @return  the entry view
   */
  private View getAuthorView(View row, int position, TextView title) {
    AuthorEntity a = (AuthorEntity) results.get(position);
    title.setText(a.getFirstName() + " " + a.getLastName());
    if (a.getAffiliation() != null && !a.getAffiliation().equalsIgnoreCase(NULL_STRING)) {
      TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
      descrp.setText(a.getAffiliation());
    }
    return row;
  }

  /**
   * Returns the paper entry view.
   * 
   * @param row the view which should be filled 
   * @param position  the position of the entity in the result list
   * @param title the title text view of the view
   * @return  the entry view
   */
  private View getPaperView(View row, int position, TextView title) {
    PaperEntity p = (PaperEntity) results.get(position);
    title.setText(p.getTitle());
    if (p.getMainAuthor() != null && !p.getMainAuthor().equalsIgnoreCase(NULL_STRING)) {
      TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
      descrp.setText(p.getMainAuthor());
    }
    if (p.getSession() != null) {
      TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
      //TODO #2 make asynchron
      SessionEntity paperSession = ConferenceDAO.getSessionByID(p.getSession().toString(), context);
      String[] times = CalendarHelper.getDayAndTimeFromSession(paperSession);
      descrp.append("\n" + paperSession.getTitle());
      descrp.append("\n" + times[0] + " - " + times[1] + ", " + paperSession.getRoom());
    }
    return row;
  }

  /**
   * Returns the session entry view.
   * 
   * @param row the view which should be filled 
   * @param position  the position of the entity in the result list
   * @param title the title text view of the view
   * @return  the entry view
   */
  private View getSessionView(View row, int position, TextView title) {
    SessionEntity s = (SessionEntity) results.get(position);
    title.setText(s.getTitle());

    if (s.getRoom() != null && !s.getRoom().equalsIgnoreCase(NULL_STRING)) {
      TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
      String[] times = CalendarHelper.getDayAndTimeFromSession(s);
      descrp.append("\n" + s.getRoom());
      descrp.append("\n" + times[0] + " - " + times[1]);
    }
    setChairToView((TextView) row.findViewById(R.id.search_result_row_description), s);
    return row;
  }

  /**
   * Returns the calendar entry view.
   *
   * @param row the view which should be filled
   * @param position the position of the entity
   * @return the calendar entry view
   */
  private View getCalendarView(View row, int position) {
    TextView agendaStart = (TextView) row.findViewById(R.id.agenda_start);
    TextView agendaEnd = (TextView) row.findViewById(R.id.agenda_end);
    TextView room = (TextView) row.findViewById(R.id.agenda_room);
    TextView title = (TextView) row.findViewById(R.id.agenda_result_row_title);

    SessionEntity s = (SessionEntity) results.get(position);
    title.setText(s.getTitle());
    String[] arDate = CalendarHelper.getTimeFromSession(s);
    agendaStart.setText(arDate[0]);
    agendaEnd.setText(arDate[1]);
    room.setText(s.getRoom());
    setChairToView((TextView) row.findViewById(R.id.agenda_result_row_description), s);
    return row;
  }

  /**
   * Set the chair information of the session to the view entry.
   * 
   * @param descrp the text view which gets the new information
   * @param s the session which contains the information
   */
  private void setChairToView(TextView descrp, SessionEntity s) {
    if (s.getChair() != null && !s.getChair().equalsIgnoreCase(NULL_STRING)) {
      descrp.setText(s.getChair());
      if (s.getCoChair() != null && !s.getCoChair().equalsIgnoreCase(NULL_STRING)) {
        descrp.append(context.getResources().getString(R.string.and));
        descrp.append(s.getCoChair());
      }
    }
  }

  /**
   * Returns the exiting sections.
   *
   * @return the sections
   */
  public Object[] getSections() {
    return sections;
  }

  /**
   * Returns for the given section id the corresponding position of the first
   * element for that section.
   *
   * @param arg0 the section id
   * @return the position
   */
  public int getPositionForSection(int arg0) {
    return sectionIndexes.get(sections[arg0]);
  }

  /**
   * Returns the section for the given position in the result list.
   *
   * @param arg0 the position
   * @return the section
   */
  public int getSectionForPosition(int arg0) {
    String s = getSectionForEntity(arg0);
    boolean found = false;
    int pos = 0;
    for (int i = 0; i < sections.length && !found; i++) {
      if (sections[i].equalsIgnoreCase(s)) {
        found = true;
        pos = i;
      }
    }
    return pos;
  }
}
