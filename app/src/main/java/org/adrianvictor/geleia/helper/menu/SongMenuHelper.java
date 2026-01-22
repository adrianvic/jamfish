package org.adrianvictor.geleia.helper.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.dialogs.AddToPlaylistDialog;
import org.adrianvictor.geleia.dialogs.SongDetailDialog;
import org.adrianvictor.geleia.dialogs.SongShareDialog;
import org.adrianvictor.geleia.helper.MusicPlayerRemote;
import org.adrianvictor.geleia.model.Album;
import org.adrianvictor.geleia.model.Artist;
import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.util.NavigationUtil;

import java.util.Collections;

public class SongMenuHelper {
    public static final int MENU_RES = R.menu.menu_item_song;

    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull Song song, int menuItemId) {
        if (menuItemId == R.id.action_share) {
            SongShareDialog.create(song).show(activity.getSupportFragmentManager(), SongShareDialog.TAG);
            return true;
        } else if (menuItemId == R.id.action_add_to_playlist) {
            AddToPlaylistDialog.create(song).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
            return true;
        } else if (menuItemId == R.id.action_play_next) {
            MusicPlayerRemote.playNext(song);
            return true;
        } else if (menuItemId == R.id.action_add_to_queue) {
            MusicPlayerRemote.enqueue(song);
            return true;
        } else if (menuItemId == R.id.action_details) {
            SongDetailDialog.create(song).show(activity.getSupportFragmentManager(), SongDetailDialog.TAG);
            return true;
        } else if (menuItemId == R.id.action_download) {
            NavigationUtil.startDownload(activity, Collections.singletonList(song));
            return true;
        } else if (menuItemId == R.id.action_go_to_album) {
            NavigationUtil.startAlbum(activity, new Album(song), null);
            return true;
        } else if (menuItemId == R.id.action_go_to_artist) {
            NavigationUtil.startArtist(activity, new Artist(song), null);
            return true;
        }

        return false;
    }

    public static abstract class OnClickSongMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final AppCompatActivity activity;

        public OnClickSongMenu(@NonNull AppCompatActivity activity) {
            this.activity = activity;
        }

        public int getMenuRes() {
            return MENU_RES;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(activity, v);

            popupMenu.inflate(getMenuRes());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuClick(activity, getSong(), item.getItemId());
        }

        public abstract Song getSong();
    }
}
