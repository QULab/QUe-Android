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

package de.tel.quenference.util.calender;

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.tel.quenference.activities.R;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.entities.PaperEntity;
import de.tel.quenference.db.entities.SessionEntity;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by deLaczkovich on 09.07.2014. This class is there to add and delete
 * events from the calendar
 */
public class CalendarHelper {

    public static final String string_file_prefs_Name = "preferences.xml";
    //Returns an array with the calendar names
    private static Context context;

    public static void setContext(Context mcontext) {
        if (context == null)
            context = mcontext;
    }

    public static void showCalendarPicker(final Context context, final Serializable session) {

        List<String> calendarList = new ArrayList<String>();
        //this is the projection used to query the calendar adapter.
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        final Cursor calCursor =
                context.getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);
                String accountName = calCursor.getString(2);
                if (displayName != null) {
                    calendarList.add("Calendar: " + displayName + " - Account: " + accountName);
                } else {
                    calendarList.add("Account: " + accountName);
                }

            } while (calCursor.moveToNext());
        }
        String[] calendarListArray = new String[calendarList.size()];
        calendarListArray = calendarList.toArray(calendarListArray);
        //setup of Dialog
        AlertDialog.Builder ADBuilder = new AlertDialog.Builder(context);
        ADBuilder.setTitle("Please pick the calendar")
                .setItems(calendarListArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        calCursor.moveToPosition(which);
                        int calendarIndex = calCursor.getInt(0);
                        insertItemToCalendar(calendarIndex, session, context);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        sharedPreferences.edit().putString("prefWhichCalendar", Integer.toString(calendarIndex)).apply();
                        Toast.makeText(context, "Item added to calendar and favorites", Toast.LENGTH_LONG).show();

                    }
                });
        AlertDialog dialog = ADBuilder.create();
        dialog.show();


    }

    //This checks if the the option add to calendar is active, if not then it will prompt user for confirmation
    //and then it will show the list of calendars so the user can pick the appropriate one
    public static void attemptToAddToCalendar(final Context context, final Serializable entity) {
        if (!((entity instanceof SessionEntity) || (entity instanceof PaperEntity))) {
            return;
        }


        //final SessionEntity session = (SessionEntity) entity;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);//context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("prefAddToCalendarAutomaticallyList", "2").contentEquals("2")) {
            //CREATING ALERTBOX to ask whether to add to calendar and remember decision or not
            final CheckBox checkBox = new CheckBox(context);
            checkBox.setText("Remember my choice");
            checkBox.setChecked(false);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(checkBox);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(linearLayout);
            builder.setMessage("Do you want to add this Session to your calendar?").setTitle("Add to Calendar?");
            //ADD BUTTON
            builder.setPositiveButton("Add to calendar", new DialogInterface.OnClickListener() {
                @Override
                //ADD BUTTON
                public void onClick(DialogInterface dialog, int which) {
                    //  Toast.makeText(context, session.getTitle(), Toast.LENGTH_SHORT).show();
                    if (checkBox.isChecked()) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor prefEdit = sharedPref.edit();
                        prefEdit.putString("prefAddToCalendarAutomaticallyList", "0"); //If checked then preferences will be updated to always add items to calendar
                        Toast.makeText(context, sharedPref.getString("prefAddToCalendarAutomaticallyList", "Didnt work"), Toast.LENGTH_SHORT).show();
                        prefEdit.apply();
                    }
                    if (sharedPreferences.getString("prefWhichCalendar", "NULL").contentEquals("NULL") ||
                            sharedPreferences.getString("prefWhichCalendar", "NULL").contentEquals("")) {
                        showCalendarPicker(context, entity);
                        //Here item-adding function is called
                    } else {
                        try {
                            int calendarID = Integer.parseInt(sharedPreferences.getString("prefWhichCalendar", "0"));
                            insertItemToCalendar(calendarID, entity, context);
                            Toast.makeText(context, "Item added to favorites and calendar", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            sharedPreferences.edit().putString("prefWhichCalendar", "NULL").commit();
                            Toast.makeText(context, "Something went wrong with inserting into calendar, please un-favorite and try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            //DONT ADD BUTTON
            builder.setNegativeButton("Don't add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (checkBox.isChecked()) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);// context.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEdit = sharedPref.edit();
                        prefEdit.putString("prefAddToCalendarAutomaticallyList", "1"); //If checked then preferences will be updated to never add items to calendar
                        prefEdit.commit();
                        Toast.makeText(context, "item not Added", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.show();
        }
        if (sharedPreferences.getString("prefAddToCalendarAutomaticallyList", "2").contentEquals("0")) {
            //Only called if items always are to be added to calendar.
            if (sharedPreferences.getString("prefWhichCalendar", "NULL").contentEquals("NULL")
                    || sharedPreferences.getString("prefWhichCalendar", "NULL").contentEquals("")) {
                showCalendarPicker(context, entity);
            }

            int calendarID = Integer.parseInt(sharedPreferences.getString("prefWhichCalendar", "0"));
            insertItemToCalendar(calendarID, entity, context); //Here item-adding function is called
            // Toast.makeText(context, "item Added", Toast.LENGTH_LONG).show();
        }
        if (sharedPreferences.getString("prefAddToCalendarAutomaticallyList", "2").equals("1")) {
            //Only called if items always are never to be added to calendar.
            //Toast.makeText(context, "item not Added, because setting prevents it", Toast.LENGTH_LONG).show();
        }

    }

    // for Sessions
    public static void insertItemToCalendar(int CalendarID, Serializable entity, Context context) {
        // Construct event details
        SessionEntity session = new SessionEntity();
        PaperEntity paper = new PaperEntity();
        String givenDateString = new String();
        SimpleDateFormat sdf_start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");
        SimpleDateFormat sdf_end = new SimpleDateFormat("mm");
        long startMillis = 0;
        long durationMillis = 0;
        long endMillis = 0;
        int sessionID;
        SessionEntity sessionEntity = null;

        if (entity instanceof SessionEntity) {
            session = (SessionEntity) entity;
            try {
                givenDateString = session.getDatetime();
                Date mDate = sdf_start.parse(givenDateString);
                startMillis = mDate.getTime() - 21600000; //FIXME Removed 6 hrs due to Singapore Time. Delete on next conference
                System.out.println("Date in milli :: " + startMillis);
                durationMillis = session.getLength() * 60 * 1000;
                endMillis = durationMillis + startMillis;
                System.out.println("Date in milli :: " + endMillis);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            paper = (PaperEntity) entity;
            try {
                sessionID = paper.getSession();
                sessionEntity = ConferenceDAO.getSessionByID(Integer.toString(sessionID), context);
                givenDateString = paper.getDateTime();
                Date mDate = sdf_start.parse(givenDateString);
                startMillis = mDate.getTime() - 21600000; //FIXME Removed 6 hrs due to Singapore Time. Delete on next conference
                System.out.println("Date in milli :: " + startMillis);
                endMillis = sdf_start.parse(paper.getDateTimeEnd()).getTime() - 21600000; //FIXME Removed 6 hrs due to Singapore Time. Delete on next conference;
                System.out.println("Date in milli :: " + endMillis);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


// Insert Event and respect the timezone the user is in right now.
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();

        if (entity instanceof SessionEntity) {
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.TITLE, session.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, context.getString(R.string.calendarEventDescription) + "sessionID" + session.getId());
            values.put(CalendarContract.Events.CALENDAR_ID, CalendarID);
            values.put(CalendarContract.Events.EVENT_LOCATION, session.getRoom());

        } else {
            if (sessionEntity != null) {
                values.put(CalendarContract.Events.EVENT_LOCATION, sessionEntity.getRoom());
            }
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.TITLE, paper.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, context.getString(R.string.calendarEventDescription) + "paperID" + paper.getId());
            values.put(CalendarContract.Events.CALENDAR_ID, CalendarID);
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// Retrieve ID for new event
        String eventID = uri.toString();//.getLastPathSegment();
        //Toast.makeText(context, "URI added is: " + eventID, Toast.LENGTH_SHORT).show();
    }

    public static void deleteItemFromCalendar(Serializable entity, Context context) {
        SessionEntity session = new SessionEntity();
        PaperEntity paper = new PaperEntity();
        //Create Projection and connection to calendar
        ContentResolver cr = context.getContentResolver();
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.CALENDAR_ID
        };
      /*Create a cursor and use the selection args to find the event by either its Session ID or its Paper ID
      which is coded in the Description field of the calendar event.*/

        Cursor cur = null;
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "((" + CalendarContract.Events.DESCRIPTION + " = ?))";

        if (entity instanceof SessionEntity) {
            session = (SessionEntity) entity;
            String[] selectionArgs = new String[]{context.getString(R.string.calendarEventDescription) + "sessionID" + session.getId()};
            System.out.println(context.getString(R.string.calendarEventDescription) + "sessionID" + session.getId());
            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            //go through the cursor, find its event id and delete it.
            if (cur.moveToFirst()) {
                do {
                    String eventID = cur.getString(0);
                    Uri deleteURI = null;
                    Long eventIDlong = Long.parseLong(eventID);
                    deleteURI = ContentUris.withAppendedId(uri, eventIDlong);
                    int rows = context.getContentResolver().delete(deleteURI, null, null);
                    //      Toast.makeText(context, "Rows deleted: " + rows + "at content URI: " + deleteURI.toString() + "from calendar: " + cur.getString(3), Toast.LENGTH_SHORT).show();
                } while (cur.moveToNext());
            }

        } else {
            //if entity is a paper and not a session
            paper = (PaperEntity) entity;
            System.out.println(context.getString(R.string.calendarEventDescription) + "sessionID" + session.getId());
            String[] selectionArgs = new String[]{context.getString(R.string.calendarEventDescription) + "paperID" + paper.getId()};
            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            //go through the cursor, find its event id and delete it.
            if (cur.moveToFirst()) {
                do {
                    String eventID = cur.getString(0);
                    Uri deleteURI = null;
                    Long eventIDlong = Long.parseLong(eventID);
                    deleteURI = ContentUris.withAppendedId(uri, eventIDlong);
                    int rows = context.getContentResolver().delete(deleteURI, null, null);
                    //      Toast.makeText(context, "Rows deleted: " + rows + "at content URI: " + deleteURI.toString() + "from calendar: " + cur.getString(3), Toast.LENGTH_SHORT).show();
                } while (cur.moveToNext());
            }
        }

    }


    public static String[] getTimeFromSession(SessionEntity session) {

        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");
        SimpleDateFormat sdfout = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfHour = new SimpleDateFormat("mm");
        String[] timeOut = {"NULLStart", "NULLEnd"};
        Date dDate = new Date();
        long dDuration = 0;

        try {
            dDate = sdfin.parse(session.getDatetime());
            dDate = new Date(dDate.getTime() - 21600000); //FIXME: Fix this after IS2014, removing 6hrs due to Singapore
            dDuration = TimeUnit.MINUTES.toMillis(session.getLength());

            timeOut[0] = sdfout.format(dDate).toString();
            timeOut[1] = sdfout.format(dDate.getTime() + dDuration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeOut;

    }

    public static String[] getDayAndTimeFromSession(SessionEntity session) {
        String[] timeOut = {"NULLStart", "NULLEnd"};
        String dateTime = session.getDatetime();
        if (dateTime == null || dateTime.isEmpty())
          return timeOut;
        
        SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");
        SimpleDateFormat sdfout = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfoutbegin = new SimpleDateFormat("EEEE - HH:mm");
        SimpleDateFormat sdfHour = new SimpleDateFormat("mm");
        Date dDate = new Date();
        long dDuration = 0;

        try {
            dDate = sdfin.parse(dateTime);
            dDate = new Date(dDate.getTime() - 21600000); //FIXME: Fix this after IS2014, removing 6hrs due to Singapore
            dDuration = TimeUnit.MINUTES.toMillis(session.getLength());

            timeOut[0] = sdfoutbegin.format(dDate).toString();
            timeOut[1] = sdfout.format(dDate.getTime() + dDuration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeOut;

    }
}