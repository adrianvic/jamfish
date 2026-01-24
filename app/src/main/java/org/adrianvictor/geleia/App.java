package org.adrianvictor.geleia;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import org.adrianvictor.geleia.database.JellyDatabase;
import org.adrianvictor.geleia.helper.EventListener;
import org.adrianvictor.geleia.util.PreferenceUtil;
import org.adrianvictor.geleia.views.shortcuts.DynamicShortcutManager;
import com.melegy.redscreenofdeath.RedScreenOfDeath;

import org.conscrypt.Conscrypt;
import org.jellyfin.apiclient.interaction.AndroidDevice;
import org.jellyfin.apiclient.interaction.ApiClient;
import org.jellyfin.apiclient.interaction.VolleyHttpClient;
import org.jellyfin.apiclient.interaction.device.IDevice;
import org.jellyfin.apiclient.interaction.http.IAsyncHttpClient;
import org.jellyfin.apiclient.logging.AndroidLogger;
import org.jellyfin.apiclient.logging.ILogger;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import java.io.InputStream;
import okhttp3.OkHttpClient;

import java.security.Security;

public class App extends MultiDexApplication {
    private static App app;

    private static JellyDatabase database;
    private static ApiClient apiClient;

    @Override
    public void onCreate() {
        // Initializing stuff for older Android APIs compatibility
        Security.insertProviderAt(Conscrypt.newProvider(), 1); // To have SSL 1.2 on API < 19
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); // To load vectors on API < 19

        super.onCreate();

        initializeGlide(this);

        if (BuildConfig.DEBUG) {
            RedScreenOfDeath.init(this);
        }

        app = this;
        database = createDatabase(this);
        apiClient = createApiClient(this);

        if (database.userDao().getUsers().isEmpty()) {
            PreferenceUtil.getInstance(this).setServer(null);
            PreferenceUtil.getInstance(this).setUser(null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new DynamicShortcutManager(this).initDynamicShortcuts();
        }
    }

    public static JellyDatabase createDatabase(Context context) {
        return Room.databaseBuilder(context, JellyDatabase.class, "database")
                .allowMainThreadQueries()
                .addMigrations(JellyDatabase.Migration2)
                .addMigrations(JellyDatabase.Migration3)
                .addMigrations(JellyDatabase.Migration4)
                .addMigrations(JellyDatabase.Migration5)
                .addMigrations(JellyDatabase.Migration6)
                .addMigrations(JellyDatabase.Migration7)
                .build();
    }

    public static ApiClient createApiClient(Context context) {
        String appName = context.getString(R.string.app_name);
        String appVersion = BuildConfig.VERSION_NAME;

        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceName = android.os.Build.MODEL;
        String server = PreferenceUtil.getInstance(context).getServer();

        ILogger logger = new AndroidLogger(context.getClass().getName());
        IAsyncHttpClient httpClient = new VolleyHttpClient(logger, context);
        IDevice device = new AndroidDevice(deviceId, deviceName);
        EventListener eventListener = new EventListener();


        return new ApiClient(httpClient, logger, server, appName, appVersion, device, eventListener);
    }

    public static JellyDatabase getDatabase() {
        return database;
    }

    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static App getInstance() {
        return app;
    }

    private void initializeGlide(@NonNull Context context) {
        // This OkHttpClient is now created with Conscrypt as the SSL provider.
        OkHttpClient client = new OkHttpClient.Builder().build();

        // Manually create and register Glide's OkHttp component.
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);

        // Ensure Glide is initialized and then register the component.
        Glide.get(context).getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }
}
