package com.example.taskreminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReminderEditActivity extends Activity {
	
	private Button mDateButton;
	private Button mTimeButton;
	private Button mSaveButton;
	private Calendar now;
	private Time tNow;
	private DateDialogFragment dFrag;
	private TimeDialogFragment tFrag;
	private String source;
	private EditText mTitleText;
	private EditText mBodyText;
	private RemindersDbAdapter mDbHelper;
	private Long mRowId;
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_TIME_FORMAT = "dd/MM/yyyy kk/mm";
	public static final String TIME_FORMAT = "kk:mm";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new RemindersDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.activity_reminder_edit);
		mDateButton = (Button)findViewById(R.id.reminder_date);
		mTimeButton = (Button)findViewById(R.id.reminder_time);
		mSaveButton = (Button)findViewById(R.id.confirm);
		mTitleText = (EditText)findViewById(R.id.title);
		mBodyText = (EditText)findViewById(R.id.body);
		now = Calendar.getInstance();		
		tNow = new Time(Time.getCurrentTimezone());
		
		mRowId = savedInstanceState != null ?
				savedInstanceState.getLong(RemindersDbAdapter.KEY_ROWID)
				: null;
				
		if(getIntent().getExtras() !=null) {
			
			setRowIdFromIntent();
			populateFields();
		} else {
			tNow.setToNow();
			updateDateButtonText();
			updateTimeButtonText();
		} 
		registerButtonListenerAndSetDefaultText();
		
	}

	private void setRowIdFromIntent() {
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(RemindersDbAdapter.KEY_ROWID) : null;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
		setRowIdFromIntent();
		populateFields();
	}
	
	private void populateFields() {
		if(mRowId != null) {
			Cursor reminder = mDbHelper.fetchReminder(mRowId);
			startManagingCursor(reminder);
			mTitleText.setText(reminder.getString
					(reminder.getColumnIndexOrThrow
							(RemindersDbAdapter.KEY_TITLE)));
			mBodyText.setText(reminder.getString
					(reminder.getColumnIndex
							(RemindersDbAdapter.KEY_BODY)));
			// Need to add the code to update Date and Time button text here
			String reminderDateTime = reminder.getString
				(reminder.getColumnIndex
					(RemindersDbAdapter.KEY_DATE_TIME));
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
			Date date = null;
			try {
				date = dateTimeFormat.parse(reminderDateTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			now.setTime(date);
			updateDateButtonText();
			updateTimeButtonText();
			
		} else {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String defaultTitleKey = getString(R.string.pref_task_title_key);
			String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);
			String defaultTitle = prefs.getString(defaultTitleKey, "");
			String defaultTime = prefs.getString(defaultTimeKey, "");
			if("".equals(defaultTime) == false)
				mTitleText.setText(defaultTitle);
			if("".equals(defaultTime) == false)
				now.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
			updateDateButtonText();
			updateTimeButtonText();
		}
	}
	
	public void updateDateButtonText() {
		SimpleDateFormat dateFormat = new
			SimpleDateFormat(DATE_FORMAT);
		mDateButton.setText(dateFormat.format(now.getTime()));		
	}
	
	public void updateTimeButtonText() {
		SimpleDateFormat timeFormat = new
			SimpleDateFormat(TIME_FORMAT);
		mTimeButton.setText(timeFormat.format(now.getTime()));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(RemindersDbAdapter.KEY_ROWID, mRowId);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reminder_edit, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected (int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_undo:
			//Toast.makeText(getApplicationContext(), "Not handled yet", Toast.LENGTH_SHORT).show();
			setResult(Activity.RESULT_CANCELED);
			finish();
			return true;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void registerButtonListenerAndSetDefaultText() {
		
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				source = "Date";
				showDialog(source);
			}
		});
		
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				source = "Time";
				showDialog(source);
			}
		});
		
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				showAlertDialog();
			/**	saveState();
				setResult(RESULT_OK);
				Toast.makeText(ReminderEditActivity.this,
						getString(R.string.task_saved_message), 
						Toast.LENGTH_SHORT).show();
				finish(); */
			}
		});
	}
	
	public void showDialog(String source) {
		if(source=="Date") {
		FragmentTransaction ft = getFragmentManager().beginTransaction(); //get the fragment
		dFrag = DateDialogFragment.newInstance(this, new DateDialogFragmentListener(){
    		public void updateChangedDate(int year, int month, int day){
    			mDateButton.setText(String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+
    					String.valueOf(year));
    			now.set(year, month, day);
    		}
    	}, now);
    	
    	dFrag.show(ft, "DateDialogFragment");
		} else {
			FragmentTransaction ft = getFragmentManager().beginTransaction(); //get the fragment
			tFrag = TimeDialogFragment.newInstance(this, new TimeDialogFragmentListner(){
	    		public void updateChangedTime(int hour, int minute){
	    			mTimeButton.setText(String.valueOf(hour)+":"+String.valueOf(minute));
	    			now.set(Calendar.HOUR, hour);
	    			now.set(Calendar.MINUTE, minute);	    				    		}
	    	}, now);
	    	
	    	tFrag.show(ft, "TimeDialogFragment");
			
		}
	}
	
	public void showAlertDialog() {
		
		AlertDialog.Builder builder= 
			new AlertDialog.Builder(ReminderEditActivity.this);
		builder.setMessage("Do you want to save this task?");
		builder.setTitle("Are you sure?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Save the activity
						saveState();
						setResult(RESULT_OK);
						Toast.makeText(ReminderEditActivity.this,
								getString(R.string.task_saved_message), 
								Toast.LENGTH_SHORT).show();
						finish();
					}
				});
		builder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
						dialog.cancel();
						
					}
				});

		builder.create().show();
	}
	
	public void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		SimpleDateFormat dateFormat = new
		SimpleDateFormat(DATE_TIME_FORMAT);
		String reminderDateTime = dateFormat.format(now.getTime());
		
		if (mRowId == null) {
		long id = mDbHelper.createReminder(title, body, reminderDateTime);
		
		if (id > 0) {
			mRowId = id;
		}
		} else {
			mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
		}
				
		new ReminderManager(this).setReminder(mRowId, now);
	}
	
	 public interface DateDialogFragmentListener{
	    	//this interface is a listener between the Date Dialog fragment and the activity to update the buttons date
	    	public void updateChangedDate(int year, int month, int day);
	    }
	 
	 public interface TimeDialogFragmentListner{
	    	//this interface is a listener between the Time Dialog fragment and the activity to update the buttons time
	    	public void updateChangedTime(int hour, int minute);
	    }
	 
	 

}
