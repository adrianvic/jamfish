package org.adrianvictor.geleia.service.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.service.DownloadService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DownloadNotification {
    private static final String CHANNEL_ID = "download_channel";
    private final int ID = 2;
    private final Context context;
    private final NotificationManager notificationManager;
    private final List<Song> queue = Collections.synchronizedList(new LinkedList<>());

    private long totalSize;
    private long downloadedSize;
    private int lastPercentage = -1;

    public DownloadNotification(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.download_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void start(Song song) {
        queue.add(song);
        if (queue.size() == 1) { // This is the first song of a new batch
            totalSize = 0;
            downloadedSize = 0;
            lastPercentage = -1;
            notificationManager.notify(ID, getNotification());
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            // For KitKat, update for every new song to show correct count.
            notificationManager.notify(ID, getNotification());
        }
    }

    public void update(long downloaded, long total) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return; // No progress updates for KitKat
        }

        synchronized (this) {
            totalSize += total;
            downloadedSize += downloaded;

            int percentage = 0;
            if (totalSize > 0) {
                percentage = (int) ((downloadedSize * 100) / totalSize);
            }

            if (percentage > lastPercentage) {
                lastPercentage = percentage;
                notificationManager.notify(ID, getNotification());
            }
        }
    }

    public void stop(Song song) {
        queue.remove(song);
        if (queue.isEmpty()) {
            notificationManager.cancel(ID);
        } else {
            // Update notification to show new queue size.
            // On KitKat, this is the only update after a download finishes.
            notificationManager.notify(ID, getNotification());
        }
    }

    public void cancelAll() {
        queue.clear();
        notificationManager.cancel(ID);
    }

    private Notification getNotification() {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_CANCEL);

        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.downloading_songs))
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(android.R.string.cancel), pendingIntent);

        String contentText = context.getResources().getQuantityString(R.plurals.downloading_s_songs, queue.size(), queue.size());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
            remoteViews.setTextViewText(R.id.notification_download_title, contentText);
            int progress = lastPercentage > 0 ? lastPercentage : 0;
            remoteViews.setProgressBar(R.id.notification_download_progress, 100, progress, totalSize == 0 && downloadedSize == 0);
            builder.setCustomContentView(remoteViews);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            int progress = lastPercentage > 0 ? lastPercentage : 0;
            builder.setProgress(100, progress, totalSize == 0 && downloadedSize == 0);
            builder.setContentText(contentText);
        } else { // KitKat
            builder.setContentText(contentText);
        }

        return builder.build();
    }
}
