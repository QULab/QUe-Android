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

/**
 * Created by deLaczkovich on 26.06.2014.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.*;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.que.activities.R;
import org.que.db.dao.ConferenceDAO;
import org.que.db.entities.PaperEntity;
import org.que.db.entities.SessionEntity;

/**
 * The child fragment is no different than any other fragment other than it is now being maintained by
 * a child FragmentManager.
 */
public class CalendarBookedFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorAdapter.ViewBinder {

    public static String CONFERENCEDATE = "DateShouldBeHere-Passed-from-args";
    private String confDayDate = "confDayDateString";

    //Projection and row_columns are used for populating and displaying the calendar data.
    private static final String[] PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.CALENDAR_DISPLAY_NAME,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION
    };
    private static final String[] ROW_COLUMNS = new String[]{
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION

    };

    private static final int[] ROW_IDS = new int[]{
            //this uses the values from the calendar_row.xml
            R.id.agenda_result_row_title, R.id.agenda_start, R.id.agenda_end, /*R.id.agenda_result_row_description,*/ R.id.agenda_room};

    private SimpleCursorAdapter adapter = null;
    private int index = -1;
    private int top = 0;

    public static CalendarBookedFragment newInstance(String CFconfDayDate) {
        CalendarBookedFragment fragment = new CalendarBookedFragment();
        Bundle args = new Bundle();
        args.putString("CONFERENCEDATE", CFconfDayDate);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        //get the date for this tab as given through the args bundle
        Bundle args = new Bundle();
        args.putString("confDayDate", confDayDate);
        //adapter is created with a null pointer, due to cursor not being populated yet. Otherwise the layout
        //is given and ROW_COLUMNS is the data basis and ROW_IDS is the array used to display the data.

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_agenda_result_row, null, ROW_COLUMNS, ROW_IDS, 0);
        adapter.setViewBinder(this);
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, args, this);
        if (index != -1) {
            this.getListView().setSelectionFromTop(index, top);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            index = this.getListView().getFirstVisiblePosition();
            View v = this.getListView().getChildAt(0);
            top = (v == null) ? 0 : v.getTop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String itemID = "NULL";
        Fragment frg = new Fragment();
        Bundle args = new Bundle();
        Object o = getListView().getItemAtPosition(position);
        super.onListItemClick(l, v, position, id);
        Cursor c = ((SimpleCursorAdapter) l.getAdapter()).getCursor();

        c.moveToPosition(position);
        String description = c.getString(5); //gets the description field of the calendar item
        try {


            //check whether item is a session or a paper
            if (description.contains("session")) {
                //itemID cleans up the description String first to get the Database ID of the Session
                itemID = description.replace(getActivity().getString(R.string.calendarEventDescription) + "sessionID", "");
                SessionEntity entity = ConferenceDAO.getSessionByID(itemID, getActivity());
                args.putSerializable(SessionViewFragment.ARG_SESSIONVIEW_FRAGMENT, entity);
                frg = new SessionViewFragment();


            } else if (description.contains("paper")) {
                itemID = description.replace(getActivity().getString(R.string.calendarEventDescription) + "paperID", "");
                PaperEntity entity = new PaperEntity();
                entity = ConferenceDAO.getPaperByID(itemID, getActivity());
                args.putSerializable(PaperFragment.ARG_PAPERVIEW_FRAGMENT, entity);
                frg = new PaperFragment();

            }
        } catch (NullPointerException e) {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setMessage("Something went wrong when trying to retrieve this event. Please re-favorite the event to re-add it to the calendar.");
            b.setTitle("Oops");
            b.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            b.show();
        }
        if (frg != null) {
            frg.setArguments(args);
            FragmentManager mgr = ((FragmentActivity) getActivity()).getSupportFragmentManager();
            Fragment old = mgr.findFragmentById(R.id.content_frame);


            FragmentTransaction trx = mgr.beginTransaction();
            if (old != null)
                trx.remove(old);

            trx.add(R.id.content_frame, frg)
                    .addToBackStack(null) //TODO
                    .commit();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        /*the cursor loader requires the context and then needs the URIs from which to populate. Based on the URI
        it uses the rows from the PROJECTION to determine which data to take from the calendar entries. The nulls are simply
        for sql-selection statement and selection args which are not needed here yet. Finally the sort order is determined
        by the start time of the calendar entries.
        This loader also uses dates to filter the entire calendar for the dates it needs to show (these are passed in
        the calendartab args bundle) and uses an SQL 'Like' filter in conjunction with the event Description String in
        the ConferenceDetails.xml to only show the events associated with the IS2014.
        */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c_end = Calendar.getInstance();
        Date dStart = null;
        Date dEnd = null;
        //get the date from the args Bundle
        String confDayDate1 = getArguments().getString("CONFERENCEDATE");
        //convert the date to an actual DATE format
        try {
            dStart = sdf.parse(confDayDate1);
            dEnd = sdf.parse(confDayDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c_end.setTime(dEnd);
        c_end.add(Calendar.DATE, 1);
        //get the description to filter the events
        String calendarDescriptionFilter = getString(R.string.calendarEventDescription);
        //create the selection statement (SQL Style)
        //the selection allows for events to go to 6AM the next day
        String selection = "((dtstart >= " + dStart.getTime() + ") AND (dtend <= " + (c_end.getTimeInMillis() + 6 * 3600000) + ") AND (description LIKE \'" + calendarDescriptionFilter + "%\'))";
        //String selection = "((dtstart >= " + dStart.getTime() + ") AND (description LIKE \'" + calendarDescriptionFilter + "%\'))";
        //execute
        return (new CursorLoader(getActivity(), CalendarContract.Events.CONTENT_URI,
                PROJECTION, selection, null, CalendarContract.Events.DTSTART));
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        //resets the loader by swapping the cursor with a null entry
        adapter.swapCursor(null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //when loading is done the adapter will swap in the finished cursor.
        adapter.swapCursor(cursor);
    }


    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        // This helps the adapter assign and format the data from the cursor  to the view. The cases are for
        //  the row IDs which point to the appropriate textviews.
        long time = 0;
        String formattedTime = null;
        switch (columnIndex) {
            case 2: //Start Time
                time = cursor.getLong(columnIndex);
                formattedTime =
                        DateUtils.formatDateTime(getActivity(), time,
                                DateUtils.FORMAT_SHOW_TIME);
                ((TextView) view).setText(formattedTime);
                break;
            case 3://End Time
                time = cursor.getLong(columnIndex);
                formattedTime =
                        DateUtils.formatDateTime(getActivity(), time,
                                DateUtils.FORMAT_SHOW_TIME);
                ((TextView) view).setText(formattedTime);
                break;
            case 5: //Location
                ((TextView) view).setText(cursor.getString(columnIndex + 1));
                break;
            default:
                return (false);
        }

        return true;
    }
}
