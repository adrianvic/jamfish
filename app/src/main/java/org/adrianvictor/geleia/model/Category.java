package org.adrianvictor.geleia.model;

import androidx.annotation.StringRes;

import org.adrianvictor.geleia.R;

public enum Category {
    SONGS(R.string.songs),
    ALBUMS(R.string.albums),
    ARTISTS(R.string.artists),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists),
    FAVORITES(R.string.favorites),
    DOWNLOADS(R.string.downloads);

    @StringRes
    public final int title;

    public boolean select;

    Category(int title) {
        this.title = title;
    }
}
