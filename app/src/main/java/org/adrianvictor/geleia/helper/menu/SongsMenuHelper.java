package org.adrianvictor.geleia.helper.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.dialogs.AddToPlaylistDialog;
import org.adrianvictor.geleia.helper.MusicPlayerRemote;
import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.util.NavigationUtil;

import java.util.List;

public class SongsMenuHelper {
    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull List<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_play:
                MusicPlayerRemote.openQueue(songs, 0, true);
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_queue:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_download:
                NavigationUtil.startDownload(activity, songs);
                return true;
        }

        return false;
    }
}
