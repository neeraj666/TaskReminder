package com.example.taskreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class ReminderService extends WakeReminderIntentService {
	
	Vibrator vibe;
	
	public ReminderService() {
		super("ReminderService");
	}

	@Override
	void doReminderWork(Intent intent) {
		// TODO Auto-generated method stub
		Long rowId = intent.getExtras()
			.getLong(RemindersDbAdapter.KEY_ROWID);
		// Status bar notification
		NotificationManager mgr =
			(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(this, ReminderEditActivity.class);
		notificationIntent.putExtra(RemindersDbAdapter.KEY_ROWID, rowId);
		Intent notificationSnoozeIntent = new Intent(this, ReminderSnoozeService.class);
		notificationSnoozeIntent.putExtra(RemindersDbAdapter.KEY_ROWID, rowId);
		notificationSnoozeIntent.putExtra("Action","snooze");
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(ReminderEditActivity.class);
		stackBuilder.addNextIntent(notificationIntent);
		//stackBuilder.addNextIntent(notificationSnoozeIntent);
		PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		PendingIntent piSnooze = PendingIntent.getService(this, 0, notificationSnoozeIntent, 0);
		
		NotificationCompat.Builder mBuilder = new
			NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getString(R.string.notifiy_new_task_title))
			.setContentText(getString(R.string.notify_new_task_message))
			.setLights(Notification.DEFAULT_LIGHTS, 300, 100)
			.setAutoCancel(true)
			.setContentIntent(pi)
			.addAction(R.drawable.ic_snooze, "Snooze", piSnooze);
		
		int id = (int)((long)rowId);
		mgr.notify(id, mBuilder.build());
	}

}
