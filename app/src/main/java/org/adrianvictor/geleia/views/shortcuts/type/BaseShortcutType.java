package org.adrianvictor.geleia.views.shortcuts.type;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.Build;
import android.os.Bundle;

import org.adrianvictor.geleia.BuildConfig;
import org.adrianvictor.geleia.views.shortcuts.AppShortcutLauncherActivity;

@TargetApi(Build.VERSION_CODES.O)
public abstract class BaseShortcutType {

    protected static final String PREFIX = BuildConfig.APPLICATION_ID + ".views.shortcut";

    protected final Context context;

    public BaseShortcutType(Context context) {
        this.context = context;
    }

    public static String getId() {
        return PREFIX + ".base";
    }

    abstract ShortcutInfo getShortcutInfo();

    Intent getPlaySongsIntent(int shortcutType) {
        Intent intent = new Intent(context, AppShortcutLauncherActivity.class);
        intent.setAction(Intent.ACTION_VIEW);

        Bundle bundle = new Bundle();
        bundle.putInt(AppShortcutLauncherActivity.EXTRA_SHORTCUT, shortcutType);

        intent.putExtras(bundle);

        return intent;
    }
}
