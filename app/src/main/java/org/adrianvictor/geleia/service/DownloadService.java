package org.adrianvictor.geleia.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import androidx.documentfile.provider.DocumentFile;

import org.adrianvictor.geleia.App;
import org.adrianvictor.geleia.BuildConfig;
import org.adrianvictor.geleia.database.Cache;
import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.service.notifications.DownloadNotification;
import org.adrianvictor.geleia.util.MusicUtil;
import org.adrianvictor.geleia.util.PreferenceUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DownloadService extends Service {
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String ACTION_START = PACKAGE_NAME + ".action.start";
    public static final String ACTION_CANCEL = PACKAGE_NAME + ".action.cancel";
    public static final String EXTRA_SONGS = PACKAGE_NAME + ".extra.songs";

    private ExecutorService executor;
    private DownloadNotification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        executor = Executors.newFixedThreadPool(4);
        notification = new DownloadNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return super.onStartCommand(null, flags, startId);
        }

        switch (intent.getAction()) {
            case DownloadService.ACTION_CANCEL:
                executor.shutdownNow();
                notification.stop(null);
                stopSelf();
                break;
            case DownloadService.ACTION_START:
                List<Song> songs = intent.getParcelableArrayListExtra(EXTRA_SONGS);
                for (Song song : songs) {
                    download(song);
                    notification.start(song);
                }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public void download(Song song) {
        executor.execute(() -> {
            try {
                URL url = new URL(MusicUtil.getDownloadUri(song));
                URLConnection connection = url.openConnection();

                String location = PreferenceUtil.getInstance(App.getInstance()).getLocationDownload();
                DocumentFile root;
                if (location.equals(getApplicationContext().getCacheDir().toString())) {
                    root = DocumentFile.fromFile(new File(location));
                } else {
                    root = DocumentFile.fromTreeUri(this, Uri.parse(location));
                }

                DocumentFile artist = root.findFile(MusicUtil.ascii(song.artistName));
                if (artist == null) {
                    artist = root.createDirectory(MusicUtil.ascii(song.artistName));
                }
                DocumentFile album = artist.findFile(MusicUtil.ascii(song.albumName));
                if (album == null) {
                    album = artist.createDirectory(MusicUtil.ascii(song.albumName));
                }

                String fileName = song.discNumber + "." + song.trackNumber + " - " + MusicUtil.ascii(song.title) + "." + song.container;
                DocumentFile audio = album.createFile("audio/" + song.container, fileName);

                InputStream input = connection.getInputStream();
                OutputStream output = getContentResolver().openOutputStream(audio.getUri());

                connection.connect();

                byte[] data = new byte[1048576];
                int count;

                notification.update(0, connection.getContentLength());
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    notification.update(count, 0);
                }

                input.close();
                output.close();

                App.getDatabase().cacheDao().insertCache(new Cache(song));
                notification.stop(song);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
