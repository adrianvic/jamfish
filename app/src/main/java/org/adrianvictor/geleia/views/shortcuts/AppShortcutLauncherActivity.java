package org.adrianvictor.geleia.views.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.adrianvictor.geleia.BuildConfig;
import org.adrianvictor.geleia.views.shortcuts.type.LatestShortcutType;
import org.adrianvictor.geleia.views.shortcuts.type.ShuffleShortcutType;
import org.adrianvictor.geleia.views.shortcuts.type.FrequentShortcutType;
import org.adrianvictor.geleia.helper.MusicPlayerRemote;
import org.adrianvictor.geleia.util.ShortcutUtil;
import org.adrianvictor.geleia.model.Playlist;
import org.adrianvictor.geleia.service.MusicService;

public class AppShortcutLauncherActivity extends Activity {
    public static final String EXTRA_SHORTCUT = BuildConfig.APPLICATION_ID + ".extra.shortcut";

    public static final int SHORTCUT_TYPE_SHUFFLE = 0;
    public static final int SHORTCUT_TYPE_FREQUENT = 1;
    public static final int SHORTCUT_TYPE_LATEST = 2;
    public static final int SHORTCUT_TYPE_DEFAULT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getIntent().getIntExtra(EXTRA_SHORTCUT, SHORTCUT_TYPE_DEFAULT)) {
            case SHORTCUT_TYPE_SHUFFLE:
                ShortcutUtil.getShuffle((media) -> MusicPlayerRemote.openAndShuffleQueue(media, true), false);
                DynamicShortcutManager.reportShortcutUsed(this, ShuffleShortcutType.getId());
                break;
            case SHORTCUT_TYPE_FREQUENT:
                ShortcutUtil.getFrequent((media) -> MusicPlayerRemote.openAndShuffleQueue(media, true));
                DynamicShortcutManager.reportShortcutUsed(this, FrequentShortcutType.getId());
                break;
            case SHORTCUT_TYPE_LATEST:
                ShortcutUtil.getLatest((media) -> MusicPlayerRemote.openAndShuffleQueue(media, true));
                DynamicShortcutManager.reportShortcutUsed(this, LatestShortcutType.getId());
                break;
        }

        finish();
    }

    private void startServiceWithPlaylist(int shuffleMode, Playlist playlist) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY_PLAYLIST);

        Bundle bundle = new Bundle();
        bundle.putParcelable(MusicService.INTENT_EXTRA_PLAYLIST, playlist);
        bundle.putInt(MusicService.INTENT_EXTRA_SHUFFLE, shuffleMode);

        intent.putExtras(bundle);

        startService(intent);
    }
}
