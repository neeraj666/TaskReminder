package com.example.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		long rowId =
			intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
		WakeReminderIntentService.acquireStaticLock(context);
		
		Intent i = new Intent(context, ReminderService.class);
		i.putExtra(RemindersDbAdapter.KEY_ROWID, rowId);
		context.startService(i);
	}

}
