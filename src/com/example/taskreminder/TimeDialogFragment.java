package com.example.taskreminder;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.TimePicker;

import com.example.taskreminder.ReminderEditActivity.TimeDialogFragmentListner;

public class TimeDialogFragment extends DialogFragment {
	
	static Context mContext;
	static int mHour;
	static int mMinute;
	static boolean am_pm;
	static TimeDialogFragmentListner mListener;
	
	public static TimeDialogFragment newInstance(Context context, TimeDialogFragmentListner listener, 
			Calendar now) {
		TimeDialogFragment dialog = new TimeDialogFragment();
		mContext = context;
		mListener = listener;
		mHour = now.get(Calendar.HOUR);
		mMinute = now.get(Calendar.MINUTE);
		am_pm = true;
		Bundle args = new Bundle();
		args.putString("title", "Set Time");
		dialog.setArguments(args);
		return dialog;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new TimePickerDialog(mContext, mTimeSetListener, mHour, mMinute,am_pm);
	}
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
						
			mHour = hour;
			mMinute = minute;
			mListener.updateChangedTime(hour,minute);
		}
	};
}
