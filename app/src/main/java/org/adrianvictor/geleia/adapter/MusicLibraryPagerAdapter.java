package org.adrianvictor.geleia.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.adrianvictor.geleia.activities.UnreachableActivity;
import org.adrianvictor.geleia.fragments.OfflineFragment;
import org.adrianvictor.geleia.fragments.library.DownloadsFragment;
import org.adrianvictor.geleia.fragments.library.FavoritesFragment;
import org.adrianvictor.geleia.model.Category;
import org.adrianvictor.geleia.fragments.library.AlbumsFragment;
import org.adrianvictor.geleia.fragments.library.ArtistsFragment;
import org.adrianvictor.geleia.fragments.library.GenresFragment;
import org.adrianvictor.geleia.fragments.library.PlaylistsFragment;
import org.adrianvictor.geleia.fragments.library.SongsFragment;
import org.adrianvictor.geleia.util.PreferenceUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MusicLibraryPagerAdapter extends FragmentPagerAdapter {

    private final SparseArray<WeakReference<Fragment>> mFragmentArray = new SparseArray<>();

    private final List<Holder> mHolderList = new ArrayList<>();

    private final Context mContext;

    public MusicLibraryPagerAdapter(@NonNull final Context context, final FragmentManager fragmentManager) {
        super(fragmentManager);

        mContext = context;
        setCategories(PreferenceUtil.getInstance(context).getCategories());
    }

    public void setCategories(@NonNull List<Category> categories) {
        List<Category> select = categories.stream().filter(category -> category.select).collect(Collectors.toList());
        mHolderList.clear();

        for (Category category : select) {
            MusicFragments fragment = MusicFragments.valueOf(category.toString());
            Holder holder = new Holder();

            holder.mClassName = fragment.getFragmentClass().getName();
            holder.title = mContext.getResources().getString(category.title).toUpperCase();

            mHolderList.add(holder);
        }

        alignCache();
        notifyDataSetChanged();
    }

    public Fragment getFragment(final int position) {
        final WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
        if (mWeakFragment != null && mWeakFragment.get() != null) {
            return mWeakFragment.get();
        }

        return getItem(position);
    }

    @Override
    public int getItemPosition(@NonNull Object fragment) {
        for (int i = 0, size = mHolderList.size(); i < size; i++) {
            Holder holder = mHolderList.get(i);
            if (holder.mClassName.equals(fragment.getClass().getName())) {
                return i;
            }
        }

        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        // as fragment position is not fixed, we can't use position as id
        return MusicFragments.of(getFragment(position).getClass()).ordinal();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final Fragment mFragment = (Fragment) super.instantiateItem(container, position);
        final WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
        if (mWeakFragment != null) {
            mWeakFragment.clear();
        }

        mFragmentArray.put(position, new WeakReference<>(mFragment));
        return mFragment;
    }

    @NotNull
    @Override
    public Fragment getItem(final int position) {
        final Holder mCurrentHolder = mHolderList.get(position);

        return Fragment.instantiate(mContext, mCurrentHolder.mClassName, mCurrentHolder.mParams);
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        super.destroyItem(container, position, object);

        final WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
        if (mWeakFragment != null) {
            mWeakFragment.clear();
        }
    }

    @Override
    public int getCount() {
        return mHolderList.size();
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(final int position) {
        return mHolderList.get(position).title;
    }

    private void alignCache() {
        if (mFragmentArray.size() == 0) return;

        HashMap<String, WeakReference<Fragment>> mappings = new HashMap<>(mFragmentArray.size());
        for (int i = 0, size = mFragmentArray.size(); i < size; i++) {
            WeakReference<Fragment> ref = mFragmentArray.valueAt(i);
            Fragment fragment = ref.get();
            if (fragment != null) {
                mappings.put(fragment.getClass().getName(), ref);
            }
        }

        for (int i = 0, size = mHolderList.size(); i < size; i++) {
            WeakReference<Fragment> ref = mappings.get(mHolderList.get(i).mClassName);
            if (ref != null) {
                mFragmentArray.put(i, ref);
            } else {
                mFragmentArray.remove(i);
            }
        }
    }

    public enum MusicFragments {
        SONGS(SongsFragment.class),
        ALBUMS(AlbumsFragment.class),
        ARTISTS(ArtistsFragment.class),
        GENRES(GenresFragment.class),
        PLAYLISTS(PlaylistsFragment.class),
        FAVORITES(FavoritesFragment.class),
        DOWNLOADS(DownloadsFragment.class);

        private final Class<? extends Fragment> mFragmentClass;

        MusicFragments(final Class<? extends Fragment> fragmentClass) {
            mFragmentClass = fragmentClass;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return mFragmentClass;
        }

        public static MusicFragments of(Class<?> cl) {
            MusicFragments[] fragments = All.FRAGMENTS;
            for (MusicFragments fragment : fragments) {
                if (cl.equals(fragment.mFragmentClass)) {
                    return fragment;
                }
            }

            throw new IllegalArgumentException(String.format("unknown music fragment: %s", cl));
        }

        private static class All {
            public static final MusicFragments[] FRAGMENTS = values();
        }
    }

    private final static class Holder {
        String mClassName;
        Bundle mParams;
        String title;
    }
}
