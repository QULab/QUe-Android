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
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.tel.quenference.activities.R;
import de.tel.quenference.db.dao.ConferenceDAO;
import de.tel.quenference.db.dao.SQLQuery;
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

  public static final String SHARED_PREF_CALENDAR_KEY = "prefWhichCalendar";
  public static final String SHARED_PREF_ADD_AUTO_TO_CALENDAR = "prefAddToCalendarAutomaticallyList";
  public static final String NULL_VALUE = "NULL";

  public static void showCalendarPicker(final Context context, final Serializable session) {
    List<String> calendarList = new ArrayList<String>();
    //this is the projection used to query the calendar adapter.
    String[] projection = new String[]{CalendarContract.Calendars._ID,
      CalendarContract.Calendars.NAME,
      CalendarContract.Calendars.ACCOUNT_NAME,
      CalendarContract.Calendars.ACCOUNT_TYPE};
    final Cursor calCursor = context.getContentResolver()
            .query(CalendarContract.Calendars.CONTENT_URI,
            projection,
            CalendarContract.Calendars.VISIBLE + " = 1",
            null,
            CalendarContract.Calendars._ID
            + SQLQuery.SQL_ASC_ORDER);
    final int calendarIDs[] = new int[calCursor.getCount()];
    if (calCursor.moveToFirst()) {
      int i = 0;
      do {
        calendarIDs[i++] = calCursor.getInt(0);
        String displayName = calCursor.getString(1);
        String accountName = calCursor.getString(2);
        if (displayName != null) {
          calendarList.add("Calendar: " + displayName + " - Account: " + accountName);
        } else {
          calendarList.add("Account: " + accountName);
        }

      } while (calCursor.moveToNext());
    }
    calCursor.close();
    createPickCalendarDialog(context, session, calendarIDs, calendarList);

  }

  private static void createPickCalendarDialog(final Context context,
          final Serializable session,
          final int[] calendarIDs,
          final List<String> calendarList) {
    String[] calendarListArray = calendarList.toArray(new String[calendarList.size()]);
    //setup of Dialog
    AlertDialog.Builder ADBuilder = new AlertDialog.Builder(context);
    ADBuilder.setTitle(context.getString(R.string.alert_pick_calendar))
            .setItems(calendarListArray, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        int calendarIndex = calendarIDs[which % calendarIDs.length];
        insertItemToCalendar(calendarIndex, session, context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SHARED_PREF_CALENDAR_KEY, Integer.toString(calendarIndex)).apply();
        Toast.makeText(context,
                context.getString(R.string.toast_added_to_calendar),
                Toast.LENGTH_LONG).show();

      }
    });
    AlertDialog dialog = ADBuilder.create();
    dialog.show();

  }

  private static CheckBox createCheckBox(Context context, String text) {
    CheckBox checkBox = new CheckBox(context);
    checkBox.setText(text);
    checkBox.setChecked(false);
    return checkBox;
  }

  //This checks if the the option add to calendar is active, if not then it will prompt user for confirmation
  //and then it will show the list of calendars so the user can pick the appropriate one
  public static void attemptToAddToCalendar(final Context context, final Serializable entity) {
    if (!((entity instanceof SessionEntity) || (entity instanceof PaperEntity))) {
      return;
    }

    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    final String autoDecision = sharedPreferences.getString(SHARED_PREF_ADD_AUTO_TO_CALENDAR, "2");

    if (autoDecision.equals("2")) {
      //CREATING ALERTBOX to ask whether to add to calendar and remember decision or not
      final CheckBox checkBox = createCheckBox(context, context.getString(R.string.checkbox_remember));
      LinearLayout ll = createLLforAddToCalendar(context, checkBox);
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setView(ll);
      builder.setMessage(context.getString(R.string.alert_head_add_to_cal))
              .setTitle(context.getString(R.string.alert_head_add_to_cal));
      addAddButtonToBuilder(context, entity, builder, checkBox, sharedPreferences);
      addCancleButtonToBuilder(context, builder, checkBox);
      builder.show();
    } else if (autoDecision.equals("0")) { //Only called if items always are to be added to calendar.
      String cal = sharedPreferences.getString(SHARED_PREF_CALENDAR_KEY, NULL_VALUE);
      if (cal.contentEquals(NULL_VALUE) || cal.isEmpty()) {
        showCalendarPicker(context, entity);
      }
      int calendarID = Integer.parseInt(sharedPreferences.getString(SHARED_PREF_CALENDAR_KEY, "0"));
      insertItemToCalendar(calendarID, entity, context);
    } else if (autoDecision.equals("1")) { //Only called if items always are never to be added to calendar
    }
  }

  private static LinearLayout createLLforAddToCalendar(final Context context,
          final CheckBox checkBox) {

    LinearLayout linearLayout = new LinearLayout(context);
    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.addView(checkBox);
    return linearLayout;
  }

  private static void addAddButtonToBuilder(final Context context,
          final Serializable entity,
          final AlertDialog.Builder builder,
          final CheckBox checkBox,
          final SharedPreferences sharedPreferences) {
    builder.setPositiveButton(context.getString(R.string.btn_add_to_cal), new DialogInterface.OnClickListener() {
      @Override
      //ADD BUTTON
      public void onClick(DialogInterface dialog, int which) {
        //  Toast.makeText(context, session.getTitle(), Toast.LENGTH_SHORT).show();
        if (checkBox.isChecked()) {
          SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
          SharedPreferences.Editor prefEdit = sharedPref.edit();
          prefEdit.putString(SHARED_PREF_ADD_AUTO_TO_CALENDAR, "0"); //If checked then preferences will be updated to always add items to calendar
          Toast.makeText(context, sharedPref.getString(SHARED_PREF_ADD_AUTO_TO_CALENDAR, "Didnt work"), Toast.LENGTH_SHORT).show();
          prefEdit.apply();
        }
        if (sharedPreferences.getString(SHARED_PREF_CALENDAR_KEY, NULL_VALUE).contentEquals(NULL_VALUE)
                || sharedPreferences.getString(SHARED_PREF_CALENDAR_KEY, NULL_VALUE).isEmpty()) {
          showCalendarPicker(context, entity);
          //Here item-adding function is called
        } else {
          try {
            int calendarID = Integer.parseInt(sharedPreferences.getString(SHARED_PREF_CALENDAR_KEY, "0"));
            insertItemToCalendar(calendarID, entity, context);
            Toast.makeText(context, context.getString(R.string.toast_added_to_calendar), Toast.LENGTH_SHORT).show();

          } catch (Exception e) {
            sharedPreferences.edit().putString(SHARED_PREF_CALENDAR_KEY, NULL_VALUE).commit();
            Toast.makeText(context, context.getString(R.string.toast_add_failed), Toast.LENGTH_SHORT).show();
          }
        }
      }
    });

  }

  private static void addCancleButtonToBuilder(final Context context,
          final AlertDialog.Builder builder,
          final CheckBox checkBox) {
    builder.setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (checkBox.isChecked()) {
          SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);// context.getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
          SharedPreferences.Editor prefEdit = sharedPref.edit();
          prefEdit.putString(SHARED_PREF_ADD_AUTO_TO_CALENDAR, "1"); //If checked then preferences will be updated to never add items to calendar
          prefEdit.commit();
        }
      }
    });

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

  /**
   * Returns the begin and end time from the session as string array.
   *
   * @param session the session entity
   * @return the start and end time of the session
   */
  public static String[] getTimeFromSession(SessionEntity session) {
    return getTimeFromSession(session, 0l);
  }

  /**
   * Returns the begin and end time from the session as string array. Calculates
   * the diff to the time, useful for different timezones.
   *
   * E.g. Singapore and Germany are 6h difference so you can give as diff
   * parameter -21600000.
   *
   *
   * @param session the session entity
   * @param diff the difference of the session time
   * @return the start and end time of the session
   */
  public static String[] getTimeFromSession(SessionEntity session, long diff) {
    SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");
    SimpleDateFormat sdfout = new SimpleDateFormat("HH:mm");
    String[] timeOut = {"NULLStart", "NULLEnd"};
    Date dDate;
    long dDuration;
    try {
      dDate = sdfin.parse(session.getDatetime());
      dDate = new Date(dDate.getTime() + diff);
      dDuration = TimeUnit.MINUTES.toMillis(session.getLength());

      timeOut[0] = sdfout.format(dDate).toString();
      timeOut[1] = sdfout.format(dDate.getTime() + dDuration);
    } catch (ParseException e) {
      Log.e(CalendarHelper.class.getName(), "ParseException getTimeFromSession", e);
    }
    return timeOut;

  }

  /**
   * Returns the begin and end day time from the session as string array.
   *
   * @param session the session entity
   * @return the start and end day time of the session
   */
  public static String[] getDayAndTimeFromSession(SessionEntity session) {
    return getDayAndTimeFromSession(session, 0);
  }

  /**
   * Returns the begin and end day time from the session as string array.
   * Calculates the diff to the time, useful for different timezones.
   *
   * E.g. Singapore and Germany are 6h difference so you can give as diff
   * parameter -21600000.
   *
   *
   * @param session the session entity
   * @param diff the difference of the session time
   * @return the start and end day time of the session
   */
  public static String[] getDayAndTimeFromSession(SessionEntity session, long diff) {
    String[] timeOut = {"NULLStart", "NULLEnd"};
    String dateTime = session.getDatetime();
    if (dateTime == null || dateTime.isEmpty()) {
      return timeOut;
    }

    SimpleDateFormat sdfin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");
    SimpleDateFormat sdfout = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfoutbegin = new SimpleDateFormat("EEEE - HH:mm");
    Date dDate;
    long dDuration;
    try {
      dDate = sdfin.parse(dateTime);
      dDate = new Date(dDate.getTime() + diff);
      dDuration = TimeUnit.MINUTES.toMillis(session.getLength());

      timeOut[0] = sdfoutbegin.format(dDate).toString();
      timeOut[1] = sdfout.format(dDate.getTime() + dDuration);
    } catch (ParseException e) {
      Log.e(CalendarHelper.class.getName(), "ParseException getDayAndTimeFromSession", e);
    }
    return timeOut;
  }
}