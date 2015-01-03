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

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.que.activities.R;
import org.que.db.entities.PaperEntity;

/**
 * @author Christopher Zell <zelldon91@googlemail.com>
 */
public class SessionSpeechesListAdapter extends BaseAdapter {

    private SparseArray<PaperEntity> speeches;
    private SparseArray<String> timeSections;
    private Context context;

    public SessionSpeechesListAdapter(Context ctx) {
        this.speeches = new SparseArray<PaperEntity>();
        this.timeSections = new SparseArray<String>();
        this.context = ctx;
    }


    public void setSpeeches(List<PaperEntity> papers) {
        int count = 0;
        for (int i = 0; i < papers.size(); i++) {
            PaperEntity paper = papers.get(i);
            String time = getTime(paper.getDateTime());
            if (timeSections.indexOfValue(time) < 0) {
                timeSections.put(count, time);
                speeches.put(++count, papers.get(i));
            } else
                speeches.put(count, papers.get(i));
            count++;
        }

    }

    private String getTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.paper_time_pattern));//"yyyy-MM-dd'T'hh:mm:ssZ"
        Date d = null;
        try {
            d = sdf.parse(date);
            d = new Date(d.getTime() - 21600000); //FIXME Removed 6 hrs due to Singapore Time. Delete on next conference
        } catch (ParseException ex) {
            Log.e(SessionSpeechesListAdapter.class.getName(), "Dateformat-ParseException", ex);
        }
        if (d != null) {
            SimpleDateFormat hourFormat = new SimpleDateFormat(context.getString(R.string.paper_hour_format));
            return hourFormat.format(d);
        }
        return null;
    }

    public int getCount() {
        return speeches.size() + timeSections.size();
    }

    public Object getItem(int arg0) {
        String section = timeSections.get(arg0);
        if (section == null)
            return speeches.get(arg0);
        else
            return section;
    }


    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int position, View convertview, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.fragment_speech_row, parent, false);

        if (isSection(position)) {
            setSectionView(row, position);
        } else {
            setSpeechView(row, position);
        }

        return row;

    }

    private void setSectionView(View row, int pos) {
        String time = timeSections.get(pos);
        if (time != null) {
            TextView header = (TextView) row.findViewById(R.id.speech_section_header);
            header.setText(time);
            header.setVisibility(View.VISIBLE);
        }
    }

    private void setSpeechView(View row, int pos) {
        PaperEntity paper = speeches.get(pos);
        if (paper != null) {
            TextView title = (TextView) row.findViewById(R.id.speech_title);
            title.setText(paper.getTitle());
            title.setVisibility(View.VISIBLE);
            TextView author = (TextView) row.findViewById(R.id.speech_author);
            author.setText(paper.getMainAuthor());
            author.setVisibility(View.VISIBLE);
        }

    }

    private boolean isSection(int pos) {
        return timeSections.get(pos) != null;
    }


}
