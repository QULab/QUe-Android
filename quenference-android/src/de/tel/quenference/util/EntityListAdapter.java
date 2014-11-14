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

    private HashMap<String, Integer> sectionIndexes;
    private String[] sections;
    private SearchTabFragmentViewPager.TabSearch entity;
    private Context context;
    private String title;

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

    private void createSections() {
        sectionIndexes = new HashMap<String, Integer>();
        int size = results.size();
        for (int i = 0; i < size; i++) {
            String str = null;
            if (SearchTabFragmentViewPager.TabSearch.AUTHOR == entity) {
                str = ((AuthorEntity) results.get(i)).getFirstName();
            } else if (SearchTabFragmentViewPager.TabSearch.PAPER == entity) {
                str = ((PaperEntity) results.get(i)).getTitle();
            } else if (SearchTabFragmentViewPager.TabSearch.SESSION == entity) {
                str = ((SessionEntity) results.get(i)).getTitle();
            }
            if (str != null) {
                String firstLetter = str.substring(0, 1).toUpperCase();
                if (!sectionIndexes.containsKey(firstLetter)) {
                    sectionIndexes.put(firstLetter, i);
                }
            }
            ArrayList<String> sectionList = new ArrayList<String>(sectionIndexes.keySet());
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);
        }
    }

    /*The EntitiyListAdapter for the Agenda which has a variable amount of days*/
    public EntityListAdapter(List r, String pageTitle, Context c) {
        this.results = r;
        this.title = pageTitle;
        this.context = c;
        createSectionsFromPageTitles();
    }

    private void createSectionsFromPageTitles() {
        sectionIndexes = new HashMap<String, Integer>();
        int size = results.size();
        for (int i = 0; i < size; i++) {
            String str = null;
            if (SearchTabFragmentViewPager.TabSearch.AUTHOR == entity) {
                str = ((AuthorEntity) results.get(i)).getFirstName();
            } else if (SearchTabFragmentViewPager.TabSearch.PAPER == entity) {
                str = ((PaperEntity) results.get(i)).getTitle();
            } else if (SearchTabFragmentViewPager.TabSearch.SESSION == entity) {
                str = ((SessionEntity) results.get(i)).getTitle();
            }
            if (str != null) {
                String firstLetter = str.substring(0, 1).toUpperCase();
                if (!sectionIndexes.containsKey(firstLetter)) {
                    sectionIndexes.put(firstLetter, i);
                }
            }
            ArrayList<String> sectionList = new ArrayList<String>(sectionIndexes.keySet());
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);
        }
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
    //TODO REFACTOR METHOD - especially the calender call !!!!!!!! -.-
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.fragment_search_result_row, parent, false);

        TextView title = (TextView) row.findViewById(R.id.search_result_row_title);

        if (SearchTabFragmentViewPager.TabSearch.AUTHOR == entity) {
            AuthorEntity a = (AuthorEntity) results.get(position);
            title.setText(a.getFirstName() + " " + a.getLastName());
            if (a.getAffiliation() != null && !a.getAffiliation().equalsIgnoreCase(NULL_STRING)) {
                TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
                descrp.setText(a.getAffiliation());
            }
        } else if (SearchTabFragmentViewPager.TabSearch.PAPER == entity) {
            PaperEntity p = (PaperEntity) results.get(position);
            title.setText(p.getTitle());
            if (p.getMainAuthor() != null && !p.getMainAuthor().equalsIgnoreCase(NULL_STRING)) {
                TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
                descrp.setText(p.getMainAuthor());
            }
            if (p.getSession() != null) {
                TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
                SessionEntity paperSession = ConferenceDAO.getSessionByID(p.getSession().toString(), context);
                descrp.append("\n" + paperSession.getTitle());
                descrp.append("\n" + CalendarHelper.getDayAndTimeFromSession(paperSession)[0] + " - "
                        + CalendarHelper.getDayAndTimeFromSession(paperSession)[1] + ", " + paperSession.getRoom());
            }
        } else if (SearchTabFragmentViewPager.TabSearch.SESSION == entity) {
            SessionEntity s = (SessionEntity) results.get(position);
            title.setText(s.getTitle());


            if (s.getChair() != null && !s.getChair().equalsIgnoreCase(NULL_STRING)) {
                TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
                descrp.setText(s.getChair());
                if (s.getCoChair() != null && !s.getCoChair().equalsIgnoreCase(NULL_STRING)) {
                    descrp.append(" and ");
                    descrp.append(s.getCoChair());
                }
            }
            if (s.getRoom() != null && !s.getRoom().equalsIgnoreCase(NULL_STRING)) {
                TextView descrp = (TextView) row.findViewById(R.id.search_result_row_description);
                descrp.append("\n" + s.getRoom());
                descrp.append("\n" + CalendarHelper.getDayAndTimeFromSession(s)[0] + " - " + CalendarHelper.getDayAndTimeFromSession(s)[1]);
            }

        } else if (SearchTabFragmentViewPager.TabSearch.AGENDA == entity) {
            row = inflater.inflate(R.layout.fragment_agenda_result_row, parent, false);
            TextView agendaStart = (TextView) row.findViewById(R.id.agenda_start);
            TextView agendaEnd = (TextView) row.findViewById(R.id.agenda_end);
            TextView room = (TextView) row.findViewById(R.id.agenda_room);
            CalendarHelper calendarHelper = new CalendarHelper();

            title = (TextView) row.findViewById(R.id.agenda_result_row_title);
            SessionEntity s = (SessionEntity) results.get(position);
            String[] arDate = new String[2];
            title.setText(s.getTitle());
            //gets the proper hours of the day for the sessions
            arDate[0] = calendarHelper.getTimeFromSession(s)[0];
            arDate[1] = calendarHelper.getTimeFromSession(s)[1];
            agendaStart.setText(arDate[0]);
            agendaEnd.setText(arDate[1]);
            room.setText(s.getRoom());

            if (s.getChair() != null && !s.getChair().equalsIgnoreCase(NULL_STRING)) {
                TextView descrp = (TextView) row.findViewById(R.id.agenda_result_row_description);
                descrp.setText(s.getChair());
                if (s.getCoChair() != null && !s.getCoChair().equalsIgnoreCase(NULL_STRING)) {
                    descrp.append(" and ");
                    descrp.append(s.getCoChair());
                }
            }
        }

        return row;
    }

    public Object[] getSections() {
        return sections;
    }

    public int getPositionForSection(int arg0) {
        return sectionIndexes.get(sections[arg0]);
    }

    public int getSectionForPosition(int arg0) {
        return 0;
    }

}
