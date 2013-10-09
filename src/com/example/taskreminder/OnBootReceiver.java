package com.example.taskreminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver{
	
	public static final String DATE_TIME_FORMAT = "dd/MM/yyyy kk/mm";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ReminderManager reminderMgr = new ReminderManager(context);
		RemindersDbAdapter dbHelper = new RemindersDbAdapter(context);
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllReminders();
		if(cursor != null) {
			cursor.moveToFirst();
			
			int rowIdColumIntex = 
					cursor.getColumnIndex(RemindersDbAdapter.KEY_ROWID);
			int dataTimeColumnIntex = 
					cursor.getColumnIndex(RemindersDbAdapter.KEY_DATE_TIME);
			
			while(cursor.isAfterLast()==false) {
				Long rowId = cursor.getLong(rowIdColumIntex);
				String dateTime = cursor.getString(dataTimeColumnIntex);
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
				Calendar cal = Calendar.getInstance();
				Date date;
				try {
					date = dateTimeFormat.parse(dateTime);
					cal.setTime(date);
					
					reminderMgr.setReminder(rowId, cal);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				cursor.moveToNext();
				Log.d("OnBootReceiver", "Adding alarm from boot.");
				Log.d("OnBootReceiver", "Row Id Column Index - " + rowId);
			}
			cursor.close();
		}
		dbHelper.close();
	}

}
