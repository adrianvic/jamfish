package org.adrianvictor.geleia.service.notifications;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.adrianvictor.geleia.R;

public class ErrorNotification {
    private static final String CHANNEL_ID = ErrorNotification.class.getSimpleName();
    private static final int NOTIFICATION_ID = 3;

    private ErrorNotification() {}

    public static void show(Context context, String error) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle("Error:").bigText(error);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bug_report_white_24dp)
                .setContentTitle(context.getString(R.string.error_notification_title))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(style)
                .setContentText("Expand the notification for details.");
    }

    public static void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);

            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, "Errors", NotificationManager.IMPORTANCE_LOW);
                channel.setDescription("Displays all sorts of errors.");
                channel.enableLights(false);
                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
