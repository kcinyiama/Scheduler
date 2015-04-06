package com.finalproject.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.finalproject.app.util.Utility;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.splashsound0);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
		
		String courseName = intent.getStringExtra("course");
		String location = intent.getStringExtra("location");
	
		// cancel the alarm
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		
		// create a notification and cancel the alarm
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentTitle("New class");
		builder.setContentText(courseName + " at " + location);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setAutoCancel(true);
		builder.setVibrate(new long[]{2, 4});
		
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.putExtra(Utility.COURSE_NAME, courseName);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, builder.build());
	}
}
