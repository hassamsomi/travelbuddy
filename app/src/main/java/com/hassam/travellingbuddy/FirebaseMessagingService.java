package com.hassam.travellingbuddy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

//
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String click_action = remoteMessage.getNotification().getClickAction();
        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();
        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notification_title)
                .setContentText(notification_message);

        Intent resultintent = new Intent(click_action);
        resultintent.putExtra("current_userID", from_user_id);
        PendingIntent resultPendingIntent = PendingIntent
                .getActivity(this,
                        0,
                        resultintent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        //Sets an ID for the notification
        int mNotificationId = (int) System.currentTimeMillis();
//        Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Builds the notification and issue it.
        mNotifyMgr.notify(mNotificationId,mBuilder.build());
    }
}
