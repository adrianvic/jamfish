package org.adrianvictor.geleia.fragments.library;

import androidx.annotation.NonNull;

import org.adrianvictor.geleia.adapter.song.ShuffleButtonSongAdapter;
import org.adrianvictor.geleia.adapter.song.SongAdapter;

import org.jellyfin.apiclient.model.querying.ItemFilter;
import org.jellyfin.apiclient.model.querying.ItemQuery;

public class FavoritesFragment extends SongsFragment {
    @NonNull
    @Override
    protected ItemQuery createQuery() {
        ItemQuery query = super.createQuery();

        // the only difference from the songs fragment is the favorite filter
        query.setFilters(new ItemFilter[]{ItemFilter.IsFavorite});

        return query;
    }

    @NonNull
    @Override
    protected SongAdapter createAdapter() {
        SongAdapter adapter = super.createAdapter();

        // set the shuffle button adapter to only shuffle favorites
        if (adapter instanceof ShuffleButtonSongAdapter) {
            ((ShuffleButtonSongAdapter) adapter).setFavorite(true);
        }

        return adapter;
    }
}
