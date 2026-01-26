package org.adrianvictor.geleia.fragments.library;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;


import org.adrianvictor.geleia.adapter.DownloadsAdapter;

import org.adrianvictor.geleia.model.Song;
import org.adrianvictor.geleia.model.SortMethod;
import org.adrianvictor.geleia.model.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends AbsLibraryPagerRecyclerViewCustomGridSizeFragment<DownloadsAdapter, GridLayoutManager, Void> {
    @NonNull
    @Override
    protected DownloadsAdapter createAdapter() {
        return new DownloadsAdapter(getItemLayoutRes());
    }

    @NonNull
    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(), getGridSize());
    }

    @NonNull
    @Override
    protected Void createQuery() {
        return null;
    }

    @Override
    protected void loadItems(int index) {
        List<Song> dummySongs = new ArrayList<>();

        Song song1 = new Song();
        song1.title = "Dummy Song Title";
        song1.artistName = "A. Dummy Artist";

        dummySongs.add(song1);

        Song song2 = new Song();
        song2.title = "Another Test Song";
        song2.artistName = "The Testers";
        dummySongs.add(song2);

        getActivity().runOnUiThread(() -> {
            getAdapter().swapDataSet(dummySongs);
        });
    }

    @Override
    protected int loadGridSize() {
        return 1;
    }

    @Override
    protected void saveGridSize(int gridColumns) {

    }

    @Override
    protected int loadGridSizeLand() {
        return 1;
    }

    @Override
    protected void saveGridSizeLand(int gridColumns) {

    }

    @Override
    protected void saveUsePalette(boolean usePalette) {

    }

    @Override
    protected boolean loadUsePalette() {
        return false;
    }

    @Override
    protected void setUsePalette(boolean usePalette) {

    }

    @Override
    protected void setGridSize(int gridSize) {

    }

    @Override
    protected SortMethod loadSortMethod() {
        return SortMethod.ADDED;
    }

    @Override
    protected void saveSortMethod(SortMethod sortMethod) {

    }

    @Override
    protected void setSortMethod(SortMethod sortMethod) {

    }

    @Override
    protected SortOrder loadSortOrder() {
        return SortOrder.ASCENDING;
    }

    @Override
    protected void saveSortOrder(SortOrder sortOrder) {

    }

    @Override
    protected void setSortOrder(SortOrder sortOrder) {

    }
}
