package com.example.myapplication.classscheduleapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "class_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        // Intent to open ClassDetailsActivity when the notification is clicked
        Intent notificationIntent = new Intent(context, ClassDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_time_24) // Set your icon
                .setContentTitle("Reminder: " + title)
                .setContentText(description)  // Includes remaining time, i.e., "Class starts in 1 hour 30 minutes"
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(title.hashCode(), builder.build());
    }
}
