package org.adrianvictor.geleia.activities.details;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialcab.attached.AttachedCab;
import com.afollestad.materialcab.attached.AttachedCabKt;
import org.adrianvictor.geleia.BuildConfig;
import org.adrianvictor.geleia.activities.base.AbsMusicContentActivity;
import org.adrianvictor.geleia.adapter.song.SongAdapter;
import org.adrianvictor.geleia.databinding.ActivityArtistDetailBinding;
import org.adrianvictor.geleia.util.NavigationUtil;
import org.adrianvictor.geleia.util.ThemeUtil;
import com.google.android.material.appbar.AppBarLayout;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.adapter.album.HorizontalAlbumAdapter;
import org.adrianvictor.geleia.dialogs.AddToPlaylistDialog;
import org.adrianvictor.geleia.glide.CustomGlideRequest;
import org.adrianvictor.geleia.glide.CustomPaletteTarget;
import org.adrianvictor.geleia.helper.MusicPlayerRemote;
import org.adrianvictor.geleia.interfaces.CabHolder;
import org.adrianvictor.geleia.interfaces.PaletteColorHolder;
import org.adrianvictor.geleia.model.Artist;
import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.util.MusicUtil;
import org.adrianvictor.geleia.util.PreferenceUtil;
import org.adrianvictor.geleia.util.QueryUtil;

import org.jellyfin.apiclient.model.querying.ItemQuery;

import java.util.List;

public class ArtistDetailActivity extends AbsMusicContentActivity implements PaletteColorHolder, CabHolder, AppBarLayout.OnOffsetChangedListener {
    public static final String EXTRA_ARTIST = BuildConfig.APPLICATION_ID + ".extra.artist";

    private ActivityArtistDetailBinding binding;

    private AttachedCab cab;
    private int headerViewHeight;
    private int toolbarColor;

    private Artist artist;
    private HorizontalAlbumAdapter albumAdapter;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        artist = getIntent().getParcelableExtra(EXTRA_ARTIST);

        super.onCreate(savedInstanceState);

        // must be loaded before album adapter
        usePalette = PreferenceUtil.getInstance(this).getAlbumArtistColoredFooters();

        setUpObservableListViewParams();
        setUpToolbar();
        setUpViews();

        loadArtistImage(artist);
        setArtist(artist);
    }

    @Override
    public void onStateOnline() {
        ItemQuery albums = new ItemQuery();
        albums.setArtistIds(new String[]{artist.id});

        QueryUtil.getAlbums(albums, media -> {
            artist.albums = media;
            setArtist(artist);
        });

        ItemQuery songs = new ItemQuery();
        songs.setArtistIds(new String[]{artist.id});

        QueryUtil.getSongs(songs, media -> {
            artist.songs = media;
            setArtist(artist);
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float headerAlpha = Math.max(0, Math.min(1, 1 + (2 * (float) verticalOffset / headerViewHeight)));
        binding.header.setAlpha(headerAlpha);
    }

    @Override
    protected View createContentView() {
        binding = ActivityArtistDetailBinding.inflate(getLayoutInflater());

        return wrapSlidingMusicPanel(binding.getRoot());
    }

    private boolean usePalette;

    private void setUpObservableListViewParams() {
        headerViewHeight = getResources().getDimensionPixelSize(R.dimen.detail_header_height);
    }

    private void setUpViews() {
        setUpSongListView();
        setUpAlbumRecyclerView();
        setColors(ThemeUtil.getColorResource(this, R.attr.defaultFooterColor));
    }

    private void setUpSongListView() {
        binding.appBarLayout.addOnOffsetChangedListener(this);

        songAdapter = new SongAdapter(this, artist.songs, R.layout.item_list, false, this);

        binding.songs.setLayoutManager(new GridLayoutManager(this, 1));
        binding.songs.setAdapter(songAdapter);

        binding.scrollView.setRecyclerView(binding.songs);
    }

    private void setUpAlbumRecyclerView() {
        binding.albums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        albumAdapter = new HorizontalAlbumAdapter(this, artist.albums, usePalette, this);
        binding.albums.setAdapter(albumAdapter);

        // NestedScrollView will ignore horizontal RecyclerView without this line
        binding.albums.setNestedScrollingEnabled(false);

        albumAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (albumAdapter.getItemCount() == 0) finish();
            }
        });
    }

    protected void setUsePalette(boolean usePalette) {
        albumAdapter.usePalette(usePalette);
        PreferenceUtil.getInstance(this).setAlbumArtistColoredFooters(usePalette);
        this.usePalette = usePalette;
    }

    private void loadArtistImage(Artist artist) {
        CustomGlideRequest.Builder
                .from(this, artist.primary, artist.blurHash)
                .palette().build().dontAnimate()
                .into(new CustomPaletteTarget(binding.image) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
    }

    @Override
    public int getPaletteColor() {
        return toolbarColor;
    }

    private void setColors(int color) {
        toolbarColor = color;
        binding.appBarLayout.setBackgroundColor(color);

        setColor(color);

        binding.toolbar.setBackgroundColor(color);
        // needed to auto readjust the toolbar content color
        setSupportActionBar(binding.toolbar);

        int secondaryTextColor = ThemeUtil.getSecondaryTextColor(this, color);
        binding.durationIcon.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        binding.songCountIcon.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        binding.albumCountIcon.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);

        binding.durationText.setTextColor(secondaryTextColor);
        binding.songCountText.setTextColor(secondaryTextColor);
        binding.albumCountText.setTextColor(secondaryTextColor);
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_artist, menu);
        menu.findItem(R.id.action_colored_footers).setChecked(usePalette);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final List<Song> songs = songAdapter.getDataSet();
        switch (id) {
            case R.id.action_shuffle_artist:
                MusicPlayerRemote.openAndShuffleQueue(songs, true);
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_queue:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_download:
                NavigationUtil.startDownload(this, songs);
                return true;
            case R.id.action_colored_footers:
                item.setChecked(!item.isChecked());
                setUsePalette(item.isChecked());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateCab(AttachedCab cab) {
        cab.backgroundColor(null, getPaletteColor());

        this.cab = cab;
    }

    @Override
    public void onBackPressed() {
        if (cab != null && AttachedCabKt.isActive(cab)) {
            AttachedCabKt.destroy(cab);
        } else {
            binding.albums.stopScroll();
            super.onBackPressed();
        }
    }

    @Override
    public void setStatusBarColor(int color) {
        super.setStatusBarColor(color);

        // the toolbar is always light at the moment
        setLightStatusBar(false);
    }

    private void setArtist(Artist artist) {
        this.artist = artist;

        binding.toolbar.setTitle(artist.name);
        binding.songCountText.setText(MusicUtil.getSongCountString(this, artist.songs.size()));
        binding.albumCountText.setText(MusicUtil.getAlbumCountString(this, artist.albums.size()));
        binding.durationText.setText(MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(this, artist.songs)));

        if (artist.songs.size() != 0) songAdapter.swapDataSet(artist.songs);
        if (artist.albums.size() != 0) albumAdapter.swapDataSet(artist.albums);
    }
}
