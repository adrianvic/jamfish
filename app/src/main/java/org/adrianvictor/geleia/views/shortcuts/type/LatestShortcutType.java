package org.adrianvictor.geleia.views.shortcuts.type;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.views.shortcuts.AppShortcutIconGenerator;
import org.adrianvictor.geleia.views.shortcuts.AppShortcutLauncherActivity;

@TargetApi(Build.VERSION_CODES.O)
public final class LatestShortcutType extends BaseShortcutType {
    public LatestShortcutType(Context context) {
        super(context);
    }

    public static String getId() {
        return PREFIX + ".latest";
    }

    @Override
    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(context, getId())
            .setShortLabel(context.getString(R.string.last_added))
            .setIcon(AppShortcutIconGenerator.generateThemedIcon(context, R.drawable.ic_app_shortcut_last_added))
            .setIntent(getPlaySongsIntent(AppShortcutLauncherActivity.SHORTCUT_TYPE_LATEST))
            .build();
    }
}
